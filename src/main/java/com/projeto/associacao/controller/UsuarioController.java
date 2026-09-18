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

import com.projeto.associacao.dto.usuario.UsuarioRequest;
import com.projeto.associacao.dto.usuario.UsuarioResponse;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.Usuario;
import com.projeto.associacao.service.UsuarioService;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

	@Autowired
	private UsuarioService service;
	
	@GetMapping("/")
	public Iterable<UsuarioResponse> selecionar() {
		return service.selecionar();
	}
	
	@PostMapping("/")
	public UsuarioResponse cadastrar(@RequestBody UsuarioRequest request) throws BusinessRuleException {
		return service.cadastrar(request);
	}
	
	@PutMapping("/")
	public UsuarioResponse alterar(@RequestBody UsuarioRequest request) throws BusinessRuleException {
		return service.alterar(request);
	}
	
	@DeleteMapping("/{id}")
	public void remover(@PathVariable int id) throws BusinessRuleException {
		service.remover(id);
	}
}
