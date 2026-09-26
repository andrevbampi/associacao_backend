package com.projeto.associacao.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projeto.associacao.dto.estoque.MovimentoEstoqueRequest;
import com.projeto.associacao.dto.estoque.MovimentoEstoqueResponse;
import com.projeto.associacao.dto.financeiro.LancamentoFinanceiroRequest;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.MovimentoEstoque;
import com.projeto.associacao.model.OrigemMovimentoEstoque;
import com.projeto.associacao.model.Produto;
import com.projeto.associacao.model.TipoMovimentoEstoque;
import com.projeto.associacao.model.Usuario;
import com.projeto.associacao.repository.MovimentoEstoqueRepository;
import com.projeto.associacao.repository.ProdutoRepository;
import com.projeto.associacao.repository.UsuarioRepository;

@Service
public class EstoqueService {

	@Autowired
	private MovimentoEstoqueRepository repository;

	@Autowired
	private ProdutoRepository produtoRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private LancamentoFinanceiroService lancamentoFinanceiroService;

	@Autowired
	private UsuarioService usuarioService;

	public Iterable<MovimentoEstoqueResponse> selecionar(Integer idProduto, String tipo, String origem,
			LocalDate dataInicio, LocalDate dataFim) throws BusinessRuleException {
		List<MovimentoEstoqueResponse> responses = new ArrayList<>();
		for (MovimentoEstoque movimento : repository.findAllByOrderByDataHoraDesc()) {
			if ((idProduto != null) && (movimento.getProduto().getId() != idProduto)) {
				continue;
			}
			if ((tipo != null) && !tipo.isBlank() && (movimento.getTipo() != converterTipo(tipo))) {
				continue;
			}
			if ((origem != null) && !origem.isBlank() && (movimento.getOrigem() != converterOrigem(origem))) {
				continue;
			}
			if ((dataInicio != null) && movimento.getDataHora().toLocalDate().isBefore(dataInicio)) {
				continue;
			}
			if ((dataFim != null) && movimento.getDataHora().toLocalDate().isAfter(dataFim)) {
				continue;
			}
			responses.add(converterParaResponse(movimento));
		}
		return responses;
	}

	// loginUsuarioAutenticado vem do token JWT, nunca do corpo da requisição.
	public MovimentoEstoqueResponse lancar(MovimentoEstoqueRequest request, String loginUsuarioAutenticado) throws BusinessRuleException {
		Produto produto = produtoRepository.findById(request.getIdProduto());
		if (produto == null) {
			throw new BusinessRuleException("Não existe produto cadastrado com o ID " + request.getIdProduto());
		}

		if (!produto.isControlaEstoque()) {
			throw new BusinessRuleException("O produto \"" + produto.getDescricao() + "\" não controla estoque.");
		}

		if (request.getQuantidade() < 1) {
			throw new BusinessRuleException("Quantidade inválida.");
		}

		TipoMovimentoEstoque tipo = converterTipo(request.getTipo());
		OrigemMovimentoEstoque origem = (request.getOrigem() != null) && !request.getOrigem().isBlank()
				? converterOrigem(request.getOrigem())
				: OrigemMovimentoEstoque.AJUSTE_MANUAL;

		Usuario usuario = usuarioRepository.findByLogin(loginUsuarioAutenticado);
		if (usuario == null) {
			throw new BusinessRuleException("Usuário autenticado não encontrado.");
		}

		int estoqueAnterior = produto.getEstoqueAtual();
		int estoquePosterior = aplicarMovimento(estoqueAnterior, tipo, request.getQuantidade());
		if (estoquePosterior < 0) {
			throw new BusinessRuleException("Estoque insuficiente para essa saída. Estoque atual: " + estoqueAnterior + ".");
		}

		MovimentoEstoque movimento = new MovimentoEstoque();
		movimento.setProduto(produto);
		movimento.setTipo(tipo);
		movimento.setQuantidade(request.getQuantidade());
		movimento.setEstoqueAnterior(estoqueAnterior);
		movimento.setEstoquePosterior(estoquePosterior);
		movimento.setDataHora(LocalDateTime.now());
		movimento.setUsuario(usuario);
		movimento.setObservacao(request.getObservacao());
		movimento.setOrigem(origem);
		repository.save(movimento);

		produto.setEstoqueAtual(estoquePosterior);
		produtoRepository.save(produto);

		if (request.isGerarLancamentoFinanceiro()) {
			if (tipo != TipoMovimentoEstoque.ENTRADA) {
				throw new BusinessRuleException("Só é possível gerar lançamento financeiro automático para movimentos de entrada.");
			}
			LancamentoFinanceiroRequest lancamento = new LancamentoFinanceiroRequest();
			lancamento.setIdCategoriaFinanceira(request.getIdCategoriaFinanceira());
			lancamento.setTipo("SAIDA");
			lancamento.setValor(request.getValorLancamento());
			lancamento.setData(LocalDate.now());
			lancamento.setDescricao("Compra de estoque: " + produto.getDescricao() + " (x" + request.getQuantidade() + ")");
			lancamento.setPago(true);
			lancamentoFinanceiroService.cadastrar(lancamento, loginUsuarioAutenticado);
		}

		return converterParaResponse(movimento);
	}

