package com.projeto.associacao.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "auditoria_acesso")
public class AuditoriaAcesso {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "datahora", nullable = false)
	private LocalDateTime dataHora;

	@ManyToOne
	@JoinColumn(name = "idusuario")
	private Usuario usuario;

	@Column(nullable = false, length = 100)
	private String login;

	@Column(nullable = false, length = 30)
	private String acao;

	@Column(nullable = false, length = 30)
	private String entidade;

	@Column(name = "identidade")
	private Integer idEntidade;

	@Column(nullable = false, length = 1000)
	private String descricao;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public LocalDateTime getDataHora() {
		return dataHora;
	}
	public void setDataHora(LocalDateTime dataHora) {
		this.dataHora = dataHora;
	}

	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}

	public String getAcao() {
		return acao;
	}
	public void setAcao(String acao) {
		this.acao = acao;
	}

	public String getEntidade() {
		return entidade;
	}
	public void setEntidade(String entidade) {
		this.entidade = entidade;
	}

	public Integer getIdEntidade() {
		return idEntidade;
	}
	public void setIdEntidade(Integer idEntidade) {
		this.idEntidade = idEntidade;
	}

	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
