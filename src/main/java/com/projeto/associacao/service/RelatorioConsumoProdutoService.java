package com.projeto.associacao.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projeto.associacao.dto.relatorio.RelatorioConsumoProdutoLinha;
import com.projeto.associacao.dto.relatorio.RelatorioConsumoProdutoResponse;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.Comanda;
import com.projeto.associacao.model.ItemComanda;
import com.projeto.associacao.model.Produto;
import com.projeto.associacao.model.StatusComanda;
import com.projeto.associacao.repository.ItemComandaRepository;

/**
 * Relatório de consumo de produtos (quantidade e valor), abstraindo as
 * comandas e somando os totais a partir dos itens lançados. Comandas
 * canceladas nunca entram no cálculo.
 */
@Service
public class RelatorioConsumoProdutoService {

	private static final DateTimeFormatter FORMATO_MES = DateTimeFormatter.ofPattern("yyyy-MM");

	@Autowired
	private ItemComandaRepository itemComandaRepository;

	public RelatorioConsumoProdutoResponse gerarConsumoProdutos(Boolean apenasPessoasCadastradas, Integer idPessoa,
			String nomeTemporario, LocalDate dataAberturaInicio, LocalDate dataAberturaFim, Integer idProduto,
			Integer idCategoriaProduto, String status, boolean agruparPorMes, boolean agruparPorPessoa,
			boolean agruparPorStatus) throws BusinessRuleException {

		StatusComanda statusFiltro = converterStatusFiltro(status);

		// Chave -> linha agregada; LinkedHashMap só para manter uma ordem estável
		// antes do sort final (a ordem de inserção não importa para o resultado).
		Map<String, RelatorioConsumoProdutoLinha> agregados = new LinkedHashMap<>();

		for (ItemComanda item : itemComandaRepository.findAll()) {
			Comanda comanda = item.getComanda();

			if (comanda.getStatus() == StatusComanda.CANCELADA) {
				continue;
			}
			if ((statusFiltro != null) && (comanda.getStatus() != statusFiltro)) {
				continue;
			}

			boolean temPessoa = comanda.getPessoa() != null;
			if ((apenasPessoasCadastradas != null) && (apenasPessoasCadastradas.booleanValue() != temPessoa)) {
				continue;
			}
			if ((idPessoa != null) && (!temPessoa || (comanda.getPessoa().getId() != idPessoa))) {
				continue;
			}
			if ((nomeTemporario != null) && !nomeTemporario.isBlank()) {
				if (temPessoa || (comanda.getNomeTemporario() == null)
						|| !comanda.getNomeTemporario().toLowerCase().contains(nomeTemporario.trim().toLowerCase())) {
					continue;
				}
			}

			LocalDate dataAbertura = comanda.getDataAbertura().toLocalDate();
			if ((dataAberturaInicio != null) && dataAbertura.isBefore(dataAberturaInicio)) {
				continue;
			}
			if ((dataAberturaFim != null) && dataAbertura.isAfter(dataAberturaFim)) {
				continue;
			}

			Produto produto = item.getProduto();
			if ((idProduto != null) && (produto.getId() != idProduto)) {
				continue;
			}
			if ((idCategoriaProduto != null)
					&& ((produto.getCategoria() == null) || (produto.getCategoria().getId() != idCategoriaProduto))) {
				continue;
			}

			String mes = agruparPorMes ? dataAbertura.format(FORMATO_MES) : null;

			String pessoaLabel = null;
			String chavePessoa = "";
			if (agruparPorPessoa) {
				if (temPessoa) {
					pessoaLabel = comanda.getPessoa().getNome();
					chavePessoa = "P" + comanda.getPessoa().getId();
				} else {
					pessoaLabel = (comanda.getNomeTemporario() != null) ? comanda.getNomeTemporario() : "Visitante sem nome";
					chavePessoa = "V" + pessoaLabel.toLowerCase();
				}
			}

			String statusLinha = agruparPorStatus ? comanda.getStatus().name() : null;

			String chave = mes + "|" + chavePessoa + "|" + produto.getId() + "|" + statusLinha;

			RelatorioConsumoProdutoLinha linha = agregados.get(chave);
			if (linha == null) {
				linha = new RelatorioConsumoProdutoLinha();
				linha.setMes(mes);
				linha.setPessoa(pessoaLabel);
				linha.setIdProduto(produto.getId());
				linha.setProduto(produto.getDescricao());
				linha.setCategoriaProduto((produto.getCategoria() != null) ? produto.getCategoria().getDescricao() : null);
				linha.setStatus(statusLinha);
				linha.setQuantidadeTotal(0);
				linha.setValorTotal(BigDecimal.ZERO);
				agregados.put(chave, linha);
			}
			linha.setQuantidadeTotal(linha.getQuantidadeTotal() + item.getQuantidade());
			linha.setValorTotal(linha.getValorTotal().add(item.getSubtotal()));
		}

		List<RelatorioConsumoProdutoLinha> linhas = new ArrayList<>(agregados.values());
		// Prioridade de ordenação: mês > pessoa > produto > status.
		linhas.sort(Comparator
				.comparing((RelatorioConsumoProdutoLinha l) -> l.getMes() == null ? "" : l.getMes())
				.thenComparing(l -> l.getPessoa() == null ? "" : l.getPessoa(), String.CASE_INSENSITIVE_ORDER)
				.thenComparing(RelatorioConsumoProdutoLinha::getProduto, String.CASE_INSENSITIVE_ORDER)
				.thenComparing(l -> l.getStatus() == null ? "" : l.getStatus()));

		RelatorioConsumoProdutoResponse response = new RelatorioConsumoProdutoResponse();
		response.setLinhas(linhas);

		int quantidadeTotalGeral = 0;
		BigDecimal valorTotalGeral = BigDecimal.ZERO;
		for (RelatorioConsumoProdutoLinha linha : linhas) {
			quantidadeTotalGeral += linha.getQuantidadeTotal();
			valorTotalGeral = valorTotalGeral.add(linha.getValorTotal());
		}
		response.setQuantidadeTotalGeral(quantidadeTotalGeral);
		response.setValorTotalGeral(valorTotalGeral);

		return response;
	}

	private StatusComanda converterStatusFiltro(String status) throws BusinessRuleException {
		if ((status == null) || status.isBlank()) {
			return null;
		}
		StatusComanda statusFiltro;
		try {
			statusFiltro = StatusComanda.valueOf(status.trim().toUpperCase());
		} catch (IllegalArgumentException ex) {
			throw new BusinessRuleException("Status \"" + status + "\" inválido. Use ABERTA ou FECHADA.");
		}
		if (statusFiltro == StatusComanda.CANCELADA) {
			throw new BusinessRuleException("Comandas canceladas nunca são incluídas neste relatório.");
		}
		return statusFiltro;
	}

}
