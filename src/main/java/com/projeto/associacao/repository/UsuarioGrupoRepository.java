package com.projeto.associacao.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.projeto.associacao.model.UsuarioGrupo;

public interface UsuarioGrupoRepository extends CrudRepository<UsuarioGrupo, Integer> {

	List<UsuarioGrupo> findByUsuario_Id(int idUsuario);

	List<UsuarioGrupo> findByGrupo_Id(int idGrupo);

	boolean existsByGrupo_Id(int idGrupo);

}
