package com.projeto.associacao.repository;

import org.springframework.data.repository.CrudRepository;

import com.projeto.associacao.model.HistoricoMembro;

public interface HistoricoMembroRepository extends CrudRepository<HistoricoMembro, Integer> {

	HistoricoMembro findById(int id);

	Iterable<HistoricoMembro> findByMembro_IdOrderByDataDesc(int idMembro);

	boolean existsByTipoEvento_Id(int idTipoEvento);

}
