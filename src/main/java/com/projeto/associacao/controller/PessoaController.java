package com.projeto.associacao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.projeto.associacao.dto.documentoPessoa.DocumentoPessoaResponse;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.Pessoa;
import com.projeto.associacao.service.DocumentoPessoaService;
import com.projeto.associacao.service.PessoaService;
import com.projeto.associacao.security.Permissoes;

@RestController
@RequestMapping("/api/pessoa")
public class PessoaController {

    @Autowired
    private PessoaService service;

    @Autowired
    private DocumentoPessoaService documentoService;

    @GetMapping("/")
    @PreAuthorize(Permissoes.LOOKUP_PESSOA)
    public Iterable<Pessoa> selecionar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Integer tipo,
            @RequestParam(required = false) Boolean semUsuario,
            @RequestParam(required = false) Boolean semMembro) {
        return service.selecionar(nome, tipo, semUsuario, semMembro);
    }

    @PostMapping("/")
    @PreAuthorize("hasAuthority('" + Permissoes.PESSOA_CRIAR + "')")
    public Pessoa cadastrar(@RequestBody Pessoa pessoa) throws BusinessRuleException {
        return service.cadastrar(pessoa);
    }

    @PutMapping("/")
    @PreAuthorize("hasAuthority('" + Permissoes.PESSOA_EDITAR + "')")
    public Pessoa alterar(@RequestBody Pessoa pessoa) throws BusinessRuleException {
        return service.alterar(pessoa);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permissoes.PESSOA_EXCLUIR + "')")
    public void remover(@PathVariable int id) throws BusinessRuleException {
        service.remover(id);
    }

    @GetMapping("/{id}/foto")
    public ResponseEntity<byte[]> buscarFoto(@PathVariable int id) throws BusinessRuleException {
        Pessoa pessoa = service.buscarOuFalhar(id);
        if ((pessoa.getFoto() == null) || (pessoa.getFoto().length == 0)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(pessoa.getFotoContentType()))
                .body(pessoa.getFoto());
    }

    @PutMapping("/{id}/foto")
    @PreAuthorize("hasAuthority('" + Permissoes.PESSOA_FOTO + "')")
    public void salvarFoto(@PathVariable int id, @RequestParam("arquivo") MultipartFile arquivo) throws BusinessRuleException {
        service.salvarFoto(id, arquivo);
    }

    @DeleteMapping("/{id}/foto")
    @PreAuthorize("hasAuthority('" + Permissoes.PESSOA_FOTO + "')")
    public void removerFoto(@PathVariable int id) throws BusinessRuleException {
        service.removerFoto(id);
    }

    @GetMapping("/{id}/documentos")
    @PreAuthorize("hasAuthority('" + Permissoes.PESSOA_DOCUMENTO_VISUALIZAR + "')")
    public Iterable<DocumentoPessoaResponse> listarDocumentos(@PathVariable int id) throws BusinessRuleException {
        return documentoService.listar(id);
    }

    @PostMapping("/{id}/documentos")
    @PreAuthorize("hasAuthority('" + Permissoes.PESSOA_DOCUMENTO_GERENCIAR + "')")
    public DocumentoPessoaResponse uploadDocumento(@PathVariable int id, @RequestParam("arquivo") MultipartFile arquivo, Authentication authentication) throws BusinessRuleException {
        return documentoService.upload(id, arquivo, authentication.getName());
    }
}
