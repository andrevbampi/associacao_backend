package com.projeto.associacao.dto.ata;

import java.time.LocalDate;

public class AtaRequest {

	private int id;
	private LocalDate dataAta;
	private int idPessoaRedator;
	private String nomeRedator;
	private String titulo;
	private String conteudo;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public LocalDate getDataAta() {
		return dataAta;
	}
	public void setDataAta(LocalDate dataAta) {
		this.dataAta = dataAta;
	}
	public int getIdPessoaRedator() {
		return idPessoaRedator;
	}
	public void setIdPessoaRedator(int idPessoaRedator) {
		this.idPessoaRedator = idPessoaRedator;
	}
	public String getNomeRedator() {
		return nomeRedator;
	}
	public void setNomeRedator(String nomeRedator) {
		this.nomeRedator = nomeRedator;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getConteudo() {
		return conteudo;
	}
	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

}
