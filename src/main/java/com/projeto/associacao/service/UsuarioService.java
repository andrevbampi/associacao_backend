package com.projeto.associacao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.Usuario;
import com.projeto.associacao.repository.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository repository;
	
	public Iterable<Usuario> selecionar() {
		return repository.findAll();
	}
	
	public Usuario cadastrar(Usuario usuario) throws BusinessRuleException {
		this.validarUsuario(usuario, false);
		return repository.save(usuario);
	}
	
	public Usuario alterar(Usuario usuario) throws BusinessRuleException {
		this.validarUsuario(usuario, true);
		return repository.save(usuario);
	}
	
	public void remover(int id) throws BusinessRuleException {
		if (id == 0) {
			throw new BusinessRuleException("ID não informado.");
		}
		repository.deleteById(id);
	}
	
	private void validarUsuario(Usuario usuario, boolean edicao) throws BusinessRuleException {
		if (usuario.getLogin().trim().equals("")) {
			throw new BusinessRuleException("Login não informado.");
		}
		
		if (edicao) {
			if (usuario.getId() == 0) {
				throw new BusinessRuleException("ID não informado.");
			}		
		} else {
			if (repository.findByLogin(usuario.getLogin()) != null) {
				throw new BusinessRuleException("Já existe um usuário com o login " + usuario.getLogin() + ".");
			}
		}
		
		if (usuario.getSenha().trim().equals("")) {
			throw new BusinessRuleException("Senha não informada.");
		}
		
		if (usuario.getNome().trim().equals("")) {
			throw new BusinessRuleException("Nome não informado.");
		}
	}
}
