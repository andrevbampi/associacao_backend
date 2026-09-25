package com.projeto.associacao.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projeto.associacao.dto.historicoMembro.HistoricoMembroRequest;
import com.projeto.associacao.dto.historicoMembro.HistoricoMembroResponse;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.HistoricoMembro;
import com.projeto.associacao.model.Membro;
import com.projeto.associacao.model.TipoEvento;
import com.projeto.associacao.model.Usuario;
import com.projeto.associacao.repository.HistoricoMembroRepository;
import com.projeto.associacao.repository.MembroRepository;
import com.projeto.associacao.repository.TipoEventoRepository;
import com.projeto.associacao.repository.UsuarioRepository;

@Service
public class HistoricoMembroService {

	@Autowired
	private HistoricoMembroRepository repository;

	@Autowired
	private MembroRepository membroRepository;

	@Autowired
	private TipoEventoRepository tipoEventoRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private MembroService membroService;

	@Autowired
	private UsuarioService usuarioService;

	public Iterable<HistoricoMembroResponse> selecionar() {
		return converterLista(repository.findAll());
	}

	public Iterable<HistoricoMembroResponse> selecionarPorMembro(int idMembro) throws BusinessRuleException {
		if (membroRepository.findById(idMembro) == null) {
			throw new BusinessRuleException("Membro de ID " + idMembro + " não cadastrado.");
		}
		return converterLista(repository.findByMembro_IdOrderByDataDesc(idMembro));
	}

	// loginUsuarioAutenticado vem do token JWT (Authentication), não do corpo da
	// requisição: quem registrou o evento é sempre quem está logado, nunca um
	// valor que o cliente poderia manipular.
	public HistoricoMembroResponse cadastrar(HistoricoMembroRequest request, String loginUsuarioAutenticado) throws BusinessRuleException {
		HistoricoMembro historico = this.validarHistorico(request, false, loginUsuarioAutenticado);
		return converterParaResponse(repository.save(historico));
	}

	public HistoricoMembroResponse alterar(HistoricoMembroRequest request, String loginUsuarioAutenticado) throws BusinessRuleException {
		HistoricoMembro historico = this.validarHistorico(request, true, loginUsuarioAutenticado);
		return converterParaResponse(repository.save(historico));
	}

	public void remover(int id) throws BusinessRuleException {
		if (id == 0) {
			throw new BusinessRuleException("ID não informado.");
		}

		if (repository.findById(id) == null) {
			throw new BusinessRuleException("Registro de histórico de ID " + id + " não cadastrado.");
		}

		repository.deleteById(id);
	}

	private HistoricoMembro validarHistorico(HistoricoMembroRequest request, boolean edicao, String loginUsuarioAutenticado) throws BusinessRuleException {
		if (request.getIdMembro() == 0) {
			throw new BusinessRuleException("Membro não informado.");
		}

		if (request.getIdTipoEvento() == 0) {
			throw new BusinessRuleException("Tipo de evento não informado.");
		}

		if ((request.getDescricao() == null) || request.getDescricao().isBlank()) {
			throw new BusinessRuleException("Descrição não informada.");
		}

		HistoricoMembro historico;
		if (edicao) {
			if (request.getId() == 0) {
				throw new BusinessRuleException("ID não informado.");
			}

			historico = repository.findById(request.getId());
			if (historico == null) {
				throw new BusinessRuleException("Registro de histórico de ID " + request.getId() + " não cadastrado.");
			}
		} else {
			historico = new HistoricoMembro();

			Usuario usuarioRegistro = usuarioRepository.findByLogin(loginUsuarioAutenticado);
			if (usuarioRegistro == null) {
				throw new BusinessRuleException("Usuário autenticado não encontrado.");
			}
			historico.setUsuarioRegistro(usuarioRegistro);
		}

		Membro membro = membroRepository.findById(request.getIdMembro());
		if (membro == null) {
			throw new BusinessRuleException("Não existe membro cadastrado com o ID " + request.getIdMembro());
		}

		TipoEvento tipoEvento = tipoEventoRepository.findById(request.getIdTipoEvento());
		if (tipoEvento == null) {
			throw new BusinessRuleException("Não existe tipo de evento cadastrado com o ID " + request.getIdTipoEvento());
		}

		historico.setMembro(membro);
		historico.setTipoEvento(tipoEvento);
		historico.setDescricao(request.getDescricao());
		historico.setData(request.getData() != null ? request.getData() : LocalDateTime.now());
		historico.setObservacao(request.getObservacao());
		historico.setAtivo(request.isAtivo());
		return historico;
	}

	private List<HistoricoMembroResponse> converterLista(Iterable<HistoricoMembro> historicos) {
		List<HistoricoMembroResponse> responses = new ArrayList<>();
		for (HistoricoMembro historico : historicos) {
			responses.add(converterParaResponse(historico));
		}
		return responses;
	}

	private HistoricoMembroResponse converterParaResponse(HistoricoMembro historico) {
		HistoricoMembroResponse response = new HistoricoMembroResponse();
		response.setId(historico.getId());
		response.setMembro(membroService.converterParaResponse(historico.getMembro()));
		response.setTipoEvento(historico.getTipoEvento());
		response.setDescricao(historico.getDescricao());
		response.setData(historico.getData());
		response.setUsuarioRegistro(usuarioService.converterParaResponse(historico.getUsuarioRegistro()));
		response.setObservacao(historico.getObservacao());
		response.setAtivo(historico.isAtivo());
		return response;
	}
}
