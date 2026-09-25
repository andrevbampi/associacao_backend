package com.projeto.associacao.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DocumentoValidadorTest {

	@Test
	void aceitaCpfValido() {
		assertTrue(DocumentoValidador.validarCpf("111.444.777-35"));
		assertTrue(DocumentoValidador.validarCpf("52998224725"));
	}

	@Test
	void rejeitaCpfComDigitoVerificadorErrado() {
		assertFalse(DocumentoValidador.validarCpf("111.444.777-36"));
	}

	@Test
	void rejeitaCpfComTodosDigitosIguais() {
		assertFalse(DocumentoValidador.validarCpf("111.111.111-11"));
	}

	@Test
	void rejeitaCpfComTamanhoErrado() {
		assertFalse(DocumentoValidador.validarCpf("123"));
	}

	@Test
	void rejeitaCpfNulo() {
		assertFalse(DocumentoValidador.validarCpf(null));
	}

	@Test
	void aceitaCnpjNumericoValido() {
		assertTrue(DocumentoValidador.validarCnpj("11.222.333/0001-81"));
		assertTrue(DocumentoValidador.validarCnpj("11444777000161"));
	}

	@Test
	void rejeitaCnpjComDigitoVerificadorErrado() {
		assertFalse(DocumentoValidador.validarCnpj("11.222.333/0001-80"));
	}

	@Test
	void rejeitaCnpjComTodosCaracteresIguais() {
		assertFalse(DocumentoValidador.validarCnpj("00.000.000/0000-00"));
	}

	@Test
	void rejeitaCnpjComTamanhoErrado() {
		assertFalse(DocumentoValidador.validarCnpj("123"));
	}
}
