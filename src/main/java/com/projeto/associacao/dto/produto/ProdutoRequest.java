package com.projeto.associacao.dto.produto;

import java.math.BigDecimal;

public class ProdutoRequest {

	private int id;
	private String descricao;
	private BigDecimal preco;
	private BigDecimal precoMembro;
	private int idCategoria;
	private boolean ativo;

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
	public int getIdCategoria() {
		return idCategoria;
	}
	public void setIdCategoria(int idCategoria) {
		this.idCategoria = idCategoria;
	}
	public boolean isAtivo() {
		return ativo;
	}
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

}
