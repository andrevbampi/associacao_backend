package com.projeto.associacao.dto.financeiro;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.projeto.associacao.dto.usuario.UsuarioResponse;
import com.projeto.associacao.model.CategoriaFinanceira;
import com.projeto.associacao.model.FormaPagamento;
import com.projeto.associacao.model.Membro;
import com.projeto.associacao.model.Pessoa;
import com.projeto.associacao.model.TipoLancamento;

public class LancamentoFinanceiroResponse {

	private int id;
	private CategoriaFinanceira categoriaFinanceira;
	private TipoLancamento tipo;
	private BigDecimal valor;
	private LocalDate data;
	private String descricao;
	private Pessoa pessoa;
	private Membro membro;
	private Integer idComanda;
	private UsuarioResponse usuario;
	private String observacao;
	private boolean pago;
	private LocalDateTime dataPagamento;
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
	public Integer getIdComanda() {
		return idComanda;
	}
	public void setIdComanda(Integer idComanda) {
		this.idComanda = idComanda;
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
