package com.projeto.associacao.repository;

import org.springframework.data.repository.CrudRepository;

import com.projeto.associacao.model.MovimentoEstoque;
import com.projeto.associacao.model.OrigemMovimentoEstoque;

public interface MovimentoEstoqueRepository extends CrudRepository<MovimentoEstoque, Integer> {

	MovimentoEstoque findById(int id);

	Iterable<MovimentoEstoque> findAllByOrderByDataHoraDesc();

	Iterable<MovimentoEstoque> findByOrigemAndIdOrigemAndEstornadoFalse(OrigemMovimentoEstoque origem, int idOrigem);

}
