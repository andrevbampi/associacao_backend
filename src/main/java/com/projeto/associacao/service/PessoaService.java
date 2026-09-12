package com.projeto.associacao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.Pessoa;
import com.projeto.associacao.repository.PessoaRepository;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository repository;

    public Iterable<Pessoa> selecionar() {
        return repository.findAll();
    }

    public Pessoa cadastrar(Pessoa pessoa) throws BusinessRuleException {
        this.validarPessoa(pessoa);
        return repository.save(pessoa);
    }

    public void remover (int id) throws BusinessRuleException{
        if (id == 0) {
			throw new BusinessRuleException("ID não informado.");
		}
		repository.deleteById(id);
    }

    private void validarPessoa (Pessoa pessoa) throws BusinessRuleException {
        if ((pessoa.getTipo() < 1) && (pessoa.getTipo() > 2)) {
            throw new BusinessRuleException("Tipo não informado.");
        }

        if (pessoa.getNome().trim().equals("")) {
            throw new BusinessRuleException("Nome não informado.");
        }
    }
    
}
