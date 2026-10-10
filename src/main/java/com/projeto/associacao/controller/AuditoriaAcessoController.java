package com.projeto.associacao.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.associacao.dto.acesso.AuditoriaAcessoResponse;
import com.projeto.associacao.security.Permissoes;
import com.projeto.associacao.service.AuditoriaAcessoService;

@RestController
@RequestMapping("/api/auditoria-acesso")
public class AuditoriaAcessoController {

	@Autowired
	private AuditoriaAcessoService service;

	@GetMapping("/")
	@PreAuthorize("hasAuthority('" + Permissoes.AUDITORIA_VISUALIZAR + "')")
	public Iterable<AuditoriaAcessoResponse> selecionar(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
			@RequestParam(required = false) String entidade,
			@RequestParam(required = false) String login) {
		return service.selecionar(dataInicio, dataFim, entidade, login);
	}

}
