package com.projeto.associacao.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Lê o header "Authorization: Bearer &lt;token&gt;" de cada requisição.
 * Se o token for válido, autentica a requisição no contexto do Spring Security
 * (com o login como principal). Se estiver ausente, inválido ou expirado, a
 * requisição simplesmente segue sem autenticação — quem decide se isso é um
 * problema é o SecurityConfig (rota pública ou não) e, se for, o
 * JwtAuthenticationEntryPoint entra em ação.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final String PREFIXO_BEARER = "Bearer ";

	@Autowired
	private JwtService jwtService;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {

		String token = extrairToken(request);

		if ((token != null) && jwtService.tokenValido(token) && (SecurityContextHolder.getContext().getAuthentication() == null)) {
			String login = jwtService.extrairLogin(token);

			var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
			var authentication = new UsernamePasswordAuthenticationToken(login, null, authorities);
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		filterChain.doFilter(request, response);
	}

	private String extrairToken(HttpServletRequest request) {
		String header = request.getHeader("Authorization");
		if ((header != null) && header.startsWith(PREFIXO_BEARER)) {
			return header.substring(PREFIXO_BEARER.length());
		}
		return null;
	}

}
