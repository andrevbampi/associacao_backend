package com.projeto.associacao.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projeto.associacao.dto.comanda.ComandaAberturaRequest;
import com.projeto.associacao.dto.comanda.ComandaFechamentoRequest;
import com.projeto.associacao.dto.comanda.ComandaResponse;
import com.projeto.associacao.dto.comanda.ItemComandaRequest;
import com.projeto.associacao.dto.comanda.ItemComandaResponse;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.Comanda;
import com.projeto.associacao.model.FormaPagamento;
import com.projeto.associacao.model.ItemComanda;
import com.projeto.associacao.model.OrigemMovimentoEstoque;
import com.projeto.associacao.model.Pessoa;
import com.projeto.associacao.model.Produto;
import com.projeto.associacao.model.StatusComanda;
import com.projeto.associacao.repository.CaixaRepository;
import com.projeto.associacao.repository.ComandaRepository;
import com.projeto.associacao.repository.ItemComandaRepository;
import com.projeto.associacao.repository.MembroRepository;
import com.projeto.associacao.repository.PessoaRepository;
import com.projeto.associacao.repository.ProdutoRepository;

@Service
public class ComandaService {

	@Autowired
	private ComandaRepository repository;

	@Autowired
	private ItemComandaRepository itemRepository;

	@Autowired
	private PessoaRepository pessoaRepository;

	@Autowired
	private ProdutoRepository produtoRepository;

	@Autowired
	private MembroRepository membroRepository;

	@Autowired
	private CaixaRepository caixaRepository;

	@Autowired
	private EstoqueService estoqueService;

	@Autowired
	private LancamentoFinanceiroService lancamentoFinanceiroService;

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	public Iterable<ComandaResponse> selecionar(String status, Integer idPessoa, String nomeTemporario, LocalDate dataAbertura,
			Boolean pago, LocalDate dataPagamento) throws BusinessRuleException {
		Iterable<Comanda> comandas;
		if ((status == null) || status.isBlank()) {
			comandas = repository.findAllByOrderByDataAberturaDesc();
		} else {
			comandas = repository.findByStatusOrderByDataAberturaDesc(converterStatus(status));
		}

		List<ComandaResponse> responses = new ArrayList<>();
		for (Comanda comanda : comandas) {
			if ((idPessoa != null) && ((comanda.getPessoa() == null) || (comanda.getPessoa().getId() != idPessoa))) {
				continue;
			}
			if ((nomeTemporario != null) && !nomeTemporario.isBlank()
					&& ((comanda.getNomeTemporario() == null)
							|| !comanda.getNomeTemporario().toLowerCase().contains(nomeTemporario.trim().toLowerCase()))) {
				continue;
			}
			if ((dataAbertura != null) && !dataAbertura.equals(comanda.getDataAbertura().toLocalDate())) {
				continue;
			}
			if ((pago != null) && (comanda.isPago() != pago)) {
				continue;
			}
			if ((dataPagamento != null)
					&& ((comanda.getDataPagamento() == null) || !dataPagamento.equals(comanda.getDataPagamento().toLocalDate()))) {
				continue;
			}
			responses.add(converterParaResponse(comanda, false));
		}
		return responses;
	}

	public ComandaResponse buscarPorId(int id) throws BusinessRuleException {
		Comanda comanda = buscarComandaOuFalhar(id);
		return converterParaResponse(comanda, true);
	}

	public ComandaResponse abrir(ComandaAberturaRequest request) throws BusinessRuleException {
		boolean temPessoa = (request.getIdPessoa() != null) && (request.getIdPessoa() != 0);
		boolean temNomeTemporario = (request.getNomeTemporario() != null) && !request.getNomeTemporario().isBlank();

		if (!temPessoa && !temNomeTemporario) {
			throw new BusinessRuleException("Informe a pessoa ou um nome para identificar a comanda.");
		}

		Comanda comanda = new Comanda();

		if (temPessoa) {
			Pessoa pessoa = pessoaRepository.findById(request.getIdPessoa().intValue());
			if (pessoa == null) {
				throw new BusinessRuleException("Não existe pessoa cadastrada com o ID " + request.getIdPessoa());
			}
			comanda.setPessoa(pessoa);
		} else {
			comanda.setNomeTemporario(request.getNomeTemporario().trim());
		}

		comanda.setObservacao(request.getObservacao());
		comanda.setDataAbertura(LocalDateTime.now());
		comanda.setStatus(StatusComanda.ABERTA);
		comanda.setValorTotal(BigDecimal.ZERO);
		comanda.setPago(false);

		return converterParaResponse(repository.save(comanda), true);
	}

