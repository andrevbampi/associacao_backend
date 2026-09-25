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

import com.projeto.associacao.dto.comanda.ComandaAberturaRequest;
import com.projeto.associacao.dto.comanda.ComandaFechamentoRequest;
import com.projeto.associacao.dto.comanda.ComandaResponse;
import com.projeto.associacao.dto.comanda.ItemComandaRequest;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.service.ComandaService;

@RestController
@RequestMapping("/api/comanda")
public class ComandaController {

	@Autowired
	private ComandaService service;

	@GetMapping("/")
	public Iterable<ComandaResponse> selecionar(@RequestParam(required = false) String status) throws BusinessRuleException {
		return service.selecionar(status);
	}

	@GetMapping("/{id}")
	public ComandaResponse buscarPorId(@PathVariable int id) throws BusinessRuleException {
		return service.buscarPorId(id);
	}

	@PostMapping("/")
	public ComandaResponse abrir(@RequestBody ComandaAberturaRequest request) throws BusinessRuleException {
		return service.abrir(request);
	}

	@PostMapping("/{id}/itens")
	public ComandaResponse adicionarItem(@PathVariable int id, @RequestBody ItemComandaRequest request) throws BusinessRuleException {
		return service.adicionarItem(id, request);
	}

	@PutMapping("/{id}/itens/{idItem}")
	public ComandaResponse alterarItem(@PathVariable int id, @PathVariable int idItem, @RequestBody ItemComandaRequest request) throws BusinessRuleException {
		return service.alterarItem(id, idItem, request);
	}

	@DeleteMapping("/{id}/itens/{idItem}")
	public ComandaResponse removerItem(@PathVariable int id, @PathVariable int idItem) throws BusinessRuleException {
		return service.removerItem(id, idItem);
	}

	@PutMapping("/{id}/fechar")
	public ComandaResponse fechar(@PathVariable int id, @RequestBody ComandaFechamentoRequest request) throws BusinessRuleException {
		return service.fechar(id, request);
	}

	@PutMapping("/{id}/cancelar")
	public ComandaResponse cancelar(@PathVariable int id) throws BusinessRuleException {
		return service.cancelar(id);
	}
}
