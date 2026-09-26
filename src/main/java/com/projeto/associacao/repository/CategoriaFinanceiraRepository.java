package com.projeto.associacao.repository;

import org.springframework.data.repository.CrudRepository;

import com.projeto.associacao.model.CategoriaFinanceira;

public interface CategoriaFinanceiraRepository extends CrudRepository<CategoriaFinanceira, Integer> {

	CategoriaFinanceira findById(int id);

	CategoriaFinanceira findByDescricaoIgnoreCase(String descricao);

}
