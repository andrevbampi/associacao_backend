package com.projeto.associacao.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "pessoa")
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int tipo;
    private String nome;
    private String documento;

    @Column(name = "datanascimento")
    private LocalDate dataNascimento;
    private String telefone;
    private String email;
    private String endereco;

    @Lob
    @Column(name = "foto")
    private byte[] foto;

    @Column(name = "fotocontenttype")
    private String fotoContentType;

    @Column(name = "fotonomeoriginal")
    private String fotoNomeOriginal;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getTipo() {
        return tipo;
    }
    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getTelefone() {
        return telefone;
    }
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getDocumento() {
        return documento;
    }
    public void setDocumento(String documento) {
        this.documento = documento;
    }
    public LocalDate getDataNascimento() {
        return dataNascimento;
    }
    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
    public String getEndereco() {
        return endereco;
    }
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    // Nunca vai no JSON: a foto é servida por um endpoint dedicado
    // (GET /api/pessoa/{id}/foto), não embutida em cada resposta de listagem.
    @JsonIgnore
    public byte[] getFoto() {
        return foto;
    }
    public void setFoto(byte[] foto) {
        this.foto = foto;
    }
    @JsonIgnore
    public String getFotoContentType() {
        return fotoContentType;
    }
    public void setFotoContentType(String fotoContentType) {
        this.fotoContentType = fotoContentType;
    }
    @JsonIgnore
    public String getFotoNomeOriginal() {
        return fotoNomeOriginal;
    }
    public void setFotoNomeOriginal(String fotoNomeOriginal) {
        this.fotoNomeOriginal = fotoNomeOriginal;
    }

    // Único indício da foto que aparece no JSON — o front usa isso para decidir
    // se busca a imagem em GET /api/pessoa/{id}/foto ou mostra o ícone padrão.
    public boolean isTemFoto() {
        return (foto != null) && (foto.length > 0);
    }

}
