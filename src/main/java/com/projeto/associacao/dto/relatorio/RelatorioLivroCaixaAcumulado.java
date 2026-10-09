package com.projeto.associacao.dto.relatorio;

import java.math.BigDecimal;

public class RelatorioLivroCaixaAcumulado {

	private String caixa;
	private BigDecimal totalEntradas;
	private BigDecimal totalSaidas;
	private BigDecimal saldo;

	public String getCaixa() {
		return caixa;
	}
	public void setCaixa(String caixa) {
		this.caixa = caixa;
	}
	public BigDecimal getTotalEntradas() {
		return totalEntradas;
	}
	public void setTotalEntradas(BigDecimal totalEntradas) {
		this.totalEntradas = totalEntradas;
	}
	public BigDecimal getTotalSaidas() {
		return totalSaidas;
	}
	public void setTotalSaidas(BigDecimal totalSaidas) {
		this.totalSaidas = totalSaidas;
	}
	public BigDecimal getSaldo() {
		return saldo;
	}
	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}

}
