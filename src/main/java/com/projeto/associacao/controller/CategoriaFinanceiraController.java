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
import com.projeto.associacao.model.CategoriaFinanceira;
import com.projeto.associacao.service.CategoriaFinanceiraService;

@RestController
@RequestMapping("/api/categoria-financeira")
public class CategoriaFinanceiraController {

	@Autowired
	private CategoriaFinanceiraService service;

	@GetMapping("/")
	public Iterable<CategoriaFinanceira> selecionar() {
		return service.selecionar();
	}

	@PostMapping("/")
	public CategoriaFinanceira cadastrar(@RequestBody CategoriaFinanceira categoria) throws BusinessRuleException {
		return service.cadastrar(categoria);
	}

	@PutMapping("/")
	public CategoriaFinanceira alterar(@RequestBody CategoriaFinanceira categoria) throws BusinessRuleException {
		return service.alterar(categoria);
	}

	@DeleteMapping("/{id}")
	public void remover(@PathVariable int id) throws BusinessRuleException {
		service.remover(id);
	}
}
