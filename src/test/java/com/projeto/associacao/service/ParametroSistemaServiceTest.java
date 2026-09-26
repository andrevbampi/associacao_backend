package com.projeto.associacao.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projeto.associacao.model.ParametroSistema;
import com.projeto.associacao.repository.ParametroSistemaRepository;

@ExtendWith(MockitoExtension.class)
class ParametroSistemaServiceTest {

	@Mock
	private ParametroSistemaRepository repository;

	@InjectMocks
	private ParametroSistemaService service;

	private ParametroSistema parametro(String chave, String valor) {
		ParametroSistema parametro = new ParametroSistema();
		parametro.setChave(chave);
		parametro.setValor(valor);
		return parametro;
	}

	@Test
	void buscarValorDevolveNuloQuandoParametroNaoExiste() {
		when(repository.findByChave("INEXISTENTE")).thenReturn(null);
		assertNull(service.buscarValor("INEXISTENTE"));
	}

	@Test
	void buscarValorDevolveNuloQuandoValorEmBranco() {
		when(repository.findByChave("DOCUMENTO_ASSOCIACAO")).thenReturn(parametro("DOCUMENTO_ASSOCIACAO", "  "));
		assertNull(service.buscarValor("DOCUMENTO_ASSOCIACAO"));
	}

	@Test
	void buscarValorInteiroDevolveNuloQuandoZerado() {
		when(repository.findByChave("CAIXA_COMANDA")).thenReturn(parametro("CAIXA_COMANDA", "0"));
		assertNull(service.buscarValorInteiro("CAIXA_COMANDA"));
	}

	@Test
	void buscarValorInteiroDevolveNuloQuandoNaoNumerico() {
		when(repository.findByChave("CAIXA_COMANDA")).thenReturn(parametro("CAIXA_COMANDA", "abc"));
		assertNull(service.buscarValorInteiro("CAIXA_COMANDA"));
	}

	@Test
	void buscarValorInteiroDevolveValorQuandoValido() {
		when(repository.findByChave("CAIXA_COMANDA")).thenReturn(parametro("CAIXA_COMANDA", "3"));
		assertEquals(3, service.buscarValorInteiro("CAIXA_COMANDA"));
	}

}
