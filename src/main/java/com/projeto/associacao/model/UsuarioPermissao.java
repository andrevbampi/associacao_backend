package com.projeto.associacao.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario_permissao")
public class UsuarioPermissao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "idusuario", nullable = false)
	private Usuario usuario;

	@ManyToOne
	@JoinColumn(name = "idpermissao", nullable = false)
	private Permissao permissao;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private EfeitoPermissao efeito;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Permissao getPermissao() {
		return permissao;
	}
	public void setPermissao(Permissao permissao) {
		this.permissao = permissao;
	}

	public EfeitoPermissao getEfeito() {
		return efeito;
	}
	public void setEfeito(EfeitoPermissao efeito) {
		this.efeito = efeito;
	}

}
