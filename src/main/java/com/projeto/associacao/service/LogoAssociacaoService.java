package com.projeto.associacao.service;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.LogoAssociacao;
import com.projeto.associacao.repository.LogoAssociacaoRepository;
import com.projeto.associacao.util.ArquivoValidador;

@Service
public class LogoAssociacaoService {

	@Autowired
	private LogoAssociacaoRepository repository;

	// Devolve null se nenhuma logo foi cadastrada ainda.
	public LogoAssociacao buscar() {
		LogoAssociacao logo = repository.findById(LogoAssociacao.ID_FIXO);
		if ((logo == null) || (logo.getArquivo() == null) || (logo.getArquivo().length == 0)) {
			return null;
		}
		return logo;
	}

	public void salvar(MultipartFile arquivo) throws BusinessRuleException {
		ArquivoValidador.validarImagem(arquivo.getContentType(), arquivo.getSize());

		LogoAssociacao logo = repository.findById(LogoAssociacao.ID_FIXO);
		if (logo == null) {
			logo = new LogoAssociacao();
		}

		try {
			logo.setArquivo(arquivo.getBytes());
		} catch (IOException ex) {
			throw new BusinessRuleException("Não foi possível ler o arquivo enviado.");
		}
		logo.setContentType(arquivo.getContentType());
		logo.setNomeOriginal(arquivo.getOriginalFilename());
		logo.setDataUpload(LocalDateTime.now());
		repository.save(logo);
	}

	public void remover() {
		LogoAssociacao logo = repository.findById(LogoAssociacao.ID_FIXO);
		if (logo != null) {
			repository.deleteById(LogoAssociacao.ID_FIXO);
		}
	}

}
