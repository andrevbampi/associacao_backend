package com.projeto.associacao.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "documento_ata")
public class DocumentoAta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "idata", nullable = false)
	private Ata ata;

	@Column(name = "nomeoriginal")
	private String nomeOriginal;

	@Column(name = "contenttype")
	private String contentType;

	private long tamanho;

	@Lob
	@Column(name = "arquivo")
	private byte[] arquivo;

	@Column(name = "dataupload")
	private LocalDateTime dataUpload;

	@ManyToOne
	@JoinColumn(name = "idusuarioupload", nullable = false)
	private Usuario usuarioUpload;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Ata getAta() {
		return ata;
	}
	public void setAta(Ata ata) {
		this.ata = ata;
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
	public byte[] getArquivo() {
		return arquivo;
	}
	public void setArquivo(byte[] arquivo) {
		this.arquivo = arquivo;
	}
	public LocalDateTime getDataUpload() {
		return dataUpload;
	}
	public void setDataUpload(LocalDateTime dataUpload) {
		this.dataUpload = dataUpload;
	}
	public Usuario getUsuarioUpload() {
		return usuarioUpload;
	}
	public void setUsuarioUpload(Usuario usuarioUpload) {
		this.usuarioUpload = usuarioUpload;
	}

}
