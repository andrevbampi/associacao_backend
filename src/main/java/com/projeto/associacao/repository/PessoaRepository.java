package com.projeto.associacao.repository;

import org.springframework.data.repository.CrudRepository;

import com.projeto.associacao.model.Pessoa;

public interface PessoaRepository extends CrudRepository <Pessoa, Integer> {
    
}
