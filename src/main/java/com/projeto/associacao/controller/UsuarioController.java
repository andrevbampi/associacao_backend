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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.associacao.dto.usuario.UsuarioRequest;
import com.projeto.associacao.dto.usuario.UsuarioResponse;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.Usuario;
import com.projeto.associacao.service.UsuarioService;
import com.projeto.associacao.security.Permissoes;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

	@Autowired
	private UsuarioService service;

	@GetMapping("/")
	@PreAuthorize("hasAuthority('" + Permissoes.USUARIO_VISUALIZAR + "')")
	public Iterable<UsuarioResponse> selecionar(
			@RequestParam(required = false) String nomePessoa,
			@RequestParam(required = false) Boolean ativo) {
		return service.selecionar(nomePessoa, ativo);
	}
	
	@PostMapping("/")
	@PreAuthorize("hasAuthority('" + Permissoes.USUARIO_CRIAR + "')")
	public UsuarioResponse cadastrar(@RequestBody UsuarioRequest request) throws BusinessRuleException {
		return service.cadastrar(request);
	}
	
	@PutMapping("/")
	@PreAuthorize("hasAuthority('" + Permissoes.USUARIO_EDITAR + "')")
	public UsuarioResponse alterar(@RequestBody UsuarioRequest request) throws BusinessRuleException {
		return service.alterar(request);
	}
	
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('" + Permissoes.USUARIO_EXCLUIR + "')")
	public void remover(@PathVariable int id) throws BusinessRuleException {
		service.remover(id);
	}
}
