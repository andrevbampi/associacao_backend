package com.projeto.associacao.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.projeto.associacao.dto.documentoAta.DocumentoAtaResponse;
import com.projeto.associacao.model.Ata;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.DocumentoAta;
import com.projeto.associacao.model.Usuario;
import com.projeto.associacao.repository.DocumentoAtaRepository;
import com.projeto.associacao.repository.UsuarioRepository;
import com.projeto.associacao.util.ArquivoValidador;

@Service
public class DocumentoAtaService {

	@Autowired
	private DocumentoAtaRepository repository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private AtaService ataService;

	public Iterable<DocumentoAtaResponse> listar(int idAta) throws BusinessRuleException {
		ataService.buscarOuFalhar(idAta);

		List<DocumentoAtaResponse> responses = new ArrayList<>();
		for (DocumentoAta documento : repository.findByAta_IdOrderByDataUploadDesc(idAta)) {
			responses.add(converterParaResponse(documento));
		}
		return responses;
	}

	// loginUsuarioAutenticado vem do token JWT, nunca do corpo da requisição.
	public DocumentoAtaResponse upload(int idAta, MultipartFile arquivo, String loginUsuarioAutenticado) throws BusinessRuleException {
		Ata ata = ataService.buscarOuFalhar(idAta);

		ArquivoValidador.validarDocumento(arquivo.getContentType(), arquivo.getSize());

		Usuario usuario = usuarioRepository.findByLogin(loginUsuarioAutenticado);
		if (usuario == null) {
			throw new BusinessRuleException("Usuário autenticado não encontrado.");
		}

		DocumentoAta documento = new DocumentoAta();
		documento.setAta(ata);
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

		DocumentoAtaResponse response = converterParaResponse(repository.save(documento));
		ataService.tocarUltimaAlteracao(idAta, loginUsuarioAutenticado);
		return response;
	}

	public DocumentoAta buscarParaDownloadOuFalhar(int id) throws BusinessRuleException {
		if (id == 0) {
			throw new BusinessRuleException("ID não informado.");
		}
		DocumentoAta documento = repository.findById(id);
		if (documento == null) {
			throw new BusinessRuleException("Documento de ID " + id + " não cadastrado.");
		}
		return documento;
	}

	public void remover(int id, String loginUsuarioAutenticado) throws BusinessRuleException {
		DocumentoAta documento = buscarParaDownloadOuFalhar(id);
		int idAta = documento.getAta().getId();
		repository.deleteById(id);
		ataService.tocarUltimaAlteracao(idAta, loginUsuarioAutenticado);
	}

	private DocumentoAtaResponse converterParaResponse(DocumentoAta documento) {
		DocumentoAtaResponse response = new DocumentoAtaResponse();
		response.setId(documento.getId());
		response.setNomeOriginal(documento.getNomeOriginal());
		response.setContentType(documento.getContentType());
		response.setTamanho(documento.getTamanho());
		response.setDataUpload(documento.getDataUpload());
		response.setUsuarioUpload(usuarioService.converterParaResponse(documento.getUsuarioUpload()));
		return response;
	}

}
