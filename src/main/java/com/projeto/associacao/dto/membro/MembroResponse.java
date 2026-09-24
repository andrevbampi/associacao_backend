package com.projeto.associacao.dto.membro;

import java.time.LocalDate;

import com.projeto.associacao.model.Pessoa;
import com.projeto.associacao.model.StatusMembro;

public class MembroResponse {

	private int id;
	private Pessoa pessoa;
	private StatusMembro status;
	private boolean ativo;
	private LocalDate dataInclusao;
	private LocalDate dataSaida;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Pessoa getPessoa() {
		return pessoa;
	}
	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}
	public StatusMembro getStatus() {
		return status;
	}
	public void setStatus(StatusMembro status) {
		this.status = status;
	}
	public boolean isAtivo() {
		return ativo;
	}
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	public LocalDate getDataInclusao() {
		return dataInclusao;
	}
	public void setDataInclusao(LocalDate dataInclusao) {
		this.dataInclusao = dataInclusao;
	}
	public LocalDate getDataSaida() {
		return dataSaida;
	}
	public void setDataSaida(LocalDate dataSaida) {
		this.dataSaida = dataSaida;
	}

}
