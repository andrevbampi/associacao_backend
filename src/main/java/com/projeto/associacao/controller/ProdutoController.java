package com.projeto.associacao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import com.projeto.associacao.dto.produto.ProdutoRequest;
import com.projeto.associacao.dto.produto.ProdutoResponse;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.Produto;
import com.projeto.associacao.service.ProdutoService;

@RestController
@RequestMapping("/api/produto")
public class ProdutoController {

	@Autowired
	private ProdutoService service;

	@GetMapping("/")
	public Iterable<ProdutoResponse> selecionar(
			@RequestParam(required = false) String descricao,
			@RequestParam(required = false) Integer idCategoria,
			@RequestParam(required = false) Boolean ativo) {
		return service.selecionar(descricao, idCategoria, ativo);
	}

	@PostMapping("/")
	public ProdutoResponse cadastrar(@RequestBody ProdutoRequest request) throws BusinessRuleException {
		return service.cadastrar(request);
	}

	@PutMapping("/")
	public ProdutoResponse alterar(@RequestBody ProdutoRequest request) throws BusinessRuleException {
		return service.alterar(request);
	}

	@DeleteMapping("/{id}")
	public void remover(@PathVariable int id) throws BusinessRuleException {
		service.remover(id);
	}

	@GetMapping("/{id}/foto")
	public ResponseEntity<byte[]> buscarFoto(@PathVariable int id) throws BusinessRuleException {
		Produto produto = service.buscarOuFalhar(id);
		if ((produto.getFoto() == null) || (produto.getFoto().length == 0)) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(produto.getFotoContentType()))
				.body(produto.getFoto());
	}

	@PutMapping("/{id}/foto")
	public void salvarFoto(@PathVariable int id, @RequestParam("arquivo") MultipartFile arquivo) throws BusinessRuleException {
		service.salvarFoto(id, arquivo);
	}

	@DeleteMapping("/{id}/foto")
	public void removerFoto(@PathVariable int id) throws BusinessRuleException {
		service.removerFoto(id);
	}
}
