package com.projeto.associacao.repository;

import org.springframework.data.repository.CrudRepository;

import com.projeto.associacao.model.DocumentoPessoa;

public interface DocumentoPessoaRepository extends CrudRepository<DocumentoPessoa, Integer> {

	DocumentoPessoa findById(int id);

	Iterable<DocumentoPessoa> findByPessoa_IdOrderByDataUploadDesc(int idPessoa);

}
