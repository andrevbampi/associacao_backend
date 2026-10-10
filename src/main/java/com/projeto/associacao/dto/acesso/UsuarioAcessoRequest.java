package com.projeto.associacao.dto.acesso;

import java.util.List;

public class UsuarioAcessoRequest {

	private List<Integer> idsGrupos;
	private List<ExcecaoPermissaoDto> excecoes;

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

}
