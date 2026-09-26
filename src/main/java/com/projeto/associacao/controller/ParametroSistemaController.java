package com.projeto.associacao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.associacao.dto.parametroSistema.ParametroSistemaRequest;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.ParametroSistema;
import com.projeto.associacao.service.ParametroSistemaService;

@RestController
@RequestMapping("/api/parametro-sistema")
public class ParametroSistemaController {

	@Autowired
	private ParametroSistemaService service;

	@GetMapping("/")
	public Iterable<ParametroSistema> selecionar() {
		return service.selecionar();
	}

	@PutMapping("/")
	public ParametroSistema alterar(@RequestBody ParametroSistemaRequest request) throws BusinessRuleException {
		return service.alterar(request);
	}
}
