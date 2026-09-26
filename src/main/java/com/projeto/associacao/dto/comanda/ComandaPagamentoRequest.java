package com.projeto.associacao.dto.comanda;

public class ComandaPagamentoRequest {

	private String formaPagamento;
	private Integer idCaixa;

	public String getFormaPagamento() {
		return formaPagamento;
	}
	public void setFormaPagamento(String formaPagamento) {
		this.formaPagamento = formaPagamento;
	}
	public Integer getIdCaixa() {
		return idCaixa;
	}
	public void setIdCaixa(Integer idCaixa) {
		this.idCaixa = idCaixa;
	}

}
