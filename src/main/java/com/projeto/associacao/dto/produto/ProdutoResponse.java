package com.projeto.associacao.dto.produto;

import java.math.BigDecimal;

import com.projeto.associacao.model.CategoriaProduto;

public class ProdutoResponse {

	private int id;
	private String descricao;
	private BigDecimal preco;
	private BigDecimal precoMembro;
	private CategoriaProduto categoria;
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
	public CategoriaProduto getCategoria() {
		return categoria;
	}
	public void setCategoria(CategoriaProduto categoria) {
		this.categoria = categoria;
	}
	public boolean isAtivo() {
		return ativo;
	}
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

}
