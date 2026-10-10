package com.projeto.associacao.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projeto.associacao.dto.acesso.AuditoriaAcessoResponse;
import com.projeto.associacao.model.AuditoriaAcesso;
import com.projeto.associacao.model.Usuario;
import com.projeto.associacao.repository.AuditoriaAcessoRepository;
import com.projeto.associacao.repository.UsuarioRepository;

/** Trilha de auditoria das mudanças de controle de acesso (quem, quando, o quê). */
@Service
public class AuditoriaAcessoService {

	@Autowired
	private AuditoriaAcessoRepository repository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	public void registrar(String loginResponsavel, String acao, String entidade, Integer idEntidade, String descricao) {
		AuditoriaAcesso auditoria = new AuditoriaAcesso();
		auditoria.setDataHora(LocalDateTime.now());
		Usuario usuario = (loginResponsavel != null) ? usuarioRepository.findByLogin(loginResponsavel) : null;
		auditoria.setUsuario(usuario);
		auditoria.setLogin((loginResponsavel != null) ? loginResponsavel : "sistema");
		auditoria.setAcao(acao);
		auditoria.setEntidade(entidade);
		auditoria.setIdEntidade(idEntidade);
		auditoria.setDescricao((descricao.length() > 1000) ? descricao.substring(0, 997) + "..." : descricao);
		repository.save(auditoria);
	}

	public Iterable<AuditoriaAcessoResponse> selecionar(LocalDate dataInicio, LocalDate dataFim, String entidade, String login) {
		List<AuditoriaAcessoResponse> respostas = new ArrayList<>();
		for (AuditoriaAcesso a : repository.findAll()) {
			LocalDate dia = a.getDataHora().toLocalDate();
			if ((dataInicio != null) && dia.isBefore(dataInicio)) {
				continue;
			}
			if ((dataFim != null) && dia.isAfter(dataFim)) {
				continue;
			}
			if ((entidade != null) && !entidade.isBlank() && !a.getEntidade().equalsIgnoreCase(entidade.trim())) {
				continue;
			}
			if ((login != null) && !login.isBlank() && !a.getLogin().toLowerCase().contains(login.trim().toLowerCase())) {
				continue;
			}
			AuditoriaAcessoResponse r = new AuditoriaAcessoResponse();
			r.setId(a.getId());
			r.setDataHora(a.getDataHora());
			r.setLogin(a.getLogin());
			r.setAcao(a.getAcao());
			r.setEntidade(a.getEntidade());
			r.setIdEntidade(a.getIdEntidade());
			r.setDescricao(a.getDescricao());
			respostas.add(r);
		}
		respostas.sort(Comparator.comparing(AuditoriaAcessoResponse::getDataHora).reversed());
		return respostas;
	}

}
