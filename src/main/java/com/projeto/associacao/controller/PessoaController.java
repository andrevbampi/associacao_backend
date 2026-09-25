package com.projeto.associacao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.Pessoa;
import com.projeto.associacao.service.PessoaService;

@RestController
@RequestMapping("/api/pessoa")
public class PessoaController {

    @Autowired
    private PessoaService service;

    @GetMapping("/")
    public Iterable<Pessoa> selecionar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Integer tipo,
            @RequestParam(required = false) Boolean semUsuario,
            @RequestParam(required = false) Boolean semMembro) {
        return service.selecionar(nome, tipo, semUsuario, semMembro);
    }

    @PostMapping("/")
    public Pessoa cadastrar(@RequestBody Pessoa pessoa) throws BusinessRuleException {
        return service.cadastrar(pessoa);
    }

    @PutMapping("/")
    public Pessoa alterar(@RequestBody Pessoa pessoa) throws BusinessRuleException {
        return service.alterar(pessoa);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable int id) throws BusinessRuleException {
        service.remover(id);
    }
}
