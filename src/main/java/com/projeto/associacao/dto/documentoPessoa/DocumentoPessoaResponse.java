package com.projeto.associacao.dto.documentoPessoa;

import java.time.LocalDateTime;

import com.projeto.associacao.dto.usuario.UsuarioResponse;

public class DocumentoPessoaResponse {

	private int id;
	private String nomeOriginal;
	private String contentType;
	private long tamanho;
	private LocalDateTime dataUpload;
	private UsuarioResponse usuarioUpload;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNomeOriginal() {
		return nomeOriginal;
	}
	public void setNomeOriginal(String nomeOriginal) {
		this.nomeOriginal = nomeOriginal;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public long getTamanho() {
		return tamanho;
	}
	public void setTamanho(long tamanho) {
		this.tamanho = tamanho;
	}
	public LocalDateTime getDataUpload() {
		return dataUpload;
	}
	public void setDataUpload(LocalDateTime dataUpload) {
		this.dataUpload = dataUpload;
	}
	public UsuarioResponse getUsuarioUpload() {
		return usuarioUpload;
	}
	public void setUsuarioUpload(UsuarioResponse usuarioUpload) {
		this.usuarioUpload = usuarioUpload;
	}

}
