package com.projeto.associacao.dto.comanda;

public class ComandaAberturaRequest {

	private Integer idPessoa;
	private String nomeTemporario;
	private String observacao;

	public Integer getIdPessoa() {
		return idPessoa;
	}
	public void setIdPessoa(Integer idPessoa) {
		this.idPessoa = idPessoa;
	}
	public String getNomeTemporario() {
		return nomeTemporario;
	}
	public void setNomeTemporario(String nomeTemporario) {
		this.nomeTemporario = nomeTemporario;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

}