	public ComandaResponse adicionarItem(int idComanda, ItemComandaRequest request) throws BusinessRuleException {
		Comanda comanda = buscarComandaAbertaOuFalhar(idComanda);

		if (request.getQuantidade() < 1) {
			throw new BusinessRuleException("Quantidade inválida.");
		}

		Produto produto = produtoRepository.findById(request.getIdProduto());
		if (produto == null) {
			throw new BusinessRuleException("Não existe produto cadastrado com o ID " + request.getIdProduto());
		}
		if (!produto.isAtivo()) {
			throw new BusinessRuleException("O produto \"" + produto.getDescricao() + "\" está inativo.");
		}

		BigDecimal precoUnitario = precoParaComanda(comanda, produto);

		ItemComanda item = new ItemComanda();
		item.setComanda(comanda);
		item.setProduto(produto);
		item.setQuantidade(request.getQuantidade());
		item.setPrecoUnitario(precoUnitario);
		item.setSubtotal(precoUnitario.multiply(BigDecimal.valueOf(request.getQuantidade())));
		itemRepository.save(item);

		return recalcularERetornar(comanda);
	}

	public ComandaResponse alterarItem(int idComanda, int idItem, ItemComandaRequest request) throws BusinessRuleException {
		Comanda comanda = buscarComandaAbertaOuFalhar(idComanda);
		ItemComanda item = buscarItemDaComandaOuFalhar(comanda, idItem);

		if (request.getQuantidade() < 1) {
			throw new BusinessRuleException("Quantidade inválida.");
		}

		item.setQuantidade(request.getQuantidade());
		item.setSubtotal(item.getPrecoUnitario().multiply(BigDecimal.valueOf(request.getQuantidade())));
		itemRepository.save(item);

		return recalcularERetornar(comanda);
	}

	public ComandaResponse removerItem(int idComanda, int idItem) throws BusinessRuleException {
		Comanda comanda = buscarComandaAbertaOuFalhar(idComanda);
		ItemComanda item = buscarItemDaComandaOuFalhar(comanda, idItem);

		itemRepository.deleteById(item.getId());

		return recalcularERetornar(comanda);
	}

	// loginUsuarioAutenticado vem do token JWT, nunca do corpo da requisição.
	public ComandaResponse fechar(int idComanda, ComandaFechamentoRequest request, String loginUsuarioAutenticado) throws BusinessRuleException {
		Comanda comanda = buscarComandaAbertaOuFalhar(idComanda);

		comanda.setStatus(StatusComanda.FECHADA);
		comanda.setDataFechamento(LocalDateTime.now());

		if (request.isPago()) {
			int idCaixa = determinarCaixaComanda(request.getIdCaixa());
			efetivarPagamento(comanda, converterFormaPagamento(request.getFormaPagamento()), idCaixa, loginUsuarioAutenticado);
		}

		return converterParaResponse(repository.save(comanda), true);
	}

	public ComandaResponse registrarPagamento(int idComanda, String formaPagamento, Integer idCaixaRequest, String loginUsuarioAutenticado) throws BusinessRuleException {
		Comanda comanda = buscarComandaOuFalhar(idComanda);

		if (comanda.getStatus() != StatusComanda.FECHADA) {
			throw new BusinessRuleException("Só é possível registrar pagamento de uma comanda fechada.");
		}
		if (comanda.isPago()) {
			throw new BusinessRuleException("Essa comanda já está paga.");
		}

		int idCaixa = determinarCaixaComanda(idCaixaRequest);
		efetivarPagamento(comanda, converterFormaPagamento(formaPagamento), idCaixa, loginUsuarioAutenticado);

		return converterParaResponse(repository.save(comanda), true);
	}

