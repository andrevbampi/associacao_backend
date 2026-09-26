package com.projeto.associacao.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projeto.associacao.dto.financeiro.LancamentoFinanceiroRequest;
import com.projeto.associacao.dto.financeiro.LancamentoFinanceiroResponse;
import com.projeto.associacao.dto.financeiro.ResumoFinanceiroResponse;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.CategoriaFinanceira;
import com.projeto.associacao.model.Comanda;
import com.projeto.associacao.model.FormaPagamento;
import com.projeto.associacao.model.LancamentoFinanceiro;
import com.projeto.associacao.model.Membro;
import com.projeto.associacao.model.Pessoa;
import com.projeto.associacao.model.TipoCategoriaFinanceira;
import com.projeto.associacao.model.TipoLancamento;
import com.projeto.associacao.model.Usuario;
import com.projeto.associacao.repository.CategoriaFinanceiraRepository;
import com.projeto.associacao.repository.ComandaRepository;
import com.projeto.associacao.repository.LancamentoFinanceiroRepository;
import com.projeto.associacao.repository.MembroRepository;
import com.projeto.associacao.repository.PessoaRepository;
import com.projeto.associacao.repository.UsuarioRepository;

@Service
public class LancamentoFinanceiroService {

	private static final String MARCADOR_AUTOMATICO_COMANDA = "[AUTO-COMANDA]";

	@Autowired
	private LancamentoFinanceiroRepository repository;

	@Autowired
	private CategoriaFinanceiraRepository categoriaRepository;

	@Autowired
	private PessoaRepository pessoaRepository;

	@Autowired
	private MembroRepository membroRepository;

	@Autowired
	private ComandaRepository comandaRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private CategoriaFinanceiraService categoriaFinanceiraService;

	public Iterable<LancamentoFinanceiroResponse> selecionar(LocalDate dataInicio, LocalDate dataFim,
			Integer idCategoriaFinanceira, String tipo, Boolean pago) throws BusinessRuleException {
		List<LancamentoFinanceiroResponse> responses = new ArrayList<>();
		for (LancamentoFinanceiro lancamento : repository.findAllByOrderByDataDesc()) {
			if ((dataInicio != null) && lancamento.getData().isBefore(dataInicio)) {
				continue;
			}
			if ((dataFim != null) && lancamento.getData().isAfter(dataFim)) {
				continue;
			}
			if ((idCategoriaFinanceira != null) && (lancamento.getCategoriaFinanceira().getId() != idCategoriaFinanceira)) {
				continue;
			}
			if ((tipo != null) && !tipo.isBlank() && (lancamento.getTipo() != converterTipo(tipo))) {
				continue;
			}
			if ((pago != null) && (lancamento.isPago() != pago)) {
				continue;
			}
			responses.add(converterParaResponse(lancamento));
		}
		return responses;
	}

	public ResumoFinanceiroResponse resumo(LocalDate dataInicio, LocalDate dataFim) {
		BigDecimal saldoAtual = BigDecimal.ZERO;
		BigDecimal totalEntradasPeriodo = BigDecimal.ZERO;
		BigDecimal totalSaidasPeriodo = BigDecimal.ZERO;

		for (LancamentoFinanceiro lancamento : repository.findAll()) {
			if (!lancamento.isPago()) {
				continue;
			}

			BigDecimal sinal = (lancamento.getTipo() == TipoLancamento.ENTRADA) ? lancamento.getValor() : lancamento.getValor().negate();
			saldoAtual = saldoAtual.add(sinal);

			boolean dentroDoPeriodo = ((dataInicio == null) || !lancamento.getData().isBefore(dataInicio))
					&& ((dataFim == null) || !lancamento.getData().isAfter(dataFim));
			if (dentroDoPeriodo) {
				if (lancamento.getTipo() == TipoLancamento.ENTRADA) {
					totalEntradasPeriodo = totalEntradasPeriodo.add(lancamento.getValor());
				} else {
					totalSaidasPeriodo = totalSaidasPeriodo.add(lancamento.getValor());
				}
			}
		}

		ResumoFinanceiroResponse resumo = new ResumoFinanceiroResponse();
		resumo.setSaldoAtual(saldoAtual);
		resumo.setTotalEntradasPeriodo(totalEntradasPeriodo);
		resumo.setTotalSaidasPeriodo(totalSaidasPeriodo);
		return resumo;
	}

	// loginUsuarioAutenticado vem do token JWT, nunca do corpo da requisição.
	public LancamentoFinanceiroResponse cadastrar(LancamentoFinanceiroRequest request, String loginUsuarioAutenticado) throws BusinessRuleException {
		LancamentoFinanceiro lancamento = this.validarLancamento(request, false, loginUsuarioAutenticado);
		return converterParaResponse(repository.save(lancamento));
	}

