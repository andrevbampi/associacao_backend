package com.projeto.associacao.dto.relatorio;

import java.math.BigDecimal;
import java.util.List;

public class RelatorioConsumoProdutoResponse {

	private List<RelatorioConsumoProdutoLinha> linhas;
	private int quantidadeTotalGeral;
	private BigDecimal valorTotalGeral;

	public List<RelatorioConsumoProdutoLinha> getLinhas() {
		return linhas;
	}
	public void setLinhas(List<RelatorioConsumoProdutoLinha> linhas) {
		this.linhas = linhas;
	}
	public int getQuantidadeTotalGeral() {
		return quantidadeTotalGeral;
	}
	public void setQuantidadeTotalGeral(int quantidadeTotalGeral) {
		this.quantidadeTotalGeral = quantidadeTotalGeral;
	}
	public BigDecimal getValorTotalGeral() {
		return valorTotalGeral;
	}
	public void setValorTotalGeral(BigDecimal valorTotalGeral) {
		this.valorTotalGeral = valorTotalGeral;
	}

}
