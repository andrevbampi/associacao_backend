package com.projeto.associacao.repository;

import org.springframework.data.repository.CrudRepository;

import com.projeto.associacao.model.Produto;

public interface ProdutoRepository extends CrudRepository<Produto, Integer> {

	Produto findById(int id);

}
