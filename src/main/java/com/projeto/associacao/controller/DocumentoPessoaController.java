package com.projeto.associacao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.DocumentoPessoa;
import com.projeto.associacao.service.DocumentoPessoaService;

@RestController
@RequestMapping("/api/documento-pessoa")
public class DocumentoPessoaController {

	@Autowired
	private DocumentoPessoaService service;

	@GetMapping("/{id}/download")
	public ResponseEntity<byte[]> download(@PathVariable int id) throws BusinessRuleException {
		DocumentoPessoa documento = service.buscarParaDownloadOuFalhar(id);
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(documento.getContentType()))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + documento.getNomeOriginal() + "\"")
				.body(documento.getArquivo());
	}

	@DeleteMapping("/{id}")
	public void remover(@PathVariable int id) throws BusinessRuleException {
		service.remover(id);
	}
}
