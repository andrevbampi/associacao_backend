package com.projeto.associacao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.Caixa;
import com.projeto.associacao.service.CaixaService;

@RestController
@RequestMapping("/api/caixa")
public class CaixaController {

	@Autowired
	private CaixaService service;

	@GetMapping("/")
	public Iterable<Caixa> selecionar() {
		return service.selecionar();
	}

	@PostMapping("/")
	public Caixa cadastrar(@RequestBody Caixa caixa) throws BusinessRuleException {
		return service.cadastrar(caixa);
	}

	@PutMapping("/")
	public Caixa alterar(@RequestBody Caixa caixa) throws BusinessRuleException {
		return service.alterar(caixa);
	}

	@DeleteMapping("/{id}")
	public void remover(@PathVariable int id) throws BusinessRuleException {
		service.remover(id);
	}
}
