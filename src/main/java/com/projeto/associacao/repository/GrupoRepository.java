package com.projeto.associacao.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.projeto.associacao.model.Grupo;

public interface GrupoRepository extends CrudRepository<Grupo, Integer> {

	Grupo findById(int id);

	Grupo findByNomeIgnoreCase(String nome);

}
