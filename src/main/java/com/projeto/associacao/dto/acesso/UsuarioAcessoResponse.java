package com.projeto.associacao.dto.acesso;

import java.util.List;

public class UsuarioAcessoResponse {

	private int idUsuario;
	private String login;
	private List<Integer> idsGrupos;
	private List<ExcecaoPermissaoDto> excecoes;
	private List<PermissaoEfetivaResponse> permissoesEfetivas;

	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}

	public List<Integer> getIdsGrupos() {
		return idsGrupos;
	}
	public void setIdsGrupos(List<Integer> idsGrupos) {
		this.idsGrupos = idsGrupos;
	}

	public List<ExcecaoPermissaoDto> getExcecoes() {
		return excecoes;
	}
	public void setExcecoes(List<ExcecaoPermissaoDto> excecoes) {
		this.excecoes = excecoes;
	}

	public List<PermissaoEfetivaResponse> getPermissoesEfetivas() {
		return permissoesEfetivas;
	}
	public void setPermissoesEfetivas(List<PermissaoEfetivaResponse> permissoesEfetivas) {
		this.permissoesEfetivas = permissoesEfetivas;
	}

}
