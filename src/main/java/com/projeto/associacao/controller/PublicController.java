package com.projeto.associacao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.associacao.dto.publico.PublicConfigResponse;
import com.projeto.associacao.model.LogoAssociacao;
import com.projeto.associacao.service.LogoAssociacaoService;
import com.projeto.associacao.service.ParametroSistemaService;

/**
 * Único controller com endpoints públicos (sem JWT) — usados pela tela de
 * login, antes de o usuário se autenticar. Liberado explicitamente no
 * SecurityConfig; nunca expõe nada além do nome/logo da associação.
 */
@RestController
@RequestMapping("/api/public")
public class PublicController {

	@Autowired
	private ParametroSistemaService parametroSistemaService;

	@Autowired
	private LogoAssociacaoService logoService;

	@GetMapping("/config")
	public PublicConfigResponse config() {
		PublicConfigResponse response = new PublicConfigResponse();
		String nome = parametroSistemaService.buscarValor(ParametroSistemaService.NOME_ASSOCIACAO);
		response.setNomeAssociacao(nome != null ? nome : "Associação");
		// Caminho relativo à base da API usada pelo front (que já inclui "/api"),
		// não o caminho completo do endpoint — senão o front duplicaria o "/api".
		response.setLogoUrl(logoService.buscar() != null ? "/public/logo" : null);
		return response;
	}

	@GetMapping("/logo")
	public ResponseEntity<byte[]> logo() {
		LogoAssociacao logo = logoService.buscar();
		if (logo == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(logo.getContentType()))
				.body(logo.getArquivo());
	}
}
