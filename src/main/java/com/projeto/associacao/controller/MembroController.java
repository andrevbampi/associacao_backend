package com.projeto.associacao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.associacao.dto.membro.MembroRequest;
import com.projeto.associacao.dto.membro.MembroResponse;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.service.MembroService;

@RestController
@RequestMapping("/api/membro")
public class MembroController {

	@Autowired
	private MembroService service;

	@GetMapping("/")
	public Iterable<MembroResponse> selecionar(
			@RequestParam(required = false) String nomePessoa,
			@RequestParam(required = false) Integer idStatus,
			@RequestParam(required = false) Boolean ativo) {
		return service.selecionar(nomePessoa, idStatus, ativo);
	}
	
	@PostMapping("/")
	public MembroResponse cadastrar(@RequestBody MembroRequest request) throws BusinessRuleException {
		return service.cadastrar(request);
	}
	
	@PutMapping("/")
	public MembroResponse alterar(@RequestBody MembroRequest request) throws BusinessRuleException {
		return service.alterar(request);
	}
	
	@DeleteMapping("/{id}")
	public void remover(@PathVariable int id) throws BusinessRuleException {
		service.remover(id);
	}
}
