package com.projeto.associacao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.projeto.associacao.dto.parametroSistema.ParametroSistemaRequest;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.ParametroSistema;
import com.projeto.associacao.service.LogoAssociacaoService;
import com.projeto.associacao.service.ParametroSistemaService;

@RestController
@RequestMapping("/api/parametro-sistema")
public class ParametroSistemaController {

	@Autowired
	private ParametroSistemaService service;

	@Autowired
	private LogoAssociacaoService logoService;

	@GetMapping("/")
	public Iterable<ParametroSistema> selecionar() {
		return service.selecionar();
	}

	@PutMapping("/")
	public ParametroSistema alterar(@RequestBody ParametroSistemaRequest request) throws BusinessRuleException {
		return service.alterar(request);
	}

	@PutMapping("/logo")
	public void salvarLogo(@RequestParam("arquivo") MultipartFile arquivo) throws BusinessRuleException {
		logoService.salvar(arquivo);
	}

	@DeleteMapping("/logo")
	public void removerLogo() {
		logoService.remover();
	}
}
