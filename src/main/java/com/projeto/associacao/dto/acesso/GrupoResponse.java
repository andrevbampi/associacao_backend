package com.projeto.associacao.dto.acesso;

import java.util.List;

public class GrupoResponse {

	private int id;
	private String nome;
	private String descricao;
	private boolean ativo;
	private boolean administrador;
	private List<String> permissoes;
	private int totalUsuarios;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public boolean isAtivo() {
		return ativo;
	}
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public boolean isAdministrador() {
		return administrador;
	}
	public void setAdministrador(boolean administrador) {
		this.administrador = administrador;
	}

	public List<String> getPermissoes() {
		return permissoes;
	}
	public void setPermissoes(List<String> permissoes) {
		this.permissoes = permissoes;
	}

	public int getTotalUsuarios() {
		return totalUsuarios;
	}
	public void setTotalUsuarios(int totalUsuarios) {
		this.totalUsuarios = totalUsuarios;
	}

}
