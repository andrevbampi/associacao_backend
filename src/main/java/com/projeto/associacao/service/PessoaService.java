package com.projeto.associacao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.Pessoa;
import com.projeto.associacao.repository.MembroRepository;
import com.projeto.associacao.repository.PessoaRepository;
import com.projeto.associacao.repository.UsuarioRepository;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MembroRepository membroRepository;

    public Iterable<Pessoa> selecionar() {
        return repository.findAll();
    }

    public Pessoa cadastrar(Pessoa pessoa) throws BusinessRuleException {
        this.validarPessoa(pessoa);
        pessoa.setId(0);
        return repository.save(pessoa);
    }

    public Pessoa alterar(Pessoa pessoa) throws BusinessRuleException {
        if (pessoa.getId() == 0) {
            throw new BusinessRuleException("ID não informado.");
        }

        if (repository.findById(pessoa.getId()) == null) {
            throw new BusinessRuleException("Pessoa de ID " + pessoa.getId() + " não cadastrada.");
        }

        this.validarPessoa(pessoa);
        return repository.save(pessoa);
    }

    public void remover (int id) throws BusinessRuleException{
        if (id == 0) {
			throw new BusinessRuleException("ID não informado.");
		}

        if (repository.findById(id) == null) {
            throw new BusinessRuleException("Pessoa de ID " + id + " não cadastrada.");
        }

        if (usuarioRepository.findByPessoa_Id(id) != null) {
            throw new BusinessRuleException("A pessoa de ID " + id + " possui um usuário vinculado.");
        }

        if (membroRepository.findByPessoa_Id(id) != null) {
            throw new BusinessRuleException("A pessoa de ID " + id + " possui um cadastro de membro vinculado.");
        }

		repository.deleteById(id);
    }

    private void validarPessoa (Pessoa pessoa) throws BusinessRuleException {
        if ((pessoa.getTipo() < 1) || (pessoa.getTipo() > 2)) {
            throw new BusinessRuleException("Tipo não informado.");
        }

        if ((pessoa.getNome() == null) || pessoa.getNome().isBlank()) {
            throw new BusinessRuleException("Nome não informado.");
        }

        if ((pessoa.getDocumento() == null) || pessoa.getDocumento().isBlank()) {
            throw new BusinessRuleException("Documento não informado.");
        }
    }

}
