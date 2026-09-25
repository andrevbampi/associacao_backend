package com.projeto.associacao.repository;

import org.springframework.data.repository.CrudRepository;

import com.projeto.associacao.model.TipoEvento;

public interface TipoEventoRepository extends CrudRepository<TipoEvento, Integer> {

	TipoEvento findById(int id);

	TipoEvento findByDescricaoIgnoreCase(String descricao);

}
