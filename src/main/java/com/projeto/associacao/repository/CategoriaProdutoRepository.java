package com.projeto.associacao.repository;

import org.springframework.data.repository.CrudRepository;

import com.projeto.associacao.model.CategoriaProduto;

public interface CategoriaProdutoRepository extends CrudRepository<CategoriaProduto, Integer> {

	CategoriaProduto findById(int id);

	CategoriaProduto findByDescricaoIgnoreCase(String descricao);

}
