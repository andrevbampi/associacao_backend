package com.projeto.associacao.dto.comanda;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.projeto.associacao.model.FormaPagamento;
import com.projeto.associacao.model.Pessoa;
import com.projeto.associacao.model.StatusComanda;

public class ComandaResponse {

	private int id;
	private Pessoa pessoa;
	private String nomeTemporario;
	private LocalDateTime dataAbertura;
	private LocalDateTime dataFechamento;
	private StatusComanda status;
	private BigDecimal valorTotal;
	private boolean pago;
	private LocalDateTime dataPagamento;
	private FormaPagamento formaPagamento;
	private String observacao;

	// Só é preenchida ao buscar uma comanda específica (GET /api/comanda/{id});
	// na listagem (GET /api/comanda/) fica null, para não buscar os itens de
	// todas as comandas de uma vez.
	private List<ItemComandaResponse> itens;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Pessoa getPessoa() {
		return pessoa;
	}
	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}
	public String getNomeTemporario() {
		return nomeTemporario;
	}
	public void setNomeTemporario(String nomeTemporario) {
		this.nomeTemporario = nomeTemporario;
	}
	public LocalDateTime getDataAbertura() {
		return dataAbertura;
	}
	public void setDataAbertura(LocalDateTime dataAbertura) {
		this.dataAbertura = dataAbertura;
	}
	public LocalDateTime getDataFechamento() {
		return dataFechamento;
	}
	public void setDataFechamento(LocalDateTime dataFechamento) {
		this.dataFechamento = dataFechamento;
	}
	public StatusComanda getStatus() {
		return status;
	}
	public void setStatus(StatusComanda status) {
		this.status = status;
	}
	public BigDecimal getValorTotal() {
		return valorTotal;
	}
	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}
	public boolean isPago() {
		return pago;
	}
	public void setPago(boolean pago) {
		this.pago = pago;
	}
	public LocalDateTime getDataPagamento() {
		return dataPagamento;
	}
	public void setDataPagamento(LocalDateTime dataPagamento) {
		this.dataPagamento = dataPagamento;
	}
	public FormaPagamento getFormaPagamento() {
		return formaPagamento;
	}
	public void setFormaPagamento(FormaPagamento formaPagamento) {
		this.formaPagamento = formaPagamento;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	public List<ItemComandaResponse> getItens() {
		return itens;
	}
	public void setItens(List<ItemComandaResponse> itens) {
		this.itens = itens;
	}

}
