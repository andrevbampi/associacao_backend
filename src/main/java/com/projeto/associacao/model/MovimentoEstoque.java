package com.projeto.associacao.model;

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
@Table(name = "movimento_estoque")
public class MovimentoEstoque {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "idproduto", nullable = false)
	private Produto produto;

	@Enumerated(EnumType.STRING)
	private TipoMovimentoEstoque tipo;

	private int quantidade;

	@Column(name = "estoqueanterior")
	private int estoqueAnterior;

	@Column(name = "estoqueposterior")
	private int estoquePosterior;

	@Column(name = "datahora")
	private LocalDateTime dataHora;

	@ManyToOne
	@JoinColumn(name = "idusuario", nullable = false)
	private Usuario usuario;

	private String observacao;

	@Enumerated(EnumType.STRING)
	private OrigemMovimentoEstoque origem;

	@Column(name = "idorigem")
	private Integer idOrigem;

	// Marca um movimento gerado automaticamente (origem VENDA) como já
	// estornado, para que um novo estorno na mesma comanda não o reverta de novo.
	private boolean estornado;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Produto getProduto() {
		return produto;
	}
	public void setProduto(Produto produto) {
		this.produto = produto;
	}
	public TipoMovimentoEstoque getTipo() {
		return tipo;
	}
	public void setTipo(TipoMovimentoEstoque tipo) {
		this.tipo = tipo;
	}
	public int getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}
	public int getEstoqueAnterior() {
		return estoqueAnterior;
	}
	public void setEstoqueAnterior(int estoqueAnterior) {
		this.estoqueAnterior = estoqueAnterior;
	}
	public int getEstoquePosterior() {
		return estoquePosterior;
	}
	public void setEstoquePosterior(int estoquePosterior) {
		this.estoquePosterior = estoquePosterior;
	}
	public LocalDateTime getDataHora() {
		return dataHora;
	}
	public void setDataHora(LocalDateTime dataHora) {
		this.dataHora = dataHora;
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
	public OrigemMovimentoEstoque getOrigem() {
		return origem;
	}
	public void setOrigem(OrigemMovimentoEstoque origem) {
		this.origem = origem;
	}
	public Integer getIdOrigem() {
		return idOrigem;
	}
	public void setIdOrigem(Integer idOrigem) {
		this.idOrigem = idOrigem;
	}
	public boolean isEstornado() {
		return estornado;
	}
	public void setEstornado(boolean estornado) {
		this.estornado = estornado;
	}

}
