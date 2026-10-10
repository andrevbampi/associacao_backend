package com.projeto.associacao.dto.acesso;

import java.time.LocalDateTime;

public class AuditoriaAcessoResponse {

	private int id;
	private LocalDateTime dataHora;
	private String login;
	private String acao;
	private String entidade;
	private Integer idEntidade;
	private String descricao;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public LocalDateTime getDataHora() {
		return dataHora;
	}
	public void setDataHora(LocalDateTime dataHora) {
		this.dataHora = dataHora;
	}

	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}

	public String getAcao() {
		return acao;
	}
	public void setAcao(String acao) {
		this.acao = acao;
	}

	public String getEntidade() {
		return entidade;
	}
	public void setEntidade(String entidade) {
		this.entidade = entidade;
	}

	public Integer getIdEntidade() {
		return idEntidade;
	}
	public void setIdEntidade(Integer idEntidade) {
		this.idEntidade = idEntidade;
	}

	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
