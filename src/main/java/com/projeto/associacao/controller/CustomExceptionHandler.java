package com.projeto.associacao.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.projeto.associacao.model.BusinessRuleException;

@ControllerAdvice
public class CustomExceptionHandler {

	@ExceptionHandler(BusinessRuleException.class)
	public ResponseEntity<String> handleBusinessRuleException(BusinessRuleException ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
	}
}
