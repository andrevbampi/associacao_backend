package com.projeto.associacao.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.projeto.associacao.dto.usuario.UsuarioRequest;
import com.projeto.associacao.dto.usuario.UsuarioResponse;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.Pessoa;
import com.projeto.associacao.model.Usuario;
import com.projeto.associacao.repository.PessoaRepository;
import com.projeto.associacao.repository.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository repository;

	@Autowired
	private PessoaRepository pessoaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

	public Iterable<UsuarioResponse> selecionar() {
		Iterable<Usuario> usuarios = repository.findAll();
		List<UsuarioResponse> responses = new ArrayList<>();
		for (Usuario usuario : usuarios) {
			responses.add(converterParaResponse(usuario));
		}
		return responses;
	}
	
	public UsuarioResponse cadastrar(UsuarioRequest request) throws BusinessRuleException {
		Usuario usuario = this.validarUsuario(request, false);
		return converterParaResponse(repository.save(usuario));
	}
	
	public UsuarioResponse alterar(UsuarioRequest request) throws BusinessRuleException {
		Usuario usuario = this.validarUsuario(request, true);
		return converterParaResponse(repository.save(usuario));
	}
	
	public void remover(int id) throws BusinessRuleException {
		if (id == 0) {
			throw new BusinessRuleException("ID não informado.");
		}

		if (repository.findById(id) == null) {
			throw new BusinessRuleException("Usuário de ID " + id + " não cadastrado.");
		}

		repository.deleteById(id);
	}
	
	private Usuario validarUsuario(UsuarioRequest request, boolean edicao) throws BusinessRuleException {
		if ((request.getLogin() == null) || request.getLogin().isBlank()) {
			throw new BusinessRuleException("Login não informado.");
		}

		if (request.getIdPessoa() == 0) {
			throw new BusinessRuleException("Id da pessoa não informado.");
		}
		
		if (edicao) {
			if (request.getId() == 0) {
				throw new BusinessRuleException("ID não informado.");
			}

			Usuario usuarioAux = repository.findById(request.getId());
			if (usuarioAux == null) {
				throw new BusinessRuleException("Usuário de ID " + request.getId() + " não cadastrado.");
			}

			if (!request.getLogin().trim().equals(usuarioAux.getLogin().trim())) {
				if (repository.findByLogin(request.getLogin()) != null) {
					throw new BusinessRuleException("Já existe um usuário com o login " + request.getLogin() + ".");
				}
			}

			if (request.getIdPessoa() != usuarioAux.getPessoa().getId()) {
				if (repository.findByPessoa_Id(request.getIdPessoa()) != null) {
					throw new BusinessRuleException("Já existe um usuário para a pessoa com o ID " + request.getIdPessoa());
				}
			}

			if ((request.getSenha() == null) || request.getSenha().isBlank()) {
				request.setSenha(usuarioAux.getSenha());				
			} else {
				request.setSenha(passwordEncoder.encode(request.getSenha()));
			}

		} else {
			if ((request.getSenha() == null) || request.getSenha().isBlank()) {
				throw new BusinessRuleException("Senha não informada.");
			}

			if (repository.findByLogin(request.getLogin()) != null) {
				throw new BusinessRuleException("Já existe um usuário com o login " + request.getLogin() + ".");
			}

			if (repository.findByPessoa_Id(request.getIdPessoa()) != null) {
				throw new BusinessRuleException("Já existe um usuário para a pessoa com o ID " + request.getIdPessoa());
			}

			request.setId(0);
			request.setSenha(passwordEncoder.encode(request.getSenha()));
		}

		Pessoa pessoa = pessoaRepository.findById(request.getIdPessoa());
		if (pessoa == null) {
			throw new BusinessRuleException("Não existe pessoa cadastrada com o ID " + request.getIdPessoa());
		}

		Usuario usuario = new Usuario();
		usuario.setId(request.getId());
		usuario.setLogin(request.getLogin());
		usuario.setSenha(request.getSenha());
		usuario.setAtivo(request.isAtivo());
		usuario.setPessoa(pessoa);
		return usuario;
	}

	public UsuarioResponse converterParaResponse(Usuario usuario) {
		UsuarioResponse response = new UsuarioResponse();
		response.setId(usuario.getId());
		response.setLogin(usuario.getLogin());
		response.setAtivo(usuario.isAtivo());
		response.setPessoa(usuario.getPessoa());
		return response;
	}
}
