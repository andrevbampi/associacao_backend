package com.projeto.associacao.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.CredenciaisInvalidasException;

@ControllerAdvice
public class CustomExceptionHandler {

	@ExceptionHandler(BusinessRuleException.class)
	public ResponseEntity<String> handleBusinessRuleException(BusinessRuleException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<String> handleAcessoNegado(AccessDeniedException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não tem permissão para realizar esta ação.");
	}

	@ExceptionHandler(CredenciaisInvalidasException.class)
	public ResponseEntity<String> handleCredenciaisInvalidas(CredenciaisInvalidasException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
	}
}
