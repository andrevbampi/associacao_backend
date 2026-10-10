package com.projeto.associacao.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.projeto.associacao.dto.auth.AlterarSenhaRequest;
import com.projeto.associacao.dto.auth.LoginRequest;
import com.projeto.associacao.dto.auth.LoginResponse;
import com.projeto.associacao.dto.usuario.UsuarioResponse;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.CredenciaisInvalidasException;
import com.projeto.associacao.model.Usuario;
import com.projeto.associacao.repository.UsuarioRepository;
import com.projeto.associacao.security.JwtService;

@Service
public class AuthService {

	private static final int TAMANHO_MINIMO_SENHA = 6;

	@Autowired
	private UsuarioRepository repository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private PermissaoService permissaoService;

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
		response.setUsuario(converterComPermissoes(usuario));
		return response;
	}

	public UsuarioResponse buscarUsuarioLogado(String login) {
		Usuario usuario = repository.findByLogin(login);
		if (usuario == null) {
			throw new CredenciaisInvalidasException("Usuário não encontrado.");
		}
		return converterComPermissoes(usuario);
	}

	/**
	 * Troca da própria senha. A senha atual é exigida para que uma sessão deixada
	 * aberta não permita a quem passar por ali assumir a conta. Erros usam
	 * BusinessRuleException (400) e não 401, para o front não confundir senha
	 * atual errada com sessão expirada e deslogar o usuário.
	 */
	public void alterarSenha(String login, AlterarSenhaRequest request) throws BusinessRuleException {
		if ((request.getSenhaAtual() == null) || request.getSenhaAtual().isBlank()) {
			throw new BusinessRuleException("Senha atual não informada.");
		}
		if ((request.getNovaSenha() == null) || request.getNovaSenha().isBlank()) {
			throw new BusinessRuleException("Nova senha não informada.");
		}
		if (request.getNovaSenha().length() < TAMANHO_MINIMO_SENHA) {
			throw new BusinessRuleException("A nova senha deve ter pelo menos " + TAMANHO_MINIMO_SENHA + " caracteres.");
		}

		Usuario usuario = repository.findByLogin(login);
		if ((usuario == null) || !usuario.isAtivo()) {
			throw new CredenciaisInvalidasException("Usuário não encontrado.");
		}
		if (!passwordEncoder.matches(request.getSenhaAtual(), usuario.getSenha())) {
			throw new BusinessRuleException("Senha atual incorreta.");
		}
		if (request.getNovaSenha().equals(request.getSenhaAtual())) {
			throw new BusinessRuleException("A nova senha deve ser diferente da senha atual.");
		}

		usuario.setSenha(passwordEncoder.encode(request.getNovaSenha()));
		repository.save(usuario);
	}

	/** Só a sessão (login e /me) carrega as permissões; as listagens de usuários não. */
	private UsuarioResponse converterComPermissoes(Usuario usuario) {
		UsuarioResponse response = usuarioService.converterParaResponse(usuario);
		response.setPermissoes(new ArrayList<>(permissaoService.permissoesEfetivas(usuario.getId())));
		return response;
	}

}
