package com.projeto.associacao.dto.relatorio;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RelatorioLivroCaixaLinha {

	// "DETALHE" (um lançamento normal), "COMANDAS" (soma de vendas de comandas
	// pagas) ou "MENSALIDADES" (soma de lançamentos da categoria "Mensalidade de
	// membro"). Ajuda o front a estilizar as linhas agregadas.
	private String tipoLinha;

	// caixa/mes nulos quando o agrupamento correspondente não foi selecionado.
	private String caixa;
	private String mes;

	// Só preenchida em linhas de detalhe: linhas agregadas somam várias datas.
	private LocalDate data;

	private String descricao;
	private String categoria;
	private BigDecimal valorEntrada;
	private BigDecimal valorSaida;
	private String observacao;

	public String getTipoLinha() {
		return tipoLinha;
	}
	public void setTipoLinha(String tipoLinha) {
		this.tipoLinha = tipoLinha;
	}
	public String getCaixa() {
		return caixa;
	}
	public void setCaixa(String caixa) {
		this.caixa = caixa;
	}
	public String getMes() {
		return mes;
	}
	public void setMes(String mes) {
		this.mes = mes;
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
	public String getCategoria() {
		return categoria;
	}
	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}
	public BigDecimal getValorEntrada() {
		return valorEntrada;
	}
	public void setValorEntrada(BigDecimal valorEntrada) {
		this.valorEntrada = valorEntrada;
	}
	public BigDecimal getValorSaida() {
		return valorSaida;
	}
	public void setValorSaida(BigDecimal valorSaida) {
		this.valorSaida = valorSaida;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

}
