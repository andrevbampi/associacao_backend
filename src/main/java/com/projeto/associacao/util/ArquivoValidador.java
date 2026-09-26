package com.projeto.associacao.util;

import java.util.Set;

import com.projeto.associacao.model.BusinessRuleException;

/**
 * Validação de tipo e tamanho para uploads (fotos e documentos), sempre pelo
 * conteúdo declarado (contentType) do arquivo enviado — nunca confiando só na
 * extensão do nome original.
 */
public final class ArquivoValidador {

	public static final long TAMANHO_MAXIMO_BYTES = 10L * 1024 * 1024;

	private static final Set<String> TIPOS_IMAGEM = Set.of(
			"image/jpeg", "image/jpg", "image/png", "image/webp", "image/gif");

	private static final Set<String> TIPOS_DOCUMENTO = Set.of(
			"application/pdf", "image/jpeg", "image/jpg", "image/png", "image/webp", "image/gif");

	private ArquivoValidador() {
	}

	public static void validarImagem(String contentType, long tamanho) throws BusinessRuleException {
		validarTamanho(tamanho);
		if ((contentType == null) || !TIPOS_IMAGEM.contains(contentType.toLowerCase())) {
			throw new BusinessRuleException("Formato de imagem inválido. Use JPG, PNG, WEBP ou GIF.");
		}
	}

	public static void validarDocumento(String contentType, long tamanho) throws BusinessRuleException {
		validarTamanho(tamanho);
		if ((contentType == null) || !TIPOS_DOCUMENTO.contains(contentType.toLowerCase())) {
			throw new BusinessRuleException("Formato de arquivo inválido. Use PDF, JPG, JPEG, PNG, WEBP ou GIF.");
		}
	}

	private static void validarTamanho(long tamanho) throws BusinessRuleException {
		if (tamanho <= 0) {
			throw new BusinessRuleException("Arquivo vazio.");
		}
		if (tamanho > TAMANHO_MAXIMO_BYTES) {
			throw new BusinessRuleException("Arquivo maior que o limite permitido (10 MB).");
		}
	}

}
