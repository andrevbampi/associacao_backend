package com.projeto.associacao.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.projeto.associacao.model.UsuarioPermissao;

public interface UsuarioPermissaoRepository extends CrudRepository<UsuarioPermissao, Integer> {

	List<UsuarioPermissao> findByUsuario_Id(int idUsuario);

	List<UsuarioPermissao> findByPermissao_Id(int idPermissao);

}
