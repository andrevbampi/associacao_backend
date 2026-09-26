package com.projeto.associacao.model;

import java.math.BigDecimal;
import java.time.LocalDate;
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
@Table(name = "lancamento_financeiro")
public class LancamentoFinanceiro {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "idcategoriafinanceira", nullable = false)
	private CategoriaFinanceira categoriaFinanceira;

	@Enumerated(EnumType.STRING)
	private TipoLancamento tipo;

	private BigDecimal valor;

	private LocalDate data;

	private String descricao;

	@ManyToOne
	@JoinColumn(name = "idpessoa")
	private Pessoa pessoa;

	@ManyToOne
	@JoinColumn(name = "idmembro")
	private Membro membro;

	@ManyToOne
	@JoinColumn(name = "idcomanda")
	private Comanda comanda;

	@ManyToOne
	@JoinColumn(name = "idusuario", nullable = false)
	private Usuario usuario;

	private String observacao;

	private boolean pago;

	@Column(name = "datapagamento")
	private LocalDateTime dataPagamento;

	@Column(name = "formapagamento")
	@Enumerated(EnumType.STRING)
	private FormaPagamento formaPagamento;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public CategoriaFinanceira getCategoriaFinanceira() {
		return categoriaFinanceira;
	}
	public void setCategoriaFinanceira(CategoriaFinanceira categoriaFinanceira) {
		this.categoriaFinanceira = categoriaFinanceira;
	}
	public TipoLancamento getTipo() {
		return tipo;
	}
	public void setTipo(TipoLancamento tipo) {
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
	public Pessoa getPessoa() {
		return pessoa;
	}
	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}
	public Membro getMembro() {
		return membro;
	}
	public void setMembro(Membro membro) {
		this.membro = membro;
	}
	public Comanda getComanda() {
		return comanda;
	}
	public void setComanda(Comanda comanda) {
		this.comanda = comanda;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
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

}
