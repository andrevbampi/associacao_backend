package com.projeto.associacao.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

/**
 * Linha única (id fixo = 1) com a logo da associação. Não usa auto-increment
 * porque só existe uma logo por instalação — cadastrar/trocar é um upsert
 * dessa linha, não a criação de um novo registro.
 */
@Entity
@Table(name = "logo_associacao")
public class LogoAssociacao {

	public static final int ID_FIXO = 1;

	@Id
	private int id = ID_FIXO;

	@Lob
	@Column(name = "arquivo")
	private byte[] arquivo;

	@Column(name = "contenttype")
	private String contentType;

	@Column(name = "nomeoriginal")
	private String nomeOriginal;

	@Column(name = "dataupload")
	private LocalDateTime dataUpload;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public byte[] getArquivo() {
		return arquivo;
	}
	public void setArquivo(byte[] arquivo) {
		this.arquivo = arquivo;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getNomeOriginal() {
		return nomeOriginal;
	}
	public void setNomeOriginal(String nomeOriginal) {
		this.nomeOriginal = nomeOriginal;
	}
	public LocalDateTime getDataUpload() {
		return dataUpload;
	}
	public void setDataUpload(LocalDateTime dataUpload) {
		this.dataUpload = dataUpload;
	}

}
