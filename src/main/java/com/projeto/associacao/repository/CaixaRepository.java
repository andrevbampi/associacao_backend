package com.projeto.associacao.repository;

import org.springframework.data.repository.CrudRepository;

import com.projeto.associacao.model.Caixa;

public interface CaixaRepository extends CrudRepository<Caixa, Integer> {

	Caixa findById(int id);

	Caixa findByNomeIgnoreCase(String nome);

}
