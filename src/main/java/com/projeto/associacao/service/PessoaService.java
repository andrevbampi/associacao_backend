package com.projeto.associacao.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.Pessoa;
import com.projeto.associacao.repository.MembroRepository;
import com.projeto.associacao.repository.PessoaRepository;
import com.projeto.associacao.repository.UsuarioRepository;
import com.projeto.associacao.util.ArquivoValidador;
import com.projeto.associacao.util.DocumentoValidador;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MembroRepository membroRepository;

    public Iterable<Pessoa> selecionar(String nome, Integer tipo, Boolean semUsuario, Boolean semMembro) {
        List<Pessoa> resultado = new ArrayList<>();
        for (Pessoa pessoa : repository.findAll()) {
            if ((nome != null) && !nome.isBlank() && !pessoa.getNome().toLowerCase().contains(nome.trim().toLowerCase())) {
                continue;
            }
            if ((tipo != null) && (pessoa.getTipo() != tipo)) {
                continue;
            }
            if (Boolean.TRUE.equals(semUsuario) && (usuarioRepository.findByPessoa_Id(pessoa.getId()) != null)) {
                continue;
            }
            if (Boolean.TRUE.equals(semMembro) && (membroRepository.findByPessoa_Id(pessoa.getId()) != null)) {
                continue;
            }
            resultado.add(pessoa);
        }
        return resultado;
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

        Pessoa existente = repository.findById(pessoa.getId());
        if (existente == null) {
            throw new BusinessRuleException("Pessoa de ID " + pessoa.getId() + " não cadastrada.");
        }

        this.validarPessoa(pessoa);

        // A foto nunca vem no corpo da requisição (@JsonIgnore no getter), então
        // salvar a pessoa recebida direto sobrescreveria a foto já gravada com null.
        pessoa.setFoto(existente.getFoto());
        pessoa.setFotoContentType(existente.getFotoContentType());
        pessoa.setFotoNomeOriginal(existente.getFotoNomeOriginal());
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

    public void salvarFoto(int id, MultipartFile arquivo) throws BusinessRuleException {
        Pessoa pessoa = buscarOuFalhar(id);
        ArquivoValidador.validarImagem(arquivo.getContentType(), arquivo.getSize());

        try {
            pessoa.setFoto(arquivo.getBytes());
        } catch (IOException ex) {
            throw new BusinessRuleException("Não foi possível ler o arquivo enviado.");
        }
        pessoa.setFotoContentType(arquivo.getContentType());
        pessoa.setFotoNomeOriginal(arquivo.getOriginalFilename());
        repository.save(pessoa);
    }

    public void removerFoto(int id) throws BusinessRuleException {
        Pessoa pessoa = buscarOuFalhar(id);
        pessoa.setFoto(null);
        pessoa.setFotoContentType(null);
        pessoa.setFotoNomeOriginal(null);
        repository.save(pessoa);
    }

    public Pessoa buscarOuFalhar(int id) throws BusinessRuleException {
        if (id == 0) {
            throw new BusinessRuleException("ID não informado.");
        }
        Pessoa pessoa = repository.findById(id);
        if (pessoa == null) {
            throw new BusinessRuleException("Pessoa de ID " + id + " não cadastrada.");
        }
        return pessoa;
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

        // A máscara (pontos/traço) é só visual, aplicada no front-end; o que fica
        // gravado é sempre só os caracteres do documento em si.
        pessoa.setDocumento(pessoa.getDocumento().replaceAll("[^0-9A-Za-z]", "").toUpperCase());

        if (pessoa.getTipo() == 1) {
            if (!DocumentoValidador.validarCpf(pessoa.getDocumento())) {
                throw new BusinessRuleException("CPF inválido.");
            }
        } else if (pessoa.getTipo() == 2) {
            if (!DocumentoValidador.validarCnpj(pessoa.getDocumento())) {
                throw new BusinessRuleException("CNPJ inválido.");
            }
        }
    }

}