	public LancamentoFinanceiroResponse alterar(LancamentoFinanceiroRequest request, String loginUsuarioAutenticado) throws BusinessRuleException {
		LancamentoFinanceiro lancamento = this.validarLancamento(request, true, loginUsuarioAutenticado);
		return converterParaResponse(repository.save(lancamento));
	}

	public LancamentoFinanceiroResponse registrarPagamento(int id, String formaPagamento) throws BusinessRuleException {
		LancamentoFinanceiro lancamento = buscarOuFalhar(id);
		if (lancamento.isPago()) {
			throw new BusinessRuleException("Esse lançamento já está pago.");
		}

		lancamento.setPago(true);
		lancamento.setDataPagamento(LocalDateTime.now());
		if ((formaPagamento != null) && !formaPagamento.isBlank()) {
			lancamento.setFormaPagamento(converterFormaPagamento(formaPagamento));
		}
		return converterParaResponse(repository.save(lancamento));
	}

	public void remover(int id) throws BusinessRuleException {
		buscarOuFalhar(id);
		repository.deleteById(id);
	}

	// Usado pela ComandaService ao registrar o pagamento de uma comanda: gera a
	// receita correspondente, já paga, na categoria financeira "Venda de Produtos".
	public void gerarReceitaComanda(Comanda comanda, BigDecimal valor, FormaPagamento formaPagamento, String loginUsuarioAutenticado) throws BusinessRuleException {
		Usuario usuario = usuarioRepository.findByLogin(loginUsuarioAutenticado);
		if (usuario == null) {
			throw new BusinessRuleException("Usuário autenticado não encontrado.");
		}

		LancamentoFinanceiro lancamento = new LancamentoFinanceiro();
		lancamento.setCategoriaFinanceira(categoriaFinanceiraService.buscarOuCriarCategoriaVendaDeProdutos());
		lancamento.setTipo(TipoLancamento.ENTRADA);
		lancamento.setValor(valor);
		lancamento.setData(LocalDate.now());
		lancamento.setDescricao("Venda de produtos - comanda #" + comanda.getId());
		lancamento.setComanda(comanda);
		if (comanda.getPessoa() != null) {
			lancamento.setPessoa(comanda.getPessoa());
			Membro membro = membroRepository.findByPessoa_Id(comanda.getPessoa().getId());
			if (membro != null) {
				lancamento.setMembro(membro);
			}
		}
		lancamento.setUsuario(usuario);
		lancamento.setObservacao(MARCADOR_AUTOMATICO_COMANDA + " Gerado automaticamente ao registrar o pagamento da comanda #" + comanda.getId() + ".");
		lancamento.setPago(true);
		lancamento.setDataPagamento(LocalDateTime.now());
		lancamento.setFormaPagamento(formaPagamento);
		repository.save(lancamento);
	}

	// Remove os lançamentos gerados automaticamente para essa comanda — usado ao
	// desfazer o pagamento (ou o fechamento pago) da comanda.
	public void estornarPorComanda(int idComanda) {
		for (LancamentoFinanceiro lancamento : repository.findByComanda_Id(idComanda)) {
			if ((lancamento.getObservacao() != null) && lancamento.getObservacao().startsWith(MARCADOR_AUTOMATICO_COMANDA)) {
				repository.deleteById(lancamento.getId());
			}
		}
	}

	private LancamentoFinanceiro buscarOuFalhar(int id) throws BusinessRuleException {
		if (id == 0) {
			throw new BusinessRuleException("ID não informado.");
		}

		LancamentoFinanceiro lancamento = repository.findById(id);
		if (lancamento == null) {
			throw new BusinessRuleException("Lançamento financeiro de ID " + id + " não cadastrado.");
		}
		return lancamento;
	}

