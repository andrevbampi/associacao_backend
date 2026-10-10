package com.projeto.associacao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.associacao.dto.acesso.UsuarioAcessoRequest;
import com.projeto.associacao.dto.acesso.UsuarioAcessoResponse;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.security.Permissoes;
import com.projeto.associacao.service.UsuarioAcessoService;

@RestController
@RequestMapping("/api/usuario/{id}/acesso")
public class UsuarioAcessoController {

	@Autowired
	private UsuarioAcessoService service;

	@GetMapping
	@PreAuthorize("hasAuthority('" + Permissoes.USUARIO_GERENCIAR_ACESSO + "')")
	public UsuarioAcessoResponse obter(@PathVariable int id) throws BusinessRuleException {
		return service.obter(id);
	}

	@PutMapping
	@PreAuthorize("hasAuthority('" + Permissoes.USUARIO_GERENCIAR_ACESSO + "')")
	public UsuarioAcessoResponse salvar(@PathVariable int id, @RequestBody UsuarioAcessoRequest request, Authentication authentication)
			throws BusinessRuleException {
		return service.salvar(id, request, authentication.getName());
	}

}
