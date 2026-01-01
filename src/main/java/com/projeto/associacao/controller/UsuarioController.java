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
import com.projeto.associacao.model.Usuario;
import com.projeto.associacao.service.UsuarioService;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

	@Autowired
	private UsuarioService service;
	
	@GetMapping("/")
	public Iterable<Usuario> selecionar() {
		return service.selecionar();
	}
	
	@PostMapping("/")
	public Usuario cadastrar(@RequestBody Usuario usuario) throws BusinessRuleException {
		return service.cadastrar(usuario);
	}
	
	@PutMapping("/")
	public Usuario alterar(@RequestBody Usuario usuario) throws BusinessRuleException {
		return service.alterar(usuario);
	}
	
	@DeleteMapping("/{id}")
	public void remover(@PathVariable int id) throws BusinessRuleException {
		service.remover(id);
	}
}
