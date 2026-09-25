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
import com.projeto.associacao.model.Produto;
import com.projeto.associacao.service.ProdutoService;

@RestController
@RequestMapping("/api/produto")
public class ProdutoController {

	@Autowired
	private ProdutoService service;

	@GetMapping("/")
	public Iterable<Produto> selecionar() {
		return service.selecionar();
	}

	@PostMapping("/")
	public Produto cadastrar(@RequestBody Produto produto) throws BusinessRuleException {
		return service.cadastrar(produto);
	}

	@PutMapping("/")
	public Produto alterar(@RequestBody Produto produto) throws BusinessRuleException {
		return service.alterar(produto);
	}

	@DeleteMapping("/{id}")
	public void remover(@PathVariable int id) throws BusinessRuleException {
		service.remover(id);
	}
}
