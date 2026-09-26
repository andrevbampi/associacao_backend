package com.projeto.associacao.dto.estoque;

import java.time.LocalDateTime;

import com.projeto.associacao.dto.produto.ProdutoResponse;
import com.projeto.associacao.dto.usuario.UsuarioResponse;
import com.projeto.associacao.model.OrigemMovimentoEstoque;
import com.projeto.associacao.model.TipoMovimentoEstoque;

public class MovimentoEstoqueResponse {

	private int id;
	private ProdutoResponse produto;
	private TipoMovimentoEstoque tipo;
	private int quantidade;
	private int estoqueAnterior;
	private int estoquePosterior;
	private LocalDateTime dataHora;
	private UsuarioResponse usuario;
	private String observacao;
	private OrigemMovimentoEstoque origem;
	private Integer idOrigem;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public ProdutoResponse getProduto() {
		return produto;
	}
	public void setProduto(ProdutoResponse produto) {
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
	public UsuarioResponse getUsuario() {
		return usuario;
	}
	public void setUsuario(UsuarioResponse usuario) {
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

}