	// Define qual caixa usar ao registrar o pagamento de uma comanda: se o
	// parâmetro de sistema CAIXA_COMANDA estiver configurado (e apontar para um
	// caixa que existe), ele é usado automaticamente; caso contrário, o caixa
	// informado na requisição é obrigatório.
	private int determinarCaixaComanda(Integer idCaixaRequest) throws BusinessRuleException {
		Integer idCaixaParametro = parametroSistemaService.buscarValorInteiro(ParametroSistemaService.CAIXA_COMANDA);
		if ((idCaixaParametro != null) && (caixaRepository.findById(idCaixaParametro.intValue()) != null)) {
			return idCaixaParametro;
		}

		if ((idCaixaRequest == null) || (idCaixaRequest == 0)) {
			throw new BusinessRuleException("Selecione o caixa para registrar o pagamento.");
		}
		if (caixaRepository.findById(idCaixaRequest.intValue()) == null) {
			throw new BusinessRuleException("Não existe caixa cadastrado com o ID " + idCaixaRequest);
		}
		return idCaixaRequest;
	}

	// Ao registrar o pagamento (seja no fechamento ou depois), gera
	// automaticamente a saída de estoque de cada item vendido e o lançamento
	// financeiro de receita correspondente — nunca calculado/aceito do cliente.
	private void efetivarPagamento(Comanda comanda, FormaPagamento formaPagamento, int idCaixa, String loginUsuarioAutenticado) throws BusinessRuleException {
		comanda.setPago(true);
		comanda.setDataPagamento(LocalDateTime.now());
		comanda.setFormaPagamento(formaPagamento);

		for (ItemComanda item : itemRepository.findByComanda_Id(comanda.getId())) {
			estoqueService.registrarSaidaPorVenda(item.getProduto().getId(), item.getQuantidade(), comanda.getId(), loginUsuarioAutenticado);
		}

		lancamentoFinanceiroService.gerarReceitaComanda(comanda, comanda.getValorTotal(), formaPagamento, idCaixa, loginUsuarioAutenticado);
	}

	// Reverte o pagamento de uma comanda: estorna os movimentos de estoque e o
	// lançamento financeiro gerados automaticamente por ele.
	public ComandaResponse desfazerPagamento(int idComanda, String loginUsuarioAutenticado) throws BusinessRuleException {
		Comanda comanda = buscarComandaOuFalhar(idComanda);

		if (!comanda.isPago()) {
			throw new BusinessRuleException("Essa comanda não está paga.");
		}

		desfazerEfeitosDoPagamento(comanda, loginUsuarioAutenticado);

		return converterParaResponse(repository.save(comanda), true);
	}

	private void desfazerEfeitosDoPagamento(Comanda comanda, String loginUsuarioAutenticado) throws BusinessRuleException {
		estoqueService.estornarMovimentosDeOrigem(OrigemMovimentoEstoque.VENDA, comanda.getId(), loginUsuarioAutenticado);
		lancamentoFinanceiroService.estornarPorComanda(comanda.getId());

		comanda.setPago(false);
		comanda.setDataPagamento(null);
		comanda.setFormaPagamento(null);
	}

	// Reabre uma comanda fechada. Se ela já estava paga, primeiro estorna os
	// efeitos do pagamento (estoque e financeiro) para não deixar rastro de uma
	// venda que, tecnicamente, deixou de existir.
	public ComandaResponse desfazerFechamento(int idComanda, String loginUsuarioAutenticado) throws BusinessRuleException {
		Comanda comanda = buscarComandaOuFalhar(idComanda);

		if (comanda.getStatus() != StatusComanda.FECHADA) {
			throw new BusinessRuleException("Só é possível desfazer o fechamento de uma comanda fechada.");
		}

		if (comanda.isPago()) {
			desfazerEfeitosDoPagamento(comanda, loginUsuarioAutenticado);
		}

		comanda.setStatus(StatusComanda.ABERTA);
		comanda.setDataFechamento(null);

		return converterParaResponse(repository.save(comanda), true);
	}

	public ComandaResponse cancelar(int idComanda) throws BusinessRuleException {
		Comanda comanda = buscarComandaAbertaOuFalhar(idComanda);

		comanda.setStatus(StatusComanda.CANCELADA);
		comanda.setDataFechamento(LocalDateTime.now());
		comanda.setPago(false);
		comanda.setDataPagamento(null);

		return converterParaResponse(repository.save(comanda), true);
	}

