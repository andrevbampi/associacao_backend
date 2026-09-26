package com.projeto.associacao.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "produto")
public class Produto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String descricao;
	private BigDecimal preco;

	@Column(name = "precomembro")
	private BigDecimal precoMembro;

	private boolean ativo;

	@ManyToOne
	@JoinColumn(name = "idcategoria", nullable = false)
	private CategoriaProduto categoria;

	@Column(name = "estoqueatual")
	private int estoqueAtual;

	@Column(name = "estoqueminimo")
	private Integer estoqueMinimo;

	@Column(name = "controlaestoque")
	private boolean controlaEstoque = true;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public BigDecimal getPreco() {
		return preco;
	}
	public void setPreco(BigDecimal preco) {
		this.preco = preco;
	}
	public BigDecimal getPrecoMembro() {
		return precoMembro;
	}
	public void setPrecoMembro(BigDecimal precoMembro) {
		this.precoMembro = precoMembro;
	}
	public boolean isAtivo() {
		return ativo;
	}
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	public CategoriaProduto getCategoria() {
		return categoria;
	}
	public void setCategoria(CategoriaProduto categoria) {
		this.categoria = categoria;
	}
	public int getEstoqueAtual() {
		return estoqueAtual;
	}
	public void setEstoqueAtual(int estoqueAtual) {
		this.estoqueAtual = estoqueAtual;
	}
	public Integer getEstoqueMinimo() {
		return estoqueMinimo;
	}
	public void setEstoqueMinimo(Integer estoqueMinimo) {
		this.estoqueMinimo = estoqueMinimo;
	}
	public boolean isControlaEstoque() {
		return controlaEstoque;
	}
	public void setControlaEstoque(boolean controlaEstoque) {
		this.controlaEstoque = controlaEstoque;
	}

}
