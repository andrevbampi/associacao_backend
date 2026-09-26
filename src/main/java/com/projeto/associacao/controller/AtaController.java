package com.projeto.associacao.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.multipart.MultipartFile;

import com.projeto.associacao.dto.ata.AtaRequest;
import com.projeto.associacao.dto.ata.AtaResponse;
import com.projeto.associacao.dto.documentoAta.DocumentoAtaResponse;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.service.AtaService;
import com.projeto.associacao.service.DocumentoAtaService;

@RestController
@RequestMapping("/api/ata")
public class AtaController {

	@Autowired
	private AtaService service;

	@Autowired
	private DocumentoAtaService documentoService;

	@GetMapping("/")
	public Iterable<AtaResponse> selecionar() {
		return service.selecionar();
	}

	@PostMapping("/")
	public AtaResponse cadastrar(@RequestBody AtaRequest request, Authentication authentication) throws BusinessRuleException {
		return service.cadastrar(request, authentication.getName());
	}

	@PutMapping("/")
	public AtaResponse alterar(@RequestBody AtaRequest request, Authentication authentication) throws BusinessRuleException {
		return service.alterar(request, authentication.getName());
	}

	@DeleteMapping("/{id}")
	public void remover(@PathVariable int id) throws BusinessRuleException {
		service.remover(id);
	}

	@GetMapping("/{id}/documentos")
	public Iterable<DocumentoAtaResponse> listarDocumentos(@PathVariable int id) throws BusinessRuleException {
		return documentoService.listar(id);
	}

	@PostMapping("/{id}/documentos")
	public DocumentoAtaResponse uploadDocumento(@PathVariable int id, @RequestParam("arquivo") MultipartFile arquivo, Authentication authentication) throws BusinessRuleException {
		return documentoService.upload(id, arquivo, authentication.getName());
	}
}
