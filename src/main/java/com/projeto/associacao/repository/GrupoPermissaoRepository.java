package com.projeto.associacao.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.projeto.associacao.model.GrupoPermissao;

public interface GrupoPermissaoRepository extends CrudRepository<GrupoPermissao, Integer> {

	List<GrupoPermissao> findByGrupo_Id(int idGrupo);

	List<GrupoPermissao> findByPermissao_Id(int idPermissao);

}