	private LancamentoFinanceiro validarLancamento(LancamentoFinanceiroRequest request, boolean edicao, String loginUsuarioAutenticado) throws BusinessRuleException {
		if (request.getIdCategoriaFinanceira() == 0) {
			throw new BusinessRuleException("Categoria financeira não informada.");
		}

		CategoriaFinanceira categoria = categoriaRepository.findById(request.getIdCategoriaFinanceira());
		if (categoria == null) {
			throw new BusinessRuleException("Não existe categoria financeira cadastrada com o ID " + request.getIdCategoriaFinanceira());
		}

		TipoLancamento tipo = converterTipo(request.getTipo());
		boolean tipoCompativel = ((categoria.getTipo() == TipoCategoriaFinanceira.RECEITA) && (tipo == TipoLancamento.ENTRADA))
				|| ((categoria.getTipo() == TipoCategoriaFinanceira.DESPESA) && (tipo == TipoLancamento.SAIDA));
		if (!tipoCompativel) {
			throw new BusinessRuleException("A categoria \"" + categoria.getDescricao() + "\" é de " + categoria.getTipo()
					+ " e não pode ser usada em um lançamento de " + tipo + ".");
		}

		if ((request.getValor() == null) || (request.getValor().compareTo(BigDecimal.ZERO) <= 0)) {
			throw new BusinessRuleException("Valor inválido.");
		}

		if (request.getData() == null) {
			throw new BusinessRuleException("Data não informada.");
		}

		LancamentoFinanceiro lancamento;
		if (edicao) {
			if (request.getId() == 0) {
				throw new BusinessRuleException("ID não informado.");
			}
			lancamento = repository.findById(request.getId());
			if (lancamento == null) {
				throw new BusinessRuleException("Lançamento financeiro de ID " + request.getId() + " não cadastrado.");
			}
		} else {
			lancamento = new LancamentoFinanceiro();
			Usuario usuario = usuarioRepository.findByLogin(loginUsuarioAutenticado);
			if (usuario == null) {
				throw new BusinessRuleException("Usuário autenticado não encontrado.");
			}
			lancamento.setUsuario(usuario);
		}

		if (request.getIdPessoa() != null) {
			Pessoa pessoa = pessoaRepository.findById(request.getIdPessoa().intValue());
			if (pessoa == null) {
				throw new BusinessRuleException("Não existe pessoa cadastrada com o ID " + request.getIdPessoa());
			}
			lancamento.setPessoa(pessoa);
		} else {
			lancamento.setPessoa(null);
		}

		if (request.getIdMembro() != null) {
			Membro membro = membroRepository.findById(request.getIdMembro().intValue());
			if (membro == null) {
				throw new BusinessRuleException("Não existe membro cadastrado com o ID " + request.getIdMembro());
			}
			lancamento.setMembro(membro);
		} else {
			lancamento.setMembro(null);
		}

		if (request.getIdComanda() != null) {
			Comanda comanda = comandaRepository.findById(request.getIdComanda().intValue());
			if (comanda == null) {
				throw new BusinessRuleException("Não existe comanda cadastrada com o ID " + request.getIdComanda());
			}
			lancamento.setComanda(comanda);
		} else {
			lancamento.setComanda(null);
		}

		lancamento.setCategoriaFinanceira(categoria);
		lancamento.setTipo(tipo);
		lancamento.setValor(request.getValor());
		lancamento.setData(request.getData());
		lancamento.setDescricao(request.getDescricao());
		lancamento.setObservacao(request.getObservacao());
		lancamento.setPago(request.isPago());
		if (request.isPago()) {
			if (lancamento.getDataPagamento() == null) {
				lancamento.setDataPagamento(LocalDateTime.now());
			}
			if ((request.getFormaPagamento() != null) && !request.getFormaPagamento().isBlank()) {
				lancamento.setFormaPagamento(converterFormaPagamento(request.getFormaPagamento()));
			}
		} else {
			lancamento.setDataPagamento(null);
			lancamento.setFormaPagamento(null);
		}

		return lancamento;
	}

	private TipoLancamento converterTipo(String tipo) throws BusinessRuleException {
		try {
			return TipoLancamento.valueOf(tipo.trim().toUpperCase());
		} catch (IllegalArgumentException | NullPointerException ex) {
			throw new BusinessRuleException("Tipo \"" + tipo + "\" inválido. Use ENTRADA ou SAIDA.");
		}
	}

	private FormaPagamento converterFormaPagamento(String formaPagamento) throws BusinessRuleException {
		try {
			return FormaPagamento.valueOf(formaPagamento.trim().toUpperCase());
		} catch (IllegalArgumentException ex) {
			throw new BusinessRuleException("Forma de pagamento \"" + formaPagamento + "\" inválida.");
		}
	}

	private LancamentoFinanceiroResponse converterParaResponse(LancamentoFinanceiro lancamento) {
		LancamentoFinanceiroResponse response = new LancamentoFinanceiroResponse();
		response.setId(lancamento.getId());
		response.setCategoriaFinanceira(lancamento.getCategoriaFinanceira());
		response.setTipo(lancamento.getTipo());
		response.setValor(lancamento.getValor());
		response.setData(lancamento.getData());
		response.setDescricao(lancamento.getDescricao());
		response.setPessoa(lancamento.getPessoa());
		response.setMembro(lancamento.getMembro());
		response.setIdComanda(lancamento.getComanda() != null ? lancamento.getComanda().getId() : null);
		response.setUsuario(usuarioService.converterParaResponse(lancamento.getUsuario()));
		response.setObservacao(lancamento.getObservacao());
		response.setPago(lancamento.isPago());
		response.setDataPagamento(lancamento.getDataPagamento());
		response.setFormaPagamento(lancamento.getFormaPagamento());
		return response;
	}

}
