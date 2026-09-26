package com.projeto.associacao.repository;

import org.springframework.data.repository.CrudRepository;

import com.projeto.associacao.model.DocumentoAta;

public interface DocumentoAtaRepository extends CrudRepository<DocumentoAta, Integer> {

	DocumentoAta findById(int id);

	Iterable<DocumentoAta> findByAta_IdOrderByDataUploadDesc(int idAta);

	boolean existsByAta_Id(int idAta);

}
