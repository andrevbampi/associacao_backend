package com.projeto.associacao.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.projeto.associacao.model.Permissao;

public interface PermissaoRepository extends CrudRepository<Permissao, Integer> {

	Permissao findByCodigo(String codigo);

}
