package com.projeto.associacao.dto.historicoMembro;

import java.time.LocalDateTime;

import com.projeto.associacao.dto.membro.MembroResponse;
import com.projeto.associacao.dto.usuario.UsuarioResponse;
import com.projeto.associacao.model.TipoEvento;

public class HistoricoMembroResponse {

	private int id;
	private MembroResponse membro;
	private TipoEvento tipoEvento;
	private String descricao;
	private LocalDateTime data;
	private UsuarioResponse usuarioRegistro;
	private String observacao;
	private boolean ativo;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public MembroResponse getMembro() {
		return membro;
	}
	public void setMembro(MembroResponse membro) {
		this.membro = membro;
	}
	public TipoEvento getTipoEvento() {
		return tipoEvento;
	}
	public void setTipoEvento(TipoEvento tipoEvento) {
		this.tipoEvento = tipoEvento;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public LocalDateTime getData() {
		return data;
	}
	public void setData(LocalDateTime data) {
		this.data = data;
	}
	public UsuarioResponse getUsuarioRegistro() {
		return usuarioRegistro;
	}
	public void setUsuarioRegistro(UsuarioResponse usuarioRegistro) {
		this.usuarioRegistro = usuarioRegistro;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	public boolean isAtivo() {
		return ativo;
	}
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

}
