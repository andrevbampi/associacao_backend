package com.projeto.associacao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.associacao.dto.auth.LoginRequest;
import com.projeto.associacao.dto.auth.LoginResponse;
import com.projeto.associacao.dto.usuario.UsuarioResponse;
import com.projeto.associacao.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthService service;

	@PostMapping("/login")
	public LoginResponse login(@RequestBody LoginRequest request) {
		return service.login(request);
	}

	/**
	 * Devolve os dados do usuário dono do token atual. Usado pelo front-end para
	 * restaurar a sessão ao recarregar a página, sem precisar guardar os dados
	 * do usuário em local storage.
	 */
	@GetMapping("/me")
	public UsuarioResponse me(Authentication authentication) {
		return service.buscarUsuarioLogado(authentication.getName());
	}

}
