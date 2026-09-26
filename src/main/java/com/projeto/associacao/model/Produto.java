package com.projeto.associacao.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
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

	@Lob
	@Column(name = "foto")
	private byte[] foto;

	@Column(name = "fotocontenttype")
	private String fotoContentType;

	@Column(name = "fotonomeoriginal")
	private String fotoNomeOriginal;

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
	public byte[] getFoto() {
		return foto;
	}
	public void setFoto(byte[] foto) {
		this.foto = foto;
	}
	public String getFotoContentType() {
		return fotoContentType;
	}
	public void setFotoContentType(String fotoContentType) {
		this.fotoContentType = fotoContentType;
	}
	public String getFotoNomeOriginal() {
		return fotoNomeOriginal;
	}
	public void setFotoNomeOriginal(String fotoNomeOriginal) {
		this.fotoNomeOriginal = fotoNomeOriginal;
	}
	public boolean isTemFoto() {
		return (foto != null) && (foto.length > 0);
	}

}
