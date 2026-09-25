package com.projeto.associacao.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projeto.associacao.dto.produto.ProdutoRequest;
import com.projeto.associacao.dto.produto.ProdutoResponse;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.CategoriaProduto;
import com.projeto.associacao.model.Produto;
import com.projeto.associacao.repository.CategoriaProdutoRepository;
import com.projeto.associacao.repository.ItemComandaRepository;
import com.projeto.associacao.repository.ProdutoRepository;

@Service
public class ProdutoService {

	@Autowired
	private ProdutoRepository repository;

	@Autowired
	private CategoriaProdutoRepository categoriaRepository;

	@Autowired
	private ItemComandaRepository itemComandaRepository;

	public Iterable<ProdutoResponse> selecionar(String descricao, Integer idCategoria, Boolean ativo) {
		List<ProdutoResponse> responses = new ArrayList<>();
		for (Produto produto : repository.findAll()) {
			if ((descricao != null) && !descricao.isBlank()
					&& !produto.getDescricao().toLowerCase().contains(descricao.trim().toLowerCase())) {
				continue;
			}
			if ((idCategoria != null) && (produto.getCategoria().getId() != idCategoria)) {
				continue;
			}
			if ((ativo != null) && (produto.isAtivo() != ativo)) {
				continue;
			}
			responses.add(converterParaResponse(produto));
		}
		return responses;
	}

	public ProdutoResponse cadastrar(ProdutoRequest request) throws BusinessRuleException {
		Produto produto = this.validarProduto(request, false);
		return converterParaResponse(repository.save(produto));
	}

	public ProdutoResponse alterar(ProdutoRequest request) throws BusinessRuleException {
		Produto produto = this.validarProduto(request, true);
		return converterParaResponse(repository.save(produto));
	}

	public void remover(int id) throws BusinessRuleException {
		if (id == 0) {
			throw new BusinessRuleException("ID não informado.");
		}

		if (repository.findById(id) == null) {
			throw new BusinessRuleException("Produto de ID " + id + " não cadastrado.");
		}

		if (itemComandaRepository.existsByProduto_Id(id)) {
			throw new BusinessRuleException("O produto de ID " + id + " já foi usado em alguma comanda e não pode ser excluído. Desative-o em vez de excluir.");
		}

		repository.deleteById(id);
	}

	private Produto validarProduto(ProdutoRequest request, boolean edicao) throws BusinessRuleException {
		if ((request.getDescricao() == null) || request.getDescricao().isBlank()) {
			throw new BusinessRuleException("Descrição não informada.");
		}

		if ((request.getPreco() == null) || (request.getPreco().compareTo(BigDecimal.ZERO) < 0)) {
			throw new BusinessRuleException("Preço inválido.");
		}

		if ((request.getPrecoMembro() == null) || (request.getPrecoMembro().compareTo(BigDecimal.ZERO) < 0)) {
			throw new BusinessRuleException("Preço para membro inválido.");
		}

		if (request.getIdCategoria() == 0) {
			throw new BusinessRuleException("Categoria não informada.");
		}

		CategoriaProduto categoria = categoriaRepository.findById(request.getIdCategoria());
		if (categoria == null) {
			throw new BusinessRuleException("Não existe categoria cadastrada com o ID " + request.getIdCategoria());
		}

		Produto produto;
		if (edicao) {
			if (request.getId() == 0) {
				throw new BusinessRuleException("ID não informado.");
			}

			produto = repository.findById(request.getId());
			if (produto == null) {
				throw new BusinessRuleException("Produto de ID " + request.getId() + " não cadastrado.");
			}
		} else {
			produto = new Produto();
		}

		produto.setDescricao(request.getDescricao());
		produto.setPreco(request.getPreco());
		produto.setPrecoMembro(request.getPrecoMembro());
		produto.setCategoria(categoria);
		produto.setAtivo(request.isAtivo());
		return produto;
	}

	private ProdutoResponse converterParaResponse(Produto produto) {
		ProdutoResponse response = new ProdutoResponse();
		response.setId(produto.getId());
		response.setDescricao(produto.getDescricao());
		response.setPreco(produto.getPreco());
		response.setPrecoMembro(produto.getPrecoMembro());
		response.setCategoria(produto.getCategoria());
		response.setAtivo(produto.isAtivo());
		return response;
	}
}
