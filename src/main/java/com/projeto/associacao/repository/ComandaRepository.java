package com.projeto.associacao.repository;

import org.springframework.data.repository.CrudRepository;

import com.projeto.associacao.model.Comanda;
import com.projeto.associacao.model.StatusComanda;

public interface ComandaRepository extends CrudRepository<Comanda, Integer> {

	Comanda findById(int id);

	Iterable<Comanda> findByStatusOrderByDataAberturaDesc(StatusComanda status);

	Iterable<Comanda> findAllByOrderByDataAberturaDesc();

}
