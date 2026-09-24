package com.projeto.associacao.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projeto.associacao.dto.membro.MembroRequest;
import com.projeto.associacao.dto.membro.MembroResponse;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.Membro;
import com.projeto.associacao.model.Pessoa;
import com.projeto.associacao.model.StatusMembro;
import com.projeto.associacao.repository.MembroRepository;
import com.projeto.associacao.repository.PessoaRepository;
import com.projeto.associacao.repository.StatusMembroRepository;

@ExtendWith(MockitoExtension.class)
class MembroServiceTest {

	@Mock
	private MembroRepository repository;

	@Mock
	private PessoaRepository pessoaRepository;

	@Mock
	private StatusMembroRepository statusRepository;

	@InjectMocks
	private MembroService service;

	private MembroRequest requestValido() {
		MembroRequest request = new MembroRequest();
		request.setIdPessoa(1);
		request.setIdStatus(2);
		request.setAtivo(true);
		return request;
	}

	@Test
	void cadastrarUsaDataAtualQuandoInclusaoNaoInformada() throws BusinessRuleException {
		when(pessoaRepository.findById(1)).thenReturn(new Pessoa());
		when(statusRepository.findById(2)).thenReturn(new StatusMembro());
		when(repository.save(any(Membro.class))).thenAnswer(inv -> inv.getArgument(0));

		MembroResponse response = service.cadastrar(requestValido());

		assertEquals(LocalDate.now(), response.getDataInclusao());
	}

	@Test
	void cadastrarRejeitaStatusNaoInformado() {
		MembroRequest request = requestValido();
		request.setIdStatus(0);
		BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.cadastrar(request));
		assertEquals("Status não informado.", ex.getMessage());
	}

	@Test
	void cadastrarRejeitaPessoaQueJaEMembro() {
		when(repository.findByPessoa_Id(1)).thenReturn(new Membro());
		assertThrows(BusinessRuleException.class, () -> service.cadastrar(requestValido()));
		verify(repository, never()).save(any());
	}

	@Test
	void cadastrarRejeitaStatusInexistente() {
		when(pessoaRepository.findById(1)).thenReturn(new Pessoa());
		when(statusRepository.findById(2)).thenReturn(null);
		assertThrows(BusinessRuleException.class, () -> service.cadastrar(requestValido()));
		verify(repository, never()).save(any());
	}

	@Test
	void cadastrarRejeitaSaidaAnteriorAInclusao() {
		MembroRequest request = requestValido();
		request.setDataInclusao(LocalDate.of(2026, 5, 10));
		request.setDataSaida(LocalDate.of(2026, 5, 9));
		BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> service.cadastrar(request));
		assertEquals("A data de saída não pode ser anterior à data de inclusão.", ex.getMessage());
	}

	@Test
	void alterarRejeitaMembroInexistente() {
		MembroRequest request = requestValido();
		request.setId(4);
		when(repository.findById(4)).thenReturn(null);
		assertThrows(BusinessRuleException.class, () -> service.alterar(request));
	}
}
