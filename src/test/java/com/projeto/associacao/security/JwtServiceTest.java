package com.projeto.associacao.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.projeto.associacao.model.Pessoa;
import com.projeto.associacao.model.Usuario;

class JwtServiceTest {

	private JwtService jwtService;

	@BeforeEach
	void setUp() {
		jwtService = new JwtService();
		ReflectionTestUtils.setField(jwtService, "secret", "segredo-de-teste-com-pelo-menos-32-bytes-de-tamanho");
		ReflectionTestUtils.setField(jwtService, "expirationMs", 60_000L);
	}

	private Usuario usuario() {
		Pessoa pessoa = new Pessoa();
		pessoa.setId(9);
		pessoa.setNome("Fulano de Tal");

		Usuario usuario = new Usuario();
		usuario.setId(3);
		usuario.setLogin("fulano");
		usuario.setPessoa(pessoa);
		return usuario;
	}

	@Test
	void gerarTokenEExtrairLoginDevolveOMesmoLogin() {
		String token = jwtService.gerarToken(usuario());
		assertEquals("fulano", jwtService.extrairLogin(token));
	}

	@Test
	void tokenGeradoEValido() {
		String token = jwtService.gerarToken(usuario());
		assertTrue(jwtService.tokenValido(token));
	}

	@Test
	void tokenAdulteradoEInvalido() {
		String token = jwtService.gerarToken(usuario());
		assertFalse(jwtService.tokenValido(token + "adulterado"));
	}

	@Test
	void tokenExpiradoEInvalido() throws InterruptedException {
		ReflectionTestUtils.setField(jwtService, "expirationMs", -1000L);
		String token = jwtService.gerarToken(usuario());
		assertFalse(jwtService.tokenValido(token));
	}

	@Test
	void textoQualquerNaoEUmTokenValido() {
		assertFalse(jwtService.tokenValido("isso-nao-e-um-jwt"));
	}
}
