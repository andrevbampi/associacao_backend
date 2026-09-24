package com.projeto.associacao.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.projeto.associacao.dto.auth.LoginRequest;
import com.projeto.associacao.dto.usuario.UsuarioResponse;
import com.projeto.associacao.model.CredenciaisInvalidasException;
import com.projeto.associacao.model.Pessoa;
import com.projeto.associacao.model.Usuario;
import com.projeto.associacao.repository.UsuarioRepository;
import com.projeto.associacao.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UsuarioRepository repository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtService jwtService;

	@Mock
	private UsuarioService usuarioService;

	@InjectMocks
	private AuthService service;

	private Usuario usuarioAtivo() {
		Pessoa pessoa = new Pessoa();
		pessoa.setId(1);
		pessoa.setNome("Fulano");

		Usuario usuario = new Usuario();
		usuario.setId(1);
		usuario.setLogin("fulano");
		usuario.setSenha("hash");
		usuario.setAtivo(true);
		usuario.setPessoa(pessoa);
		return usuario;
	}

	@Test
	void loginRejeitaLoginOuSenhaEmBranco() {
		LoginRequest request = new LoginRequest();
		request.setLogin("");
		request.setSenha("");
		assertThrows(CredenciaisInvalidasException.class, () -> service.login(request));
	}

	@Test
	void loginRejeitaUsuarioInexistente() {
		LoginRequest request = new LoginRequest();
		request.setLogin("naoexiste");
		request.setSenha("123");
		when(repository.findByLogin("naoexiste")).thenReturn(null);
		assertThrows(CredenciaisInvalidasException.class, () -> service.login(request));
	}

	@Test
	void loginRejeitaUsuarioInativo() {
		Usuario usuario = usuarioAtivo();
		usuario.setAtivo(false);

		LoginRequest request = new LoginRequest();
		request.setLogin("fulano");
		request.setSenha("123");
		when(repository.findByLogin("fulano")).thenReturn(usuario);

		assertThrows(CredenciaisInvalidasException.class, () -> service.login(request));
	}

	@Test
	void loginRejeitaSenhaIncorreta() {
		Usuario usuario = usuarioAtivo();

		LoginRequest request = new LoginRequest();
		request.setLogin("fulano");
		request.setSenha("errada");
		when(repository.findByLogin("fulano")).thenReturn(usuario);
		when(passwordEncoder.matches("errada", "hash")).thenReturn(false);

		assertThrows(CredenciaisInvalidasException.class, () -> service.login(request));
	}

	@Test
	void loginComSucessoRetornaTokenEUsuario() {
		Usuario usuario = usuarioAtivo();

		LoginRequest request = new LoginRequest();
		request.setLogin("fulano");
		request.setSenha("123");

		UsuarioResponse response = new UsuarioResponse();
		response.setId(1);
		response.setLogin("fulano");

		when(repository.findByLogin("fulano")).thenReturn(usuario);
		when(passwordEncoder.matches("123", "hash")).thenReturn(true);
		when(jwtService.gerarToken(usuario)).thenReturn("token-gerado");
		when(usuarioService.converterParaResponse(usuario)).thenReturn(response);

		var resultado = service.login(request);

		assertEquals("token-gerado", resultado.getToken());
		assertEquals("Bearer", resultado.getTipoToken());
		assertEquals("fulano", resultado.getUsuario().getLogin());
	}

	@Test
	void buscarUsuarioLogadoRejeitaLoginInexistente() {
		when(repository.findByLogin(eq("fulano"))).thenReturn(null);
		assertThrows(CredenciaisInvalidasException.class, () -> service.buscarUsuarioLogado("fulano"));
	}
}
