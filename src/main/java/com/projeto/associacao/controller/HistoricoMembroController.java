package com.projeto.associacao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.associacao.dto.historicoMembro.HistoricoMembroRequest;
import com.projeto.associacao.dto.historicoMembro.HistoricoMembroResponse;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.service.HistoricoMembroService;

@RestController
@RequestMapping("/api/historico-membro")
public class HistoricoMembroController {

	@Autowired
	private HistoricoMembroService service;

	@GetMapping("/")
	public Iterable<HistoricoMembroResponse> selecionar() {
		return service.selecionar();
	}

	@GetMapping("/membro/{idMembro}")
	public Iterable<HistoricoMembroResponse> selecionarPorMembro(@PathVariable int idMembro) throws BusinessRuleException {
		return service.selecionarPorMembro(idMembro);
	}

	@PostMapping("/")
	public HistoricoMembroResponse cadastrar(@RequestBody HistoricoMembroRequest request, Authentication authentication) throws BusinessRuleException {
		return service.cadastrar(request, authentication.getName());
	}

	@PutMapping("/")
	public HistoricoMembroResponse alterar(@RequestBody HistoricoMembroRequest request, Authentication authentication) throws BusinessRuleException {
		return service.alterar(request, authentication.getName());
	}

	@DeleteMapping("/{id}")
	public void remover(@PathVariable int id) throws BusinessRuleException {
		service.remover(id);
	}
}
