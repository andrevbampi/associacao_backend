package com.projeto.associacao.dto.comanda;

public class ComandaFechamentoRequest {

	private boolean pago;
	private String formaPagamento;
	private Integer idCaixa;

	public boolean isPago() {
		return pago;
	}
	public void setPago(boolean pago) {
		this.pago = pago;
	}
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
