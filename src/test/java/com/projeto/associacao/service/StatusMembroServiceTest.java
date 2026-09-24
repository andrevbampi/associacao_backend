package com.projeto.associacao.service;

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
import com.projeto.associacao.model.StatusMembro;
import com.projeto.associacao.repository.MembroRepository;
import com.projeto.associacao.repository.StatusMembroRepository;

@ExtendWith(MockitoExtension.class)
class StatusMembroServiceTest {

	@Mock
	private StatusMembroRepository repository;

	@Mock
	private MembroRepository membroRepository;

	@InjectMocks
	private StatusMembroService service;

	@Test
	void cadastrarRejeitaDescricaoDuplicada() {
		StatusMembro existente = new StatusMembro();
		existente.setId(1);
		existente.setDescricao("Ativo");
		when(repository.findByDescricaoIgnoreCase("ativo")).thenReturn(existente);

		StatusMembro novo = new StatusMembro();
		novo.setDescricao("ativo");
		assertThrows(BusinessRuleException.class, () -> service.cadastrar(novo));
		verify(repository, never()).save(any());
	}

	@Test
	void removerRejeitaStatusEmUso() {
		when(repository.findById(1)).thenReturn(new StatusMembro());
		when(membroRepository.existsByStatus_Id(1)).thenReturn(true);
		assertThrows(BusinessRuleException.class, () -> service.remover(1));
		verify(repository, never()).deleteById(any());
	}
}
