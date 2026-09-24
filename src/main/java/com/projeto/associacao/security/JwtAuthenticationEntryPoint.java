package com.projeto.associacao.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Acionado quando uma rota protegida é acessada sem um token válido.
 * Devolve texto puro (não JSON) para manter o mesmo formato de erro que o
 * CustomExceptionHandler já usa para BusinessRuleException — assim o
 * front-end trata os dois casos com a mesma lógica de extração de mensagem.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
			throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("text/plain;charset=UTF-8");
		response.getWriter().write("Sessão expirada ou não autenticada. Faça login novamente.");
	}

}