	// Usado pela ComandaService ao registrar o pagamento de uma comanda: gera a
	// saída de estoque de cada item vendido. Produtos que não controlam estoque
	// são ignorados (não geram movimento).
	public void registrarSaidaPorVenda(int idProduto, int quantidade, int idComanda, String loginUsuarioAutenticado) throws BusinessRuleException {
		Produto produto = produtoRepository.findById(idProduto);
		if ((produto == null) || !produto.isControlaEstoque()) {
			return;
		}

		Usuario usuario = usuarioRepository.findByLogin(loginUsuarioAutenticado);
		if (usuario == null) {
			throw new BusinessRuleException("Usuário autenticado não encontrado.");
		}

		int estoqueAnterior = produto.getEstoqueAtual();
		int estoquePosterior = estoqueAnterior - quantidade;

		MovimentoEstoque movimento = new MovimentoEstoque();
		movimento.setProduto(produto);
		movimento.setTipo(TipoMovimentoEstoque.SAIDA);
		movimento.setQuantidade(quantidade);
		movimento.setEstoqueAnterior(estoqueAnterior);
		movimento.setEstoquePosterior(estoquePosterior);
		movimento.setDataHora(LocalDateTime.now());
		movimento.setUsuario(usuario);
		movimento.setObservacao("Saída automática pela venda na comanda #" + idComanda + ".");
		movimento.setOrigem(OrigemMovimentoEstoque.VENDA);
		movimento.setIdOrigem(idComanda);
		repository.save(movimento);

		produto.setEstoqueAtual(estoquePosterior);
		produtoRepository.save(produto);
	}

	// Estorna (gera um movimento de entrada compensatório) todas as saídas de
	// estoque geradas automaticamente para a origem/idOrigem informados — usado
	// ao desfazer o pagamento (ou o fechamento pago) de uma comanda.
	public void estornarMovimentosDeOrigem(OrigemMovimentoEstoque origem, int idOrigem, String loginUsuarioAutenticado) throws BusinessRuleException {
		Usuario usuario = usuarioRepository.findByLogin(loginUsuarioAutenticado);
		if (usuario == null) {
			throw new BusinessRuleException("Usuário autenticado não encontrado.");
		}

		for (MovimentoEstoque original : repository.findByOrigemAndIdOrigemAndEstornadoFalse(origem, idOrigem)) {
			original.setEstornado(true);
			repository.save(original);

			Produto produto = original.getProduto();
			int estoqueAnterior = produto.getEstoqueAtual();
			int estoquePosterior = estoqueAnterior + original.getQuantidade();

			MovimentoEstoque estorno = new MovimentoEstoque();
			estorno.setProduto(produto);
			estorno.setTipo(TipoMovimentoEstoque.ENTRADA);
			estorno.setQuantidade(original.getQuantidade());
			estorno.setEstoqueAnterior(estoqueAnterior);
			estorno.setEstoquePosterior(estoquePosterior);
			estorno.setDataHora(LocalDateTime.now());
			estorno.setUsuario(usuario);
			estorno.setObservacao("Estorno do movimento #" + original.getId() + " (origem " + origem + " #" + idOrigem + ").");
			estorno.setOrigem(OrigemMovimentoEstoque.OUTRO);
			estorno.setIdOrigem(idOrigem);
			repository.save(estorno);

			produto.setEstoqueAtual(estoquePosterior);
			produtoRepository.save(produto);
		}
	}

	private int aplicarMovimento(int estoqueAtual, TipoMovimentoEstoque tipo, int quantidade) {
		return switch (tipo) {
			case ENTRADA -> estoqueAtual + quantidade;
			case SAIDA -> estoqueAtual - quantidade;
			case AJUSTE -> quantidade;
		};
	}

	private TipoMovimentoEstoque converterTipo(String tipo) throws BusinessRuleException {
		try {
			return TipoMovimentoEstoque.valueOf(tipo.trim().toUpperCase());
		} catch (IllegalArgumentException | NullPointerException ex) {
			throw new BusinessRuleException("Tipo de movimento \"" + tipo + "\" inválido. Use ENTRADA, SAIDA ou AJUSTE.");
		}
	}

	private OrigemMovimentoEstoque converterOrigem(String origem) throws BusinessRuleException {
		try {
			return OrigemMovimentoEstoque.valueOf(origem.trim().toUpperCase());
		} catch (IllegalArgumentException | NullPointerException ex) {
			throw new BusinessRuleException("Origem \"" + origem + "\" inválida.");
		}
	}

	private MovimentoEstoqueResponse converterParaResponse(MovimentoEstoque movimento) {
		MovimentoEstoqueResponse response = new MovimentoEstoqueResponse();
		response.setId(movimento.getId());
		response.setProduto(produtoService.converterParaResponse(movimento.getProduto()));
		response.setTipo(movimento.getTipo());
		response.setQuantidade(movimento.getQuantidade());
		response.setEstoqueAnterior(movimento.getEstoqueAnterior());
		response.setEstoquePosterior(movimento.getEstoquePosterior());
		response.setDataHora(movimento.getDataHora());
		response.setUsuario(usuarioService.converterParaResponse(movimento.getUsuario()));
		response.setObservacao(movimento.getObservacao());
		response.setOrigem(movimento.getOrigem());
		response.setIdOrigem(movimento.getIdOrigem());
		return response;
	}

}
