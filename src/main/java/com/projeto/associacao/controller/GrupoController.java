package com.projeto.associacao.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.associacao.dto.acesso.GrupoRequest;
import com.projeto.associacao.dto.acesso.GrupoResponse;
import com.projeto.associacao.dto.acesso.PermissaoResponse;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.security.Permissoes;
import com.projeto.associacao.service.GrupoService;

@RestController
@RequestMapping("/api/grupo")
public class GrupoController {

	@Autowired
	private GrupoService service;

	@GetMapping("/")
	@PreAuthorize(Permissoes.LOOKUP_GRUPO)
	public Iterable<GrupoResponse> selecionar() {
		return service.selecionar();
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('" + Permissoes.GRUPO_VISUALIZAR + "')")
	public GrupoResponse buscarPorId(@PathVariable int id) throws BusinessRuleException {
		return service.buscarPorId(id);
	}

	/** Catálogo de permissões existentes (código, módulo e descrição). */
	@GetMapping("/permissoes")
	@PreAuthorize(Permissoes.LOOKUP_GRUPO)
	public List<PermissaoResponse> permissoes() {
		return service.listarPermissoes();
	}

	@PostMapping("/")
	@PreAuthorize("hasAuthority('" + Permissoes.GRUPO_CRIAR + "')")
	public GrupoResponse cadastrar(@RequestBody GrupoRequest request, Authentication authentication) throws BusinessRuleException {
		return service.cadastrar(request, authentication.getName());
	}

	@PutMapping("/")
	@PreAuthorize("hasAuthority('" + Permissoes.GRUPO_EDITAR + "')")
	public GrupoResponse alterar(@RequestBody GrupoRequest request, Authentication authentication) throws BusinessRuleException {
		return service.alterar(request, authentication.getName());
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('" + Permissoes.GRUPO_EXCLUIR + "')")
	public void remover(@PathVariable int id, Authentication authentication) throws BusinessRuleException {
		service.remover(id, authentication.getName());
	}

}
