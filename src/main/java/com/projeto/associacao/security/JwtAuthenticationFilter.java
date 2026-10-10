package com.projeto.associacao.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.projeto.associacao.service.PermissaoService;

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

	@Autowired
	private PermissaoService permissaoService;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {

		String token = extrairToken(request);

		if ((token != null) && jwtService.tokenValido(token) && (SecurityContextHolder.getContext().getAuthentication() == null)) {
			String login = jwtService.extrairLogin(token);

			// As permissões vêm do banco (com cache curto), não do token: assim uma
			// mudança de grupo/permissão vale quase na hora e um usuário inativado
			// ou excluído deixa de ser autenticado mesmo com token ainda válido.
			Set<String> permissoes = permissaoService.permissoesDoLogin(login);
			if (permissoes != null) {
				List<GrantedAuthority> authorities = new ArrayList<>();
				authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
				permissoes.forEach(codigo -> authorities.add(new SimpleGrantedAuthority(codigo)));
				var authentication = new UsernamePasswordAuthenticationToken(login, null, authorities);
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
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
