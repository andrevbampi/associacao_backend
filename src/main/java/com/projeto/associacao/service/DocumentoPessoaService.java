package com.projeto.associacao.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.projeto.associacao.dto.documentoPessoa.DocumentoPessoaResponse;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.DocumentoPessoa;
import com.projeto.associacao.model.Pessoa;
import com.projeto.associacao.model.Usuario;
import com.projeto.associacao.repository.DocumentoPessoaRepository;
import com.projeto.associacao.repository.PessoaRepository;
import com.projeto.associacao.repository.UsuarioRepository;
import com.projeto.associacao.util.ArquivoValidador;

@Service
public class DocumentoPessoaService {

	@Autowired
	private DocumentoPessoaRepository repository;

	@Autowired
	private PessoaRepository pessoaRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private UsuarioService usuarioService;

	public Iterable<DocumentoPessoaResponse> listar(int idPessoa) throws BusinessRuleException {
		if (pessoaRepository.findById(idPessoa) == null) {
			throw new BusinessRuleException("Pessoa de ID " + idPessoa + " não cadastrada.");
		}

		List<DocumentoPessoaResponse> responses = new ArrayList<>();
		for (DocumentoPessoa documento : repository.findByPessoa_IdOrderByDataUploadDesc(idPessoa)) {
			responses.add(converterParaResponse(documento));
		}
		return responses;
	}

	// loginUsuarioAutenticado vem do token JWT, nunca do corpo da requisição.
	public DocumentoPessoaResponse upload(int idPessoa, MultipartFile arquivo, String loginUsuarioAutenticado) throws BusinessRuleException {
		Pessoa pessoa = pessoaRepository.findById(idPessoa);
		if (pessoa == null) {
			throw new BusinessRuleException("Pessoa de ID " + idPessoa + " não cadastrada.");
		}

		ArquivoValidador.validarDocumento(arquivo.getContentType(), arquivo.getSize());

		Usuario usuario = usuarioRepository.findByLogin(loginUsuarioAutenticado);
		if (usuario == null) {
			throw new BusinessRuleException("Usuário autenticado não encontrado.");
		}

		DocumentoPessoa documento = new DocumentoPessoa();
		documento.setPessoa(pessoa);
		documento.setNomeOriginal(arquivo.getOriginalFilename());
		documento.setContentType(arquivo.getContentType());
		documento.setTamanho(arquivo.getSize());
		try {
			documento.setArquivo(arquivo.getBytes());
		} catch (IOException ex) {
			throw new BusinessRuleException("Não foi possível ler o arquivo enviado.");
		}
		documento.setDataUpload(LocalDateTime.now());
		documento.setUsuarioUpload(usuario);

		return converterParaResponse(repository.save(documento));
	}

	public DocumentoPessoa buscarParaDownloadOuFalhar(int id) throws BusinessRuleException {
		if (id == 0) {
			throw new BusinessRuleException("ID não informado.");
		}
		DocumentoPessoa documento = repository.findById(id);
		if (documento == null) {
			throw new BusinessRuleException("Documento de ID " + id + " não cadastrado.");
		}
		return documento;
	}

	public void remover(int id) throws BusinessRuleException {
		buscarParaDownloadOuFalhar(id);
		repository.deleteById(id);
	}

	private DocumentoPessoaResponse converterParaResponse(DocumentoPessoa documento) {
		DocumentoPessoaResponse response = new DocumentoPessoaResponse();
		response.setId(documento.getId());
		response.setNomeOriginal(documento.getNomeOriginal());
		response.setContentType(documento.getContentType());
		response.setTamanho(documento.getTamanho());
		response.setDataUpload(documento.getDataUpload());
		response.setUsuarioUpload(usuarioService.converterParaResponse(documento.getUsuarioUpload()));
		return response;
	}

}
