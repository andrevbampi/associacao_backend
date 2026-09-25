package com.projeto.associacao.service;

import java.math.BigDecimal;
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
import com.projeto.associacao.model.ItemComanda;
import com.projeto.associacao.model.Pessoa;
import com.projeto.associacao.model.Produto;
import com.projeto.associacao.model.StatusComanda;
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

	public Iterable<ComandaResponse> selecionar(String status) throws BusinessRuleException {
		Iterable<Comanda> comandas;
		if ((status == null) || status.isBlank()) {
			comandas = repository.findAllByOrderByDataAberturaDesc();
		} else {
			comandas = repository.findByStatusOrderByDataAberturaDesc(converterStatus(status));
		}

		List<ComandaResponse> responses = new ArrayList<>();
		for (Comanda comanda : comandas) {
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

	public ComandaResponse fechar(int idComanda, ComandaFechamentoRequest request) throws BusinessRuleException {
		Comanda comanda = buscarComandaAbertaOuFalhar(idComanda);

		comanda.setStatus(StatusComanda.FECHADA);
		comanda.setDataFechamento(LocalDateTime.now());
		comanda.setPago(request.isPago());
		comanda.setDataPagamento(request.isPago() ? LocalDateTime.now() : null);

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
