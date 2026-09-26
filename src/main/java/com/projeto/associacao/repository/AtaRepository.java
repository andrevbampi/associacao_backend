package com.projeto.associacao.repository;

import org.springframework.data.repository.CrudRepository;

import com.projeto.associacao.model.Ata;

public interface AtaRepository extends CrudRepository<Ata, Integer> {

	Ata findById(int id);

	Iterable<Ata> findAllByOrderByDataAtaDesc();

}
