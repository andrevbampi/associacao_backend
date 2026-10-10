package com.projeto.associacao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import com.projeto.associacao.dto.comanda.ComandaAberturaRequest;
import com.projeto.associacao.dto.comanda.ComandaFechamentoRequest;
import com.projeto.associacao.dto.comanda.ComandaPagamentoRequest;
import com.projeto.associacao.dto.comanda.ComandaResponse;
import com.projeto.associacao.dto.comanda.ItemComandaRequest;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.service.ComandaService;
import com.projeto.associacao.security.Permissoes;

@RestController
@RequestMapping("/api/comanda")
public class ComandaController {

	@Autowired
	private ComandaService service;

	@GetMapping("/")
	@PreAuthorize("hasAuthority('" + Permissoes.COMANDA_VISUALIZAR + "')")
	public Iterable<ComandaResponse> selecionar(
			@RequestParam(required = false) String status,
			@RequestParam(required = false) Integer idPessoa,
			@RequestParam(required = false) String nomeTemporario,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataAberturaInicio,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataAberturaFim,
			@RequestParam(required = false) Boolean pago,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataPagamentoInicio,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataPagamentoFim) throws BusinessRuleException {
		return service.selecionar(status, idPessoa, nomeTemporario, dataAberturaInicio, dataAberturaFim, pago, dataPagamentoInicio, dataPagamentoFim);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('" + Permissoes.COMANDA_VISUALIZAR + "')")
	public ComandaResponse buscarPorId(@PathVariable int id) throws BusinessRuleException {
		return service.buscarPorId(id);
	}

	@PostMapping("/")
	@PreAuthorize("hasAuthority('" + Permissoes.COMANDA_ABRIR + "')")
	public ComandaResponse abrir(@RequestBody ComandaAberturaRequest request) throws BusinessRuleException {
		return service.abrir(request);
	}

	@PostMapping("/{id}/itens")
	@PreAuthorize("hasAuthority('" + Permissoes.COMANDA_LANCAR_ITEM + "')")
	public ComandaResponse adicionarItem(@PathVariable int id, @RequestBody ItemComandaRequest request) throws BusinessRuleException {
		return service.adicionarItem(id, request);
	}

	@PutMapping("/{id}/itens/{idItem}")
	@PreAuthorize("hasAuthority('" + Permissoes.COMANDA_LANCAR_ITEM + "')")
	public ComandaResponse alterarItem(@PathVariable int id, @PathVariable int idItem, @RequestBody ItemComandaRequest request) throws BusinessRuleException {
		return service.alterarItem(id, idItem, request);
	}

	@DeleteMapping("/{id}/itens/{idItem}")
	@PreAuthorize("hasAuthority('" + Permissoes.COMANDA_LANCAR_ITEM + "')")
	public ComandaResponse removerItem(@PathVariable int id, @PathVariable int idItem) throws BusinessRuleException {
		return service.removerItem(id, idItem);
	}

	@PutMapping("/{id}/fechar")
	// Fechar já registrando o pagamento (pago = true) também exige a permissão de receber pagamento.
	@PreAuthorize("hasAuthority('" + Permissoes.COMANDA_FECHAR + "') and (!#request.pago or hasAuthority('" + Permissoes.COMANDA_RECEBER_PAGAMENTO + "'))")
	public ComandaResponse fechar(@PathVariable int id, @RequestBody ComandaFechamentoRequest request, Authentication authentication) throws BusinessRuleException {
		return service.fechar(id, request, authentication.getName());
	}

	@PutMapping("/{id}/pagamento")
	@PreAuthorize("hasAuthority('" + Permissoes.COMANDA_RECEBER_PAGAMENTO + "')")
	public ComandaResponse registrarPagamento(@PathVariable int id, @RequestBody(required = false) ComandaPagamentoRequest request, Authentication authentication) throws BusinessRuleException {
		return service.registrarPagamento(id, request != null ? request.getFormaPagamento() : null, request != null ? request.getIdCaixa() : null, authentication.getName());
	}

	@PutMapping("/{id}/desfazer-pagamento")
	@PreAuthorize("hasAuthority('" + Permissoes.COMANDA_DESFAZER_PAGAMENTO + "')")
	public ComandaResponse desfazerPagamento(@PathVariable int id, Authentication authentication) throws BusinessRuleException {
		return service.desfazerPagamento(id, authentication.getName());
	}

	@PutMapping("/{id}/desfazer-fechamento")
	@PreAuthorize("hasAuthority('" + Permissoes.COMANDA_DESFAZER_FECHAMENTO + "')")
	public ComandaResponse desfazerFechamento(@PathVariable int id, Authentication authentication) throws BusinessRuleException {
		return service.desfazerFechamento(id, authentication.getName());
	}

	@PutMapping("/{id}/cancelar")
	@PreAuthorize("hasAuthority('" + Permissoes.COMANDA_CANCELAR + "')")
	public ComandaResponse cancelar(@PathVariable int id) throws BusinessRuleException {
		return service.cancelar(id);
	}
}
