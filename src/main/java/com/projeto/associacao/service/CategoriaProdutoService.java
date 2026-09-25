package com.projeto.associacao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.CategoriaProduto;
import com.projeto.associacao.repository.CategoriaProdutoRepository;
import com.projeto.associacao.repository.ProdutoRepository;

@Service
public class CategoriaProdutoService {

	@Autowired
	private CategoriaProdutoRepository repository;

	@Autowired
	private ProdutoRepository produtoRepository;

	public Iterable<CategoriaProduto> selecionar() {
		return repository.findAll();
	}

	public CategoriaProduto cadastrar(CategoriaProduto categoria) throws BusinessRuleException {
		categoria.setId(0);
		this.validarCategoria(categoria);
		return repository.save(categoria);
	}

	public CategoriaProduto alterar(CategoriaProduto categoria) throws BusinessRuleException {
		if (categoria.getId() == 0) {
			throw new BusinessRuleException("ID não informado.");
		}

		if (repository.findById(categoria.getId()) == null) {
			throw new BusinessRuleException("Categoria de ID " + categoria.getId() + " não cadastrada.");
		}

		this.validarCategoria(categoria);
		return repository.save(categoria);
	}

	public void remover(int id) throws BusinessRuleException {
		if (id == 0) {
			throw new BusinessRuleException("ID não informado.");
		}

		if (repository.findById(id) == null) {
			throw new BusinessRuleException("Categoria de ID " + id + " não cadastrada.");
		}

		if (produtoRepository.existsByCategoria_Id(id)) {
			throw new BusinessRuleException("A categoria de ID " + id + " está vinculada a produtos.");
		}

		repository.deleteById(id);
	}

	private void validarCategoria(CategoriaProduto categoria) throws BusinessRuleException {
		if ((categoria.getDescricao() == null) || categoria.getDescricao().isBlank()) {
			throw new BusinessRuleException("Descrição não informada.");
		}

		CategoriaProduto categoriaAux = repository.findByDescricaoIgnoreCase(categoria.getDescricao().trim());
		if ((categoriaAux != null) && (categoriaAux.getId() != categoria.getId())) {
			throw new BusinessRuleException("Já existe uma categoria com a descrição " + categoria.getDescricao() + ".");
		}
	}
}
