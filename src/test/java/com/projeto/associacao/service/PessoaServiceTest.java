package com.projeto.associacao.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.Membro;
import com.projeto.associacao.model.Pessoa;
import com.projeto.associacao.model.Usuario;
import com.projeto.associacao.repository.MembroRepository;
import com.projeto.associacao.repository.PessoaRepository;
import com.projeto.associacao.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class PessoaServiceTest {

	@Mock
	private PessoaRepository repository;

	@Mock
	private UsuarioRepository usuarioRepository;

	@Mock
	private MembroRepository membroRepository;

	@InjectMocks
	private PessoaService service;

	private Pessoa pessoaValida() {
		Pessoa pessoa = new Pessoa();
		pessoa.setTipo(1);
		pessoa.setNome("Fulano");
		pessoa.setDocumento("12345678900");
		return pessoa;
	}

	@Test
	void cadastrarRejeitaTipoInvalido() {
		Pessoa pessoa = pessoaValida();
		pessoa.setTipo(3);
		BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.cadastrar(pessoa));
		assertEquals("Tipo não informado.", ex.getMessage());
	}

	@Test
	void cadastrarRejeitaNomeNulo() {
		Pessoa pessoa = pessoaValida();
		pessoa.setNome(null);
		BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.cadastrar(pessoa));
		assertEquals("Nome não informado.", ex.getMessage());
	}

	@Test
	void cadastrarRejeitaDocumentoVazio() {
		Pessoa pessoa = pessoaValida();
		pessoa.setDocumento("  ");
		BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.cadastrar(pessoa));
		assertEquals("Documento não informado.", ex.getMessage());
	}

	@Test
	void cadastrarIgnoraIdInformado() throws BusinessRuleException {
		Pessoa pessoa = pessoaValida();
		pessoa.setId(5);
		when(repository.save(any(Pessoa.class))).thenAnswer(inv -> inv.getArgument(0));
		assertEquals(0, service.cadastrar(pessoa).getId());
	}

	@Test
	void alterarRejeitaPessoaInexistente() {
		Pessoa pessoa = pessoaValida();
		pessoa.setId(99);
		when(repository.findById(99)).thenReturn(null);
		assertThrows(BusinessRuleException.class, () -> service.alterar(pessoa));
		verify(repository, never()).save(any());
	}

	@Test
	void removerRejeitaPessoaComUsuario() {
		when(repository.findById(1)).thenReturn(pessoaValida());
		when(usuarioRepository.findByPessoa_Id(1)).thenReturn(new Usuario());
		assertThrows(BusinessRuleException.class, () -> service.remover(1));
		verify(repository, never()).deleteById(any());
	}

	@Test
	void removerRejeitaPessoaComMembro() {
		when(repository.findById(1)).thenReturn(pessoaValida());
		when(membroRepository.findByPessoa_Id(1)).thenReturn(new Membro());
		assertThrows(BusinessRuleException.class, () -> service.remover(1));
		verify(repository, never()).deleteById(any());
	}
}
