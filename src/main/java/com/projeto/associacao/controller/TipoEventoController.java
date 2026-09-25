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
import com.projeto.associacao.model.TipoEvento;
import com.projeto.associacao.service.TipoEventoService;

@RestController
@RequestMapping("/api/tipo-evento")
public class TipoEventoController {

	@Autowired
	private TipoEventoService service;

	@GetMapping("/")
	public Iterable<TipoEvento> selecionar() {
		return service.selecionar();
	}

	@PostMapping("/")
	public TipoEvento cadastrar(@RequestBody TipoEvento tipoEvento) throws BusinessRuleException {
		return service.cadastrar(tipoEvento);
	}

	@PutMapping("/")
	public TipoEvento alterar(@RequestBody TipoEvento tipoEvento) throws BusinessRuleException {
		return service.alterar(tipoEvento);
	}

	@DeleteMapping("/{id}")
	public void remover(@PathVariable int id) throws BusinessRuleException {
		service.remover(id);
	}
}