	private FormaPagamento converterFormaPagamento(String formaPagamento) throws BusinessRuleException {
		if ((formaPagamento == null) || formaPagamento.isBlank()) {
			return FormaPagamento.OUTRO;
		}
		try {
			return FormaPagamento.valueOf(formaPagamento.trim().toUpperCase());
		} catch (IllegalArgumentException ex) {
			throw new BusinessRuleException("Forma de pagamento \"" + formaPagamento + "\" inválida.");
		}
	}

	private BigDecimal precoParaComanda(Comanda comanda, Produto produto) {
		if (comanda.getPessoa() != null) {
			var membro = membroRepository.findByPessoa_Id(comanda.getPessoa().getId());
			if ((membro != null) && membro.isAtivo()) {
				return produto.getPrecoMembro();
			}
		}
		return produto.getPreco();
	}

	private ComandaResponse recalcularERetornar(Comanda comanda) {
		BigDecimal total = BigDecimal.ZERO;
		for (ItemComanda item : itemRepository.findByComanda_Id(comanda.getId())) {
			total = total.add(item.getSubtotal());
		}
		comanda.setValorTotal(total);
		repository.save(comanda);
		return converterParaResponse(comanda, true);
	}

	private Comanda buscarComandaOuFalhar(int id) throws BusinessRuleException {
		if (id == 0) {
			throw new BusinessRuleException("ID não informado.");
		}

		Comanda comanda = repository.findById(id);
		if (comanda == null) {
			throw new BusinessRuleException("Comanda de ID " + id + " não cadastrada.");
		}
		return comanda;
	}

	private Comanda buscarComandaAbertaOuFalhar(int id) throws BusinessRuleException {
		Comanda comanda = buscarComandaOuFalhar(id);
		if (comanda.getStatus() != StatusComanda.ABERTA) {
			throw new BusinessRuleException("A comanda de ID " + id + " não está aberta.");
		}
		return comanda;
	}

	private ItemComanda buscarItemDaComandaOuFalhar(Comanda comanda, int idItem) throws BusinessRuleException {
		ItemComanda item = itemRepository.findById(idItem);
		if ((item == null) || (item.getComanda().getId() != comanda.getId())) {
			throw new BusinessRuleException("Item de ID " + idItem + " não encontrado nessa comanda.");
		}
		return item;
	}

	private StatusComanda converterStatus(String status) throws BusinessRuleException {
		try {
			return StatusComanda.valueOf(status.trim().toUpperCase());
		} catch (IllegalArgumentException ex) {
			throw new BusinessRuleException("Status \"" + status + "\" inválido. Use ABERTA, FECHADA ou CANCELADA.");
		}
	}

	private ComandaResponse converterParaResponse(Comanda comanda, boolean incluirItens) {
		ComandaResponse response = new ComandaResponse();
		response.setId(comanda.getId());
		response.setPessoa(comanda.getPessoa());
		response.setNomeTemporario(comanda.getNomeTemporario());
		response.setDataAbertura(comanda.getDataAbertura());
		response.setDataFechamento(comanda.getDataFechamento());
		response.setStatus(comanda.getStatus());
		response.setValorTotal(comanda.getValorTotal());
		response.setPago(comanda.isPago());
		response.setDataPagamento(comanda.getDataPagamento());
		response.setFormaPagamento(comanda.getFormaPagamento());
		response.setObservacao(comanda.getObservacao());

		if (incluirItens) {
			List<ItemComandaResponse> itens = new ArrayList<>();
			for (ItemComanda item : itemRepository.findByComanda_Id(comanda.getId())) {
				ItemComandaResponse itemResponse = new ItemComandaResponse();
				itemResponse.setId(item.getId());
				itemResponse.setProduto(item.getProduto());
				itemResponse.setQuantidade(item.getQuantidade());
				itemResponse.setPrecoUnitario(item.getPrecoUnitario());
				itemResponse.setSubtotal(item.getSubtotal());
				itens.add(itemResponse);
			}
			response.setItens(itens);
		}

		return response;
	}
}
