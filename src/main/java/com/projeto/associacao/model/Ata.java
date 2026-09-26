package com.projeto.associacao.model;

import java.time.LocalDate;
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
@Table(name = "ata")
public class Ata {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "dataata")
	private LocalDate dataAta;

	// Redator: ou uma pessoa cadastrada (idPessoaRedator), ou só um nome livre
	// (nomeRedator) para quem não tem cadastro no sistema. Os dois são opcionais.
	@ManyToOne
	@JoinColumn(name = "idpessoaredator")
	private Pessoa pessoaRedator;

	@Column(name = "nomeredator")
	private String nomeRedator;

	private String titulo;

	@Lob
	private String conteudo;

	@Column(name = "datahoracadastro")
	private LocalDateTime dataHoraCadastro;

	@ManyToOne
	@JoinColumn(name = "idusuariocadastro", nullable = false)
	private Usuario usuarioCadastro;

	@Column(name = "datahoraultimaalteracao")
	private LocalDateTime dataHoraUltimaAlteracao;

	@ManyToOne
	@JoinColumn(name = "idusuarioultimaalteracao", nullable = false)
	private Usuario usuarioUltimaAlteracao;

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
	public Usuario getUsuarioCadastro() {
		return usuarioCadastro;
	}
	public void setUsuarioCadastro(Usuario usuarioCadastro) {
		this.usuarioCadastro = usuarioCadastro;
	}
	public LocalDateTime getDataHoraUltimaAlteracao() {
		return dataHoraUltimaAlteracao;
	}
	public void setDataHoraUltimaAlteracao(LocalDateTime dataHoraUltimaAlteracao) {
		this.dataHoraUltimaAlteracao = dataHoraUltimaAlteracao;
	}
	public Usuario getUsuarioUltimaAlteracao() {
		return usuarioUltimaAlteracao;
	}
	public void setUsuarioUltimaAlteracao(Usuario usuarioUltimaAlteracao) {
		this.usuarioUltimaAlteracao = usuarioUltimaAlteracao;
	}

}
