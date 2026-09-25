package com.projeto.associacao.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.Produto;
import com.projeto.associacao.repository.ItemComandaRepository;
import com.projeto.associacao.repository.ProdutoRepository;

@Service
public class ProdutoService {

	@Autowired
	private ProdutoRepository repository;

	@Autowired
	private ItemComandaRepository itemComandaRepository;

	public Iterable<Produto> selecionar() {
		return repository.findAll();
	}

	public Produto cadastrar(Produto produto) throws BusinessRuleException {
		produto.setId(0);
		this.validarProduto(produto);
		return repository.save(produto);
	}

	public Produto alterar(Produto produto) throws BusinessRuleException {
		if (produto.getId() == 0) {
			throw new BusinessRuleException("ID não informado.");
		}

		if (repository.findById(produto.getId()) == null) {
			throw new BusinessRuleException("Produto de ID " + produto.getId() + " não cadastrado.");
		}

		this.validarProduto(produto);
		return repository.save(produto);
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

	private void validarProduto(Produto produto) throws BusinessRuleException {
		if ((produto.getDescricao() == null) || produto.getDescricao().isBlank()) {
			throw new BusinessRuleException("Descrição não informada.");
		}

		if ((produto.getPreco() == null) || (produto.getPreco().compareTo(BigDecimal.ZERO) < 0)) {
			throw new BusinessRuleException("Preço inválido.");
		}

		if ((produto.getPrecoMembro() == null) || (produto.getPrecoMembro().compareTo(BigDecimal.ZERO) < 0)) {
			throw new BusinessRuleException("Preço para membro inválido.");
		}
	}
}
