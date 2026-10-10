package com.projeto.associacao.dto.usuario;

import java.util.List;

import com.projeto.associacao.model.Pessoa;

public class UsuarioResponse {

	private int id;
	private String login;
	private Pessoa pessoa;
	private boolean ativo;
	private List<String> permissoes;

	public List<String> getPermissoes() {
		return permissoes;
	}
	public void setPermissoes(List<String> permissoes) {
		this.permissoes = permissoes;
	}
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
	public boolean isAtivo() {
		return ativo;
	}
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

}
