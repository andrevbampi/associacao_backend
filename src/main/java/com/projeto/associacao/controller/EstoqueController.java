package com.projeto.associacao.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.associacao.dto.estoque.MovimentoEstoqueRequest;
import com.projeto.associacao.dto.estoque.MovimentoEstoqueResponse;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.service.EstoqueService;

@RestController
@RequestMapping("/api/estoque")
public class EstoqueController {

	@Autowired
	private EstoqueService service;

	@GetMapping("/movimentos")
	public Iterable<MovimentoEstoqueResponse> selecionar(
			@RequestParam(required = false) Integer idProduto,
			@RequestParam(required = false) String tipo,
			@RequestParam(required = false) String origem,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) throws BusinessRuleException {
		return service.selecionar(idProduto, tipo, origem, dataInicio, dataFim);
	}

	@PostMapping("/movimentos")
	public MovimentoEstoqueResponse lancar(@RequestBody MovimentoEstoqueRequest request, Authentication authentication) throws BusinessRuleException {
		return service.lancar(request, authentication.getName());
	}
}
