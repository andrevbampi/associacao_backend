package com.projeto.associacao.dto.financeiro;

import java.math.BigDecimal;

public class ResumoFinanceiroResponse {

	private BigDecimal saldoAtual;
	private BigDecimal totalEntradasPeriodo;
	private BigDecimal totalSaidasPeriodo;

	public BigDecimal getSaldoAtual() {
		return saldoAtual;
	}
	public void setSaldoAtual(BigDecimal saldoAtual) {
		this.saldoAtual = saldoAtual;
	}
	public BigDecimal getTotalEntradasPeriodo() {
		return totalEntradasPeriodo;
	}
	public void setTotalEntradasPeriodo(BigDecimal totalEntradasPeriodo) {
		this.totalEntradasPeriodo = totalEntradasPeriodo;
	}
	public BigDecimal getTotalSaidasPeriodo() {
		return totalSaidasPeriodo;
	}
	public void setTotalSaidasPeriodo(BigDecimal totalSaidasPeriodo) {
		this.totalSaidasPeriodo = totalSaidasPeriodo;
	}

}
