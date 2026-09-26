package com.projeto.associacao.dto.estoque;

import java.math.BigDecimal;

public class MovimentoEstoqueRequest {

	private int idProduto;
	private String tipo;
	private int quantidade;
	private String observacao;
	private String origem;

	// Se true (só faz sentido para ENTRADA de origem COMPRA), gera também um
	// lançamento financeiro de despesa vinculado a esta movimentação.
	private boolean gerarLancamentoFinanceiro;
	private int idCategoriaFinanceira;
	private BigDecimal valorLancamento;

	public int getIdProduto() {
		return idProduto;
	}
	public void setIdProduto(int idProduto) {
		this.idProduto = idProduto;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public int getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	public String getOrigem() {
		return origem;
	}
	public void setOrigem(String origem) {
		this.origem = origem;
	}
	public boolean isGerarLancamentoFinanceiro() {
		return gerarLancamentoFinanceiro;
	}
	public void setGerarLancamentoFinanceiro(boolean gerarLancamentoFinanceiro) {
		this.gerarLancamentoFinanceiro = gerarLancamentoFinanceiro;
	}
	public int getIdCategoriaFinanceira() {
		return idCategoriaFinanceira;
	}
	public void setIdCategoriaFinanceira(int idCategoriaFinanceira) {
		this.idCategoriaFinanceira = idCategoriaFinanceira;
	}
	public BigDecimal getValorLancamento() {
		return valorLancamento;
	}
	public void setValorLancamento(BigDecimal valorLancamento) {
		this.valorLancamento = valorLancamento;
	}

}
