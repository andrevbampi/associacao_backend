package com.projeto.associacao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.projeto.associacao.dto.auth.LoginRequest;
import com.projeto.associacao.dto.auth.LoginResponse;
import com.projeto.associacao.dto.usuario.UsuarioResponse;
import com.projeto.associacao.model.CredenciaisInvalidasException;
import com.projeto.associacao.model.Usuario;
import com.projeto.associacao.repository.UsuarioRepository;
import com.projeto.associacao.security.JwtService;

@Service
public class AuthService {

	@Autowired
	private UsuarioRepository repository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private UsuarioService usuarioService;

	public LoginResponse login(LoginRequest request) {
		if ((request.getLogin() == null) || request.getLogin().isBlank()
				|| (request.getSenha() == null) || request.getSenha().isBlank()) {
			throw new CredenciaisInvalidasException("Informe login e senha.");
		}

		Usuario usuario = repository.findByLogin(request.getLogin().trim());

		// Mensagem genérica de propósito: não dá para o front (nem para quem tenta
		// adivinhar) saber se o login existe, se está inativo ou se a senha é que
		// está errada.
		if ((usuario == null) || !usuario.isAtivo() || !passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
			throw new CredenciaisInvalidasException("Login ou senha inválidos.");
		}

		LoginResponse response = new LoginResponse();
		response.setToken(jwtService.gerarToken(usuario));
		response.setUsuario(usuarioService.converterParaResponse(usuario));
		return response;
	}

	public UsuarioResponse buscarUsuarioLogado(String login) {
		Usuario usuario = repository.findByLogin(login);
		if (usuario == null) {
			throw new CredenciaisInvalidasException("Usuário não encontrado.");
		}
		return usuarioService.converterParaResponse(usuario);
	}

}
