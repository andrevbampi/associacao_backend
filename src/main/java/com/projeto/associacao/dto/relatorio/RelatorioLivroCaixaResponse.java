package com.projeto.associacao.dto.relatorio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class RelatorioLivroCaixaResponse {

	private List<RelatorioLivroCaixaLinha> linhas;
	private BigDecimal totalEntradasGeral;
	private BigDecimal totalSaidasGeral;
	private BigDecimal saldoGeral;
	private LocalDate dataAcumuladoAte;
	private RelatorioLivroCaixaAcumulado acumuladoGeral;
	private List<RelatorioLivroCaixaAcumulado> acumuladosPorCaixa;

	public LocalDate getDataAcumuladoAte() {
		return dataAcumuladoAte;
	}
	public void setDataAcumuladoAte(LocalDate dataAcumuladoAte) {
		this.dataAcumuladoAte = dataAcumuladoAte;
	}
	public RelatorioLivroCaixaAcumulado getAcumuladoGeral() {
		return acumuladoGeral;
	}
	public void setAcumuladoGeral(RelatorioLivroCaixaAcumulado acumuladoGeral) {
		this.acumuladoGeral = acumuladoGeral;
	}
	public List<RelatorioLivroCaixaAcumulado> getAcumuladosPorCaixa() {
		return acumuladosPorCaixa;
	}
	public void setAcumuladosPorCaixa(List<RelatorioLivroCaixaAcumulado> acumuladosPorCaixa) {
		this.acumuladosPorCaixa = acumuladosPorCaixa;
	}

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
