package com.projeto.associacao.repository;

import org.springframework.data.repository.CrudRepository;

import com.projeto.associacao.model.Usuario;

public interface UsuarioRepository extends CrudRepository<Usuario, Integer> {

	Usuario findById(int id);

	Usuario findByLogin(String login);

	Usuario findByPessoa_Id(int idPessoa);
	
}
