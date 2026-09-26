package com.projeto.associacao.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.associacao.dto.financeiro.LancamentoFinanceiroRequest;
import com.projeto.associacao.dto.financeiro.LancamentoFinanceiroResponse;
import com.projeto.associacao.dto.financeiro.ResumoFinanceiroResponse;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.service.LancamentoFinanceiroService;

@RestController
@RequestMapping("/api/lancamento-financeiro")
public class LancamentoFinanceiroController {

	@Autowired
	private LancamentoFinanceiroService service;

	@GetMapping("/")
	public Iterable<LancamentoFinanceiroResponse> selecionar(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
			@RequestParam(required = false) Integer idCategoriaFinanceira,
			@RequestParam(required = false) String tipo,
			@RequestParam(required = false) Boolean pago) throws BusinessRuleException {
		return service.selecionar(dataInicio, dataFim, idCategoriaFinanceira, tipo, pago);
	}

	@GetMapping("/resumo")
	public ResumoFinanceiroResponse resumo(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
		return service.resumo(dataInicio, dataFim);
	}

	@PostMapping("/")
	public LancamentoFinanceiroResponse cadastrar(@RequestBody LancamentoFinanceiroRequest request, Authentication authentication) throws BusinessRuleException {
		return service.cadastrar(request, authentication.getName());
	}

	@PutMapping("/")
	public LancamentoFinanceiroResponse alterar(@RequestBody LancamentoFinanceiroRequest request, Authentication authentication) throws BusinessRuleException {
		return service.alterar(request, authentication.getName());
	}

	@PutMapping("/{id}/pagamento")
	public LancamentoFinanceiroResponse registrarPagamento(@PathVariable int id, @RequestBody(required = false) LancamentoFinanceiroRequest request) throws BusinessRuleException {
		return service.registrarPagamento(id, request != null ? request.getFormaPagamento() : null);
	}

	@DeleteMapping("/{id}")
	public void remover(@PathVariable int id) throws BusinessRuleException {
		service.remover(id);
	}
}
