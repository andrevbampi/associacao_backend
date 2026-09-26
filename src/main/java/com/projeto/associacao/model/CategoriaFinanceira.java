package com.projeto.associacao.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "categoria_financeira")
public class CategoriaFinanceira {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String descricao;

	@Enumerated(EnumType.STRING)
	private TipoCategoriaFinanceira tipo;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public TipoCategoriaFinanceira getTipo() {
		return tipo;
	}
	public void setTipo(TipoCategoriaFinanceira tipo) {
		this.tipo = tipo;
	}

}
