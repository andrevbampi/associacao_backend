package com.projeto.associacao.dto.relatorio;

import java.math.BigDecimal;
import java.util.List;

public class RelatorioLivroCaixaResponse {

	private List<RelatorioLivroCaixaLinha> linhas;
	private BigDecimal totalEntradasGeral;
	private BigDecimal totalSaidasGeral;
	private BigDecimal saldoGeral;

	public List<RelatorioLivroCaixaLinha> getLinhas() {
		return linhas;
	}
	public void setLinhas(List<RelatorioLivroCaixaLinha> linhas) {
		this.linhas = linhas;
	}
	public BigDecimal getTotalEntradasGeral() {
		return totalEntradasGeral;
	}
	public void setTotalEntradasGeral(BigDecimal totalEntradasGeral) {
		this.totalEntradasGeral = totalEntradasGeral;
	}
	public BigDecimal getTotalSaidasGeral() {
		return totalSaidasGeral;
	}
	public void setTotalSaidasGeral(BigDecimal totalSaidasGeral) {
		this.totalSaidasGeral = totalSaidasGeral;
	}
	public BigDecimal getSaldoGeral() {
		return saldoGeral;
	}
	public void setSaldoGeral(BigDecimal saldoGeral) {
		this.saldoGeral = saldoGeral;
	}

}
