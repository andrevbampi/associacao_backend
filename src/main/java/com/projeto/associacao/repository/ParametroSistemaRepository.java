package com.projeto.associacao.repository;

import org.springframework.data.repository.CrudRepository;

import com.projeto.associacao.model.ParametroSistema;

public interface ParametroSistemaRepository extends CrudRepository<ParametroSistema, Integer> {

	ParametroSistema findById(int id);

	ParametroSistema findByChave(String chave);

}
