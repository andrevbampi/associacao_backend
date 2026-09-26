package com.projeto.associacao.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projeto.associacao.dto.ata.AtaRequest;
import com.projeto.associacao.dto.ata.AtaResponse;
import com.projeto.associacao.model.Ata;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.Pessoa;
import com.projeto.associacao.model.Usuario;
import com.projeto.associacao.repository.AtaRepository;
import com.projeto.associacao.repository.DocumentoAtaRepository;
import com.projeto.associacao.repository.PessoaRepository;
import com.projeto.associacao.repository.UsuarioRepository;

@Service
public class AtaService {

	@Autowired
	private AtaRepository repository;

	@Autowired
	private PessoaRepository pessoaRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private DocumentoAtaRepository documentoAtaRepository;

	@Autowired
	private UsuarioService usuarioService;

	public Iterable<AtaResponse> selecionar() {
		return converterLista(repository.findAllByOrderByDataAtaDesc());
	}

	// loginUsuarioAutenticado vem do token JWT (Authentication), não do corpo da
	// requisição: quem cadastrou/alterou é sempre quem está logado.
	public AtaResponse cadastrar(AtaRequest request, String loginUsuarioAutenticado) throws BusinessRuleException {
		Usuario usuario = buscarUsuarioOuFalhar(loginUsuarioAutenticado);

		Ata ata = new Ata();
		this.preencher(ata, request);

		LocalDateTime agora = LocalDateTime.now();
		ata.setDataHoraCadastro(agora);
		ata.setUsuarioCadastro(usuario);
		ata.setDataHoraUltimaAlteracao(agora);
		ata.setUsuarioUltimaAlteracao(usuario);

		return converterParaResponse(repository.save(ata));
	}

	public AtaResponse alterar(AtaRequest request, String loginUsuarioAutenticado) throws BusinessRuleException {
		if (request.getId() == 0) {
			throw new BusinessRuleException("ID não informado.");
		}

		Ata ata = repository.findById(request.getId());
		if (ata == null) {
			throw new BusinessRuleException("Ata de ID " + request.getId() + " não cadastrada.");
		}

		this.preencher(ata, request);
		this.tocarUltimaAlteracao(ata, loginUsuarioAutenticado);

		return converterParaResponse(repository.save(ata));
	}

	public void remover(int id) throws BusinessRuleException {
		if (id == 0) {
			throw new BusinessRuleException("ID não informado.");
		}

		if (repository.findById(id) == null) {
			throw new BusinessRuleException("Ata de ID " + id + " não cadastrada.");
		}

		if (documentoAtaRepository.existsByAta_Id(id)) {
			throw new BusinessRuleException("A ata de ID " + id + " possui documentos anexados. Exclua os documentos antes de excluir a ata.");
		}

		repository.deleteById(id);
	}

	// Chamado pela AtaService (edição) e pelo DocumentoAtaService (upload/exclusão
	// de documento), já que ambos "alteram" a ata para fins de auditoria.
	public void tocarUltimaAlteracao(int idAta, String loginUsuarioAutenticado) throws BusinessRuleException {
		Ata ata = buscarOuFalhar(idAta);
		this.tocarUltimaAlteracao(ata, loginUsuarioAutenticado);
		repository.save(ata);
	}

	private void tocarUltimaAlteracao(Ata ata, String loginUsuarioAutenticado) throws BusinessRuleException {
		ata.setDataHoraUltimaAlteracao(LocalDateTime.now());
		ata.setUsuarioUltimaAlteracao(buscarUsuarioOuFalhar(loginUsuarioAutenticado));
	}

	public Ata buscarOuFalhar(int id) throws BusinessRuleException {
		if (id == 0) {
			throw new BusinessRuleException("ID não informado.");
		}
		Ata ata = repository.findById(id);
		if (ata == null) {
			throw new BusinessRuleException("Ata de ID " + id + " não cadastrada.");
		}
		return ata;
	}

	private Usuario buscarUsuarioOuFalhar(String login) throws BusinessRuleException {
		Usuario usuario = usuarioRepository.findByLogin(login);
		if (usuario == null) {
			throw new BusinessRuleException("Usuário autenticado não encontrado.");
		}
		return usuario;
	}

	private void preencher(Ata ata, AtaRequest request) throws BusinessRuleException {
		if (request.getDataAta() == null) {
			throw new BusinessRuleException("Data da ata não informada.");
		}
		if ((request.getConteudo() == null) || request.getConteudo().isBlank()) {
			throw new BusinessRuleException("Conteúdo da ata não informado.");
		}

		Pessoa pessoaRedator = null;
		if (request.getIdPessoaRedator() != 0) {
			pessoaRedator = pessoaRepository.findById(request.getIdPessoaRedator());
			if (pessoaRedator == null) {
				throw new BusinessRuleException("Não existe pessoa cadastrada com o ID " + request.getIdPessoaRedator());
			}
		}

		ata.setDataAta(request.getDataAta());
		ata.setPessoaRedator(pessoaRedator);
		ata.setNomeRedator(request.getNomeRedator());
		ata.setTitulo(request.getTitulo());
		ata.setConteudo(request.getConteudo());
	}

	private List<AtaResponse> converterLista(Iterable<Ata> atas) {
		List<AtaResponse> responses = new ArrayList<>();
		for (Ata ata : atas) {
			responses.add(converterParaResponse(ata));
		}
		return responses;
	}

	private AtaResponse converterParaResponse(Ata ata) {
		AtaResponse response = new AtaResponse();
		response.setId(ata.getId());
		response.setDataAta(ata.getDataAta());
		response.setPessoaRedator(ata.getPessoaRedator());
		response.setNomeRedator(ata.getNomeRedator());
		response.setTitulo(ata.getTitulo());
		response.setConteudo(ata.getConteudo());
		response.setDataHoraCadastro(ata.getDataHoraCadastro());
		response.setUsuarioCadastro(usuarioService.converterParaResponse(ata.getUsuarioCadastro()));
		response.setDataHoraUltimaAlteracao(ata.getDataHoraUltimaAlteracao());
		response.setUsuarioUltimaAlteracao(usuarioService.converterParaResponse(ata.getUsuarioUltimaAlteracao()));
		return response;
	}

}
