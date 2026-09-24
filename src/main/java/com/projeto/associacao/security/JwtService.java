package com.projeto.associacao.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.projeto.associacao.model.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Geração e validação dos tokens JWT usados para autenticar as requisições à API.
 * O token carrega o login do usuário (subject) e alguns dados úteis ao front-end
 * (id do usuário, id e nome da pessoa vinculada), evitando uma consulta extra
 * a cada requisição só para exibir "quem está logado".
 */
@Service
public class JwtService {

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expiration-ms:86400000}")
	private long expirationMs;

	public String gerarToken(Usuario usuario) {
		Date agora = new Date();
		Date expiracao = new Date(agora.getTime() + expirationMs);

		return Jwts.builder()
				.subject(usuario.getLogin())
				.claim("id", usuario.getId())
				.claim("idPessoa", usuario.getPessoa().getId())
				.claim("nome", usuario.getPessoa().getNome())
				.issuedAt(agora)
				.expiration(expiracao)
				.signWith(chave())
				.compact();
	}

	public String extrairLogin(String token) {
		return parseClaims(token).getSubject();
	}

	public boolean tokenValido(String token) {
		try {
			parseClaims(token);
			return true;
		} catch (JwtException | IllegalArgumentException ex) {
			return false;
		}
	}

	private Claims parseClaims(String token) {
		return Jwts.parser()
				.verifyWith(chave())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	private SecretKey chave() {
		return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

}
