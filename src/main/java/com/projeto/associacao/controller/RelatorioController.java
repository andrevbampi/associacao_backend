package com.projeto.associacao.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.associacao.dto.relatorio.RelatorioConsumoProdutoResponse;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.service.RelatorioConsumoProdutoService;

@RestController
@RequestMapping("/api/relatorio")
public class RelatorioController {

	@Autowired
	private RelatorioConsumoProdutoService service;

	@GetMapping("/consumo-produtos")
	public RelatorioConsumoProdutoResponse consumoProdutos(
			@RequestParam(required = false) Boolean apenasPessoasCadastradas,
			@RequestParam(required = false) Integer idPessoa,
			@RequestParam(required = false) String nomeTemporario,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataAberturaInicio,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataAberturaFim,
			@RequestParam(required = false) Integer idProduto,
			@RequestParam(required = false) Integer idCategoriaProduto,
			@RequestParam(required = false) String status,
			@RequestParam(defaultValue = "false") boolean agruparPorMes,
			@RequestParam(defaultValue = "false") boolean agruparPorPessoa,
			@RequestParam(defaultValue = "false") boolean agruparPorStatus) throws BusinessRuleException {
		return service.gerarConsumoProdutos(apenasPessoasCadastradas, idPessoa, nomeTemporario, dataAberturaInicio,
				dataAberturaFim, idProduto, idCategoriaProduto, status, agruparPorMes, agruparPorPessoa, agruparPorStatus);
	}
}
