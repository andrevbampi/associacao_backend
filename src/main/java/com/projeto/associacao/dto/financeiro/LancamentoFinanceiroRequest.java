package com.projeto.associacao.dto.financeiro;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LancamentoFinanceiroRequest {

	private int id;
	private int idCategoriaFinanceira;
	private int idCaixa;
	private String tipo;
	private BigDecimal valor;
	private LocalDate data;
	private String descricao;
	private Integer idPessoa;
	private Integer idMembro;
	private Integer idComanda;
	private String observacao;
	private boolean pago;
	private String formaPagamento;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIdCategoriaFinanceira() {
		return idCategoriaFinanceira;
	}
	public void setIdCategoriaFinanceira(int idCategoriaFinanceira) {
		this.idCategoriaFinanceira = idCategoriaFinanceira;
	}
	public int getIdCaixa() {
		return idCaixa;
	}
	public void setIdCaixa(int idCaixa) {
		this.idCaixa = idCaixa;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	public LocalDate getData() {
		return data;
	}
	public void setData(LocalDate data) {
		this.data = data;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Integer getIdPessoa() {
		return idPessoa;
	}
	public void setIdPessoa(Integer idPessoa) {
		this.idPessoa = idPessoa;
	}
	public Integer getIdMembro() {
		return idMembro;
	}
	public void setIdMembro(Integer idMembro) {
		this.idMembro = idMembro;
	}
	public Integer getIdComanda() {
		return idComanda;
	}
	public void setIdComanda(Integer idComanda) {
		this.idComanda = idComanda;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
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

}
