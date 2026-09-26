package com.projeto.associacao.dto.financeiro;

import java.math.BigDecimal;

import com.projeto.associacao.model.Caixa;

public class ResumoCaixaResponse {

	private Caixa caixa;
	private BigDecimal saldoAtual;

	public Caixa getCaixa() {
		return caixa;
	}
	public void setCaixa(Caixa caixa) {
		this.caixa = caixa;
	}
	public BigDecimal getSaldoAtual() {
		return saldoAtual;
	}
	public void setSaldoAtual(BigDecimal saldoAtual) {
		this.saldoAtual = saldoAtual;
	}

}
