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
import com.projeto.associacao.model.TipoEvento;
import com.projeto.associacao.repository.HistoricoMembroRepository;
import com.projeto.associacao.repository.TipoEventoRepository;

@ExtendWith(MockitoExtension.class)
class TipoEventoServiceTest {

	@Mock
	private TipoEventoRepository repository;

	@Mock
	private HistoricoMembroRepository historicoMembroRepository;

	@InjectMocks
	private TipoEventoService service;

	@Test
	void cadastrarRejeitaDescricaoEmBranco() {
		TipoEvento tipoEvento = new TipoEvento();
		tipoEvento.setDescricao("  ");
		assertThrows(BusinessRuleException.class, () -> service.cadastrar(tipoEvento));
	}

	@Test
	void cadastrarRejeitaDescricaoDuplicada() {
		TipoEvento existente = new TipoEvento();
		existente.setId(1);
		existente.setDescricao("Renovação");
		when(repository.findByDescricaoIgnoreCase("renovação")).thenReturn(existente);

		TipoEvento novo = new TipoEvento();
		novo.setDescricao("renovação");
		assertThrows(BusinessRuleException.class, () -> service.cadastrar(novo));
		verify(repository, never()).save(any());
	}

	@Test
	void removerRejeitaTipoEmUso() {
		when(repository.findById(1)).thenReturn(new TipoEvento());
		when(historicoMembroRepository.existsByTipoEvento_Id(1)).thenReturn(true);
		assertThrows(BusinessRuleException.class, () -> service.remover(1));
		verify(repository, never()).deleteById(any());
	}

	@Test
	void removerRejeitaTipoInexistente() {
		when(repository.findById(9)).thenReturn(null);
		assertThrows(BusinessRuleException.class, () -> service.remover(9));
	}
}
