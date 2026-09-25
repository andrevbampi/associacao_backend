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
		pessoa.setDocumento("111.444.777-35");
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

	@Test
	void cadastrarRejeitaCpfInvalido() {
		Pessoa pessoa = pessoaValida();
		pessoa.setDocumento("111.111.111-11");
		BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.cadastrar(pessoa));
		assertEquals("CPF inválido.", ex.getMessage());
	}

	@Test
	void cadastrarAceitaCnpjValidoParaPessoaJuridica() throws BusinessRuleException {
		Pessoa pessoa = pessoaValida();
		pessoa.setTipo(2);
		pessoa.setDocumento("11.222.333/0001-81");
		when(repository.save(any(Pessoa.class))).thenAnswer(inv -> inv.getArgument(0));
		assertEquals("11.222.333/0001-81", service.cadastrar(pessoa).getDocumento());
	}

	@Test
	void cadastrarRejeitaCnpjInvalidoParaPessoaJuridica() {
		Pessoa pessoa = pessoaValida();
		pessoa.setTipo(2);
		pessoa.setDocumento("11.222.333/0001-00");
		BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.cadastrar(pessoa));
		assertEquals("CNPJ inválido.", ex.getMessage());
	}

	@Test
	void selecionarFiltraPorNome() {
		Pessoa fulano = pessoaValida();
		fulano.setNome("Fulano de Tal");
		Pessoa ciclano = pessoaValida();
		ciclano.setNome("Ciclano da Silva");
		when(repository.findAll()).thenReturn(java.util.List.of(fulano, ciclano));

		var resultado = service.selecionar("fulano", null, null, null);

		assertEquals(1, ((java.util.List<Pessoa>) resultado).size());
	}

	@Test
	void selecionarComSemUsuarioIgnoraPessoaComUsuario() {
		Pessoa comUsuario = pessoaValida();
		comUsuario.setId(1);
		Pessoa semUsuario = pessoaValida();
		semUsuario.setId(2);
		when(repository.findAll()).thenReturn(java.util.List.of(comUsuario, semUsuario));
		when(usuarioRepository.findByPessoa_Id(1)).thenReturn(new Usuario());
		when(usuarioRepository.findByPessoa_Id(2)).thenReturn(null);

		var resultado = service.selecionar(null, null, true, null);

		assertEquals(1, ((java.util.List<Pessoa>) resultado).size());
	}
}
