package com.projeto.associacao.repository;

import org.springframework.data.repository.CrudRepository;

import com.projeto.associacao.model.Membro;

public interface MembroRepository extends CrudRepository<Membro, Integer> {

	Membro findById(int id);

	Membro findByPessoa_Id(int idPessoa);

	boolean existsByStatus_Id(int idStatus);

}
