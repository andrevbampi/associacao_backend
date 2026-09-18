package com.projeto.associacao.dto.usuario;

import com.projeto.associacao.model.Pessoa;

public class UsuarioResponse {

	private int id;
	private String login;
	private Pessoa pessoa;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public Pessoa getPessoa() {
		return pessoa;
	}
	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

}
