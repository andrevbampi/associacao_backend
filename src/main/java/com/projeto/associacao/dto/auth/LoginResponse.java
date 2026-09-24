package com.projeto.associacao.dto.auth;

import com.projeto.associacao.dto.usuario.UsuarioResponse;

public class LoginResponse {

	private String token;
	private String tipoToken = "Bearer";
	private UsuarioResponse usuario;

	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getTipoToken() {
		return tipoToken;
	}
	public void setTipoToken(String tipoToken) {
		this.tipoToken = tipoToken;
	}
	public UsuarioResponse getUsuario() {
		return usuario;
	}
	public void setUsuario(UsuarioResponse usuario) {
		this.usuario = usuario;
	}

}
