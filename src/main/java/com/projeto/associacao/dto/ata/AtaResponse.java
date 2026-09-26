package com.projeto.associacao.dto.ata;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.projeto.associacao.dto.usuario.UsuarioResponse;
import com.projeto.associacao.model.Pessoa;

public class AtaResponse {

	private int id;
	private LocalDate dataAta;
	private Pessoa pessoaRedator;
	private String nomeRedator;
	private String titulo;
	private String conteudo;
	private LocalDateTime dataHoraCadastro;
	private UsuarioResponse usuarioCadastro;
	private LocalDateTime dataHoraUltimaAlteracao;
	private UsuarioResponse usuarioUltimaAlteracao;

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
	public Pessoa getPessoaRedator() {
		return pessoaRedator;
	}
	public void setPessoaRedator(Pessoa pessoaRedator) {
		this.pessoaRedator = pessoaRedator;
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
	public LocalDateTime getDataHoraCadastro() {
		return dataHoraCadastro;
	}
	public void setDataHoraCadastro(LocalDateTime dataHoraCadastro) {
		this.dataHoraCadastro = dataHoraCadastro;
	}
	public UsuarioResponse getUsuarioCadastro() {
		return usuarioCadastro;
	}
	public void setUsuarioCadastro(UsuarioResponse usuarioCadastro) {
		this.usuarioCadastro = usuarioCadastro;
	}
	public LocalDateTime getDataHoraUltimaAlteracao() {
		return dataHoraUltimaAlteracao;
	}
	public void setDataHoraUltimaAlteracao(LocalDateTime dataHoraUltimaAlteracao) {
		this.dataHoraUltimaAlteracao = dataHoraUltimaAlteracao;
	}
	public UsuarioResponse getUsuarioUltimaAlteracao() {
		return usuarioUltimaAlteracao;
	}
	public void setUsuarioUltimaAlteracao(UsuarioResponse usuarioUltimaAlteracao) {
		this.usuarioUltimaAlteracao = usuarioUltimaAlteracao;
	}

}
