package com.projeto.associacao.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "comanda")
public class Comanda {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "idpessoa")
	private Pessoa pessoa;

	@Column(name = "nometemporario")
	private String nomeTemporario;

	@Column(name = "dataabertura")
	private LocalDateTime dataAbertura;

	@Column(name = "datafechamento")
	private LocalDateTime dataFechamento;

	@Enumerated(EnumType.STRING)
	private StatusComanda status;

	@Column(name = "valortotal")
	private BigDecimal valorTotal;

	private boolean pago;

	@Column(name = "datapagamento")
	private LocalDateTime dataPagamento;

	@Column(name = "formapagamento")
	@Enumerated(EnumType.STRING)
	private FormaPagamento formaPagamento;

	private String observacao;

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

}
