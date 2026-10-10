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
import com.projeto.associacao.model.StatusMembro;
import com.projeto.associacao.service.StatusMembroService;
import com.projeto.associacao.security.Permissoes;

@RestController
@RequestMapping("/api/status-membro")
public class StatusMembroController {

	@Autowired
	private StatusMembroService service;

	@GetMapping("/")
	@PreAuthorize(Permissoes.LOOKUP_STATUS_MEMBRO)
	public Iterable<StatusMembro> selecionar() {
		return service.selecionar();
	}

	@PostMapping("/")
	@PreAuthorize("hasAuthority('" + Permissoes.STATUS_MEMBRO_CRIAR + "')")
	public StatusMembro cadastrar(@RequestBody StatusMembro status) throws BusinessRuleException {
		return service.cadastrar(status);
	}

	@PutMapping("/")
	@PreAuthorize("hasAuthority('" + Permissoes.STATUS_MEMBRO_EDITAR + "')")
	public StatusMembro alterar(@RequestBody StatusMembro status) throws BusinessRuleException {
		return service.alterar(status);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('" + Permissoes.STATUS_MEMBRO_EXCLUIR + "')")
	public void remover(@PathVariable int id) throws BusinessRuleException {
		service.remover(id);
	}
}
