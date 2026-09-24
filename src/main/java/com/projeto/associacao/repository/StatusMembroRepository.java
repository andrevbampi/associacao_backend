package com.projeto.associacao.repository;

import org.springframework.data.repository.CrudRepository;

import com.projeto.associacao.model.StatusMembro;

public interface StatusMembroRepository extends CrudRepository<StatusMembro, Integer> {

	StatusMembro findById(int id);

	StatusMembro findByDescricaoIgnoreCase(String descricao);

}
