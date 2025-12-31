package com.projeto.associacao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projeto.associacao.model.Usuario;
import com.projeto.associacao.repository.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository repository;
	
	public Iterable<Usuario> selecionar() {
		return repository.findAll();
	}
	
	public Usuario cadastrar(Usuario usuario) {
		return repository.save(usuario);
	}
	
	public Usuario alterar(Usuario usuario) {
		return repository.save(usuario);
	}
	
	public void remover(int id) {
		repository.deleteById(id);
	}
}
