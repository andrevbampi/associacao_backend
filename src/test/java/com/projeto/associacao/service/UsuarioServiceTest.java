package com.projeto.associacao.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.projeto.associacao.dto.usuario.UsuarioRequest;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.Pessoa;
import com.projeto.associacao.model.Usuario;
import com.projeto.associacao.repository.PessoaRepository;
import com.projeto.associacao.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

	@Mock
	private UsuarioRepository repository;

	@Mock
	private PessoaRepository pessoaRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UsuarioService service;

	private UsuarioRequest requestValido() {
		UsuarioRequest request = new UsuarioRequest();
		request.setLogin("fulano");
		request.setSenha("segredo");
		request.setIdPessoa(1);
		request.setAtivo(true);
		return request;
	}

	@Test
	void cadastrarRejeitaLoginNulo() {
		UsuarioRequest request = requestValido();
		request.setLogin(null);
		BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.cadastrar(request));
		assertEquals("Login não informado.", ex.getMessage());
	}

	@Test
	void cadastrarRejeitaSenhaNula() {
		UsuarioRequest request = requestValido();
		request.setSenha(null);
		BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.cadastrar(request));
		assertEquals("Senha não informada.", ex.getMessage());
	}

	@Test
	void cadastrarGravaSenhaCriptografadaENovoId() throws BusinessRuleException {
		UsuarioRequest request = requestValido();
		request.setId(7);
		when(pessoaRepository.findById(1)).thenReturn(new Pessoa());
		when(passwordEncoder.encode("segredo")).thenReturn("hash");
		when(repository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

		service.cadastrar(request);

		ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
		verify(repository).save(captor.capture());
		assertEquals("hash", captor.getValue().getSenha());
		assertEquals(0, captor.getValue().getId());
	}

	@Test
	void alterarSemSenhaMantemSenhaAtual() throws BusinessRuleException {
		Pessoa pessoa = new Pessoa();
		pessoa.setId(1);
		Usuario existente = new Usuario();
		existente.setId(3);
		existente.setLogin("fulano");
		existente.setSenha("hashAntigo");
		existente.setPessoa(pessoa);

		UsuarioRequest request = requestValido();
		request.setId(3);
		request.setSenha(null);
		when(repository.findById(3)).thenReturn(existente);
		when(pessoaRepository.findById(1)).thenReturn(pessoa);
		when(repository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

		service.alterar(request);

		ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
		verify(repository).save(captor.capture());
		assertEquals("hashAntigo", captor.getValue().getSenha());
	}

	@Test
	void removerRejeitaUsuarioInexistente() {
		when(repository.findById(9)).thenReturn(null);
		assertThrows(BusinessRuleException.class, () -> service.remover(9));
		verify(repository, never()).deleteById(any());
	}
}
