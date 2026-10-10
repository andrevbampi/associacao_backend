package com.projeto.associacao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.CategoriaProduto;
import com.projeto.associacao.service.CategoriaProdutoService;
import com.projeto.associacao.security.Permissoes;

@RestController
@RequestMapping("/api/categoria-produto")
public class CategoriaProdutoController {

	@Autowired
	private CategoriaProdutoService service;

	@GetMapping("/")
	@PreAuthorize(Permissoes.LOOKUP_CATEGORIA_PRODUTO)
	public Iterable<CategoriaProduto> selecionar() {
		return service.selecionar();
	}

	@PostMapping("/")
	@PreAuthorize("hasAuthority('" + Permissoes.CATEGORIA_PRODUTO_CRIAR + "')")
	public CategoriaProduto cadastrar(@RequestBody CategoriaProduto categoria) throws BusinessRuleException {
		return service.cadastrar(categoria);
	}

	@PutMapping("/")
	@PreAuthorize("hasAuthority('" + Permissoes.CATEGORIA_PRODUTO_EDITAR + "')")
	public CategoriaProduto alterar(@RequestBody CategoriaProduto categoria) throws BusinessRuleException {
		return service.alterar(categoria);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('" + Permissoes.CATEGORIA_PRODUTO_EXCLUIR + "')")
	public void remover(@PathVariable int id) throws BusinessRuleException {
		service.remover(id);
	}
}
