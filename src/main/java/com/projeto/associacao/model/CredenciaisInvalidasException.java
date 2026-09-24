package com.projeto.associacao.model;

/**
 * Lançada quando o login ou a senha informados no login não conferem,
 * ou quando o usuário não pode se autenticar (ex.: inativo).
 * Não relacionada a uma regra de negócio de cadastro (BusinessRuleException),
 * por isso vira HTTP 401 em vez de 400.
 */
public class CredenciaisInvalidasException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CredenciaisInvalidasException(String message) {
		super(message);
	}

}
