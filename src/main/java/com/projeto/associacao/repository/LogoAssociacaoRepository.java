package com.projeto.associacao.repository;

import org.springframework.data.repository.CrudRepository;

import com.projeto.associacao.model.LogoAssociacao;

public interface LogoAssociacaoRepository extends CrudRepository<LogoAssociacao, Integer> {

	LogoAssociacao findById(int id);

}
