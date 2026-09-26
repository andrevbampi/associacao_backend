package com.projeto.associacao.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.projeto.associacao.model.BusinessRuleException;

class ArquivoValidadorTest {

	@Test
	void validarImagemAceitaJpeg() {
		assertDoesNotThrow(() -> ArquivoValidador.validarImagem("image/jpeg", 1024));
	}

	@Test
	void validarImagemRejeitaPdf() {
		assertThrows(BusinessRuleException.class, () -> ArquivoValidador.validarImagem("application/pdf", 1024));
	}

	@Test
	void validarImagemRejeitaArquivoVazio() {
		assertThrows(BusinessRuleException.class, () -> ArquivoValidador.validarImagem("image/png", 0));
	}

	@Test
	void validarImagemRejeitaArquivoMuitoGrande() {
		assertThrows(BusinessRuleException.class, () -> ArquivoValidador.validarImagem("image/png", ArquivoValidador.TAMANHO_MAXIMO_BYTES + 1));
	}

	@Test
	void validarDocumentoAceitaPdf() {
		assertDoesNotThrow(() -> ArquivoValidador.validarDocumento("application/pdf", 1024));
	}

	@Test
	void validarDocumentoRejeitaTipoNaoPermitido() {
		assertThrows(BusinessRuleException.class, () -> ArquivoValidador.validarDocumento("application/zip", 1024));
	}

}
