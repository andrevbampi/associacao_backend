package com.projeto.associacao.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projeto.associacao.dto.acesso.ExcecaoPermissaoDto;
import com.projeto.associacao.dto.acesso.PermissaoEfetivaResponse;
import com.projeto.associacao.dto.acesso.UsuarioAcessoRequest;
import com.projeto.associacao.dto.acesso.UsuarioAcessoResponse;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.EfeitoPermissao;
import com.projeto.associacao.model.Grupo;
import com.projeto.associacao.model.Usuario;
import com.projeto.associacao.model.UsuarioGrupo;
import com.projeto.associacao.model.UsuarioPermissao;
import com.projeto.associacao.repository.GrupoRepository;
import com.projeto.associacao.repository.PermissaoRepository;
import com.projeto.associacao.repository.UsuarioGrupoRepository;
import com.projeto.associacao.repository.UsuarioPermissaoRepository;
import com.projeto.associacao.repository.UsuarioRepository;
import com.projeto.associacao.security.Permissoes;

/** Grupos e exceções de permissão de um usuário. */
@Service
public class UsuarioAcessoService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private GrupoRepository grupoRepository;

	@Autowired
	private PermissaoRepository permissaoRepository;

	@Autowired
	private UsuarioGrupoRepository usuarioGrupoRepository;

	@Autowired
	private UsuarioPermissaoRepository usuarioPermissaoRepository;

	@Autowired
	private PermissaoService permissaoService;

	@Autowired
	private AuditoriaAcessoService auditoriaService;

	public UsuarioAcessoResponse obter(int idUsuario) throws BusinessRuleException {
		Usuario usuario = buscarUsuario(idUsuario);

		UsuarioAcessoResponse r = new UsuarioAcessoResponse();
		r.setIdUsuario(usuario.getId());
		r.setLogin(usuario.getLogin());

		List<Integer> idsGrupos = new ArrayList<>();
		for (UsuarioGrupo ug : usuarioGrupoRepository.findByUsuario_Id(idUsuario)) {
			idsGrupos.add(ug.getGrupo().getId());
		}
		r.setIdsGrupos(idsGrupos);

		List<ExcecaoPermissaoDto> excecoes = new ArrayList<>();
		for (UsuarioPermissao up : usuarioPermissaoRepository.findByUsuario_Id(idUsuario)) {
			ExcecaoPermissaoDto e = new ExcecaoPermissaoDto();
			e.setCodigo(up.getPermissao().getCodigo());
			e.setEfeito(up.getEfeito().name());
			excecoes.add(e);
		}
		r.setExcecoes(excecoes);

		List<PermissaoEfetivaResponse> efetivas = new ArrayList<>();
		for (Map.Entry<String, String> entrada : permissaoService.permissoesEfetivasComOrigem(idUsuario).entrySet()) {
			PermissaoEfetivaResponse p = new PermissaoEfetivaResponse();
			p.setCodigo(entrada.getKey());
			p.setOrigem(entrada.getValue());
			efetivas.add(p);
		}
		r.setPermissoesEfetivas(efetivas);
		return r;
	}

	@Transactional
	public UsuarioAcessoResponse salvar(int idUsuario, UsuarioAcessoRequest request, String loginResponsavel) throws BusinessRuleException {
		Usuario usuario = buscarUsuario(idUsuario);

		if (usuario.getLogin().equals(loginResponsavel)) {
			throw new BusinessRuleException("Você não pode alterar o seu próprio acesso. Peça a outro administrador.");
		}

		// --- valida os grupos informados
		Set<Integer> novosIdsGrupos = new LinkedHashSet<>();
		boolean seraAdministrador = false;
		if (request.getIdsGrupos() != null) {
			for (Integer idGrupo : request.getIdsGrupos()) {
				Grupo grupo = (idGrupo == null) ? null : grupoRepository.findById(idGrupo.intValue());
				if (grupo == null) {
					throw new BusinessRuleException("Grupo de ID " + idGrupo + " não cadastrado.");
				}
				novosIdsGrupos.add(grupo.getId());
				seraAdministrador = seraAdministrador || (grupo.isAtivo() && grupo.isAdministrador());
			}
		}

		// --- valida as exceções informadas (código -> efeito)
		Map<String, EfeitoPermissao> novasExcecoes = new HashMap<>();
		if (request.getExcecoes() != null) {
			for (ExcecaoPermissaoDto e : request.getExcecoes()) {
				if ((e.getCodigo() == null) || !Permissoes.existe(e.getCodigo())) {
					throw new BusinessRuleException("Permissão desconhecida: " + e.getCodigo() + ".");
				}
				EfeitoPermissao efeito;
				try {
					efeito = EfeitoPermissao.valueOf(String.valueOf(e.getEfeito()));
				} catch (IllegalArgumentException ex) {
					throw new BusinessRuleException("Efeito inválido para a permissão " + e.getCodigo() + " (use PERMITIR ou NEGAR).");
				}
				novasExcecoes.put(e.getCodigo(), efeito);
			}
		}

		// --- nunca deixar o sistema sem administrador
		if (permissaoService.ehAdministradorAtivo(idUsuario) && !seraAdministrador
				&& (permissaoService.contarAdministradoresAtivos(idUsuario, null) == 0)) {
			throw new BusinessRuleException("Não é possível tirar o acesso de administrador deste usuário: o sistema ficaria sem nenhum administrador ativo.");
		}

		// --- grupos: grava a diferença
		List<UsuarioGrupo> gruposAtuais = usuarioGrupoRepository.findByUsuario_Id(idUsuario);
		Set<Integer> idsAntigos = new TreeSet<>();
		List<UsuarioGrupo> gruposRemover = new ArrayList<>();
		for (UsuarioGrupo ug : gruposAtuais) {
			idsAntigos.add(ug.getGrupo().getId());
			if (!novosIdsGrupos.contains(ug.getGrupo().getId())) {
				gruposRemover.add(ug);
			}
		}
		usuarioGrupoRepository.deleteAll(gruposRemover);
		List<String> gruposAdicionados = new ArrayList<>();
		for (Integer idGrupo : novosIdsGrupos) {
			if (!idsAntigos.contains(idGrupo)) {
				Grupo grupo = grupoRepository.findById(idGrupo.intValue());
				UsuarioGrupo ug = new UsuarioGrupo();
				ug.setUsuario(usuario);
				ug.setGrupo(grupo);
				usuarioGrupoRepository.save(ug);
				gruposAdicionados.add(grupo.getNome());
			}
		}
		List<String> gruposRemovidos = new ArrayList<>();
		for (UsuarioGrupo ug : gruposRemover) {
			gruposRemovidos.add(ug.getGrupo().getNome());
		}

		// --- exceções: grava a diferença
		List<UsuarioPermissao> excecoesAtuais = usuarioPermissaoRepository.findByUsuario_Id(idUsuario);
		Map<String, EfeitoPermissao> antigas = new HashMap<>();
		List<UsuarioPermissao> excecoesRemover = new ArrayList<>();
		for (UsuarioPermissao up : excecoesAtuais) {
			String codigo = up.getPermissao().getCodigo();
			antigas.put(codigo, up.getEfeito());
			EfeitoPermissao novo = novasExcecoes.get(codigo);
			if (novo == null) {
				excecoesRemover.add(up);
			} else if (novo != up.getEfeito()) {
				up.setEfeito(novo);
				usuarioPermissaoRepository.save(up);
			}
		}
		usuarioPermissaoRepository.deleteAll(excecoesRemover);
		for (Map.Entry<String, EfeitoPermissao> entrada : novasExcecoes.entrySet()) {
			if (!antigas.containsKey(entrada.getKey())) {
				UsuarioPermissao up = new UsuarioPermissao();
				up.setUsuario(usuario);
				up.setPermissao(permissaoRepository.findByCodigo(entrada.getKey()));
				up.setEfeito(entrada.getValue());
				usuarioPermissaoRepository.save(up);
			}
		}

		List<String> mudancasExcecoes = new ArrayList<>();
		for (Map.Entry<String, EfeitoPermissao> entrada : new TreeMap<>(novasExcecoes).entrySet()) {
			if (antigas.get(entrada.getKey()) != entrada.getValue()) {
				mudancasExcecoes.add(entrada.getKey() + "=" + entrada.getValue());
			}
		}
		for (String codigo : new TreeSet<>(antigas.keySet())) {
			if (!novasExcecoes.containsKey(codigo)) {
				mudancasExcecoes.add(codigo + " (exceção removida)");
			}
		}

		if (!gruposAdicionados.isEmpty() || !gruposRemovidos.isEmpty() || !mudancasExcecoes.isEmpty()) {
			StringBuilder descricao = new StringBuilder("Acesso do usuário '").append(usuario.getLogin()).append("' alterado");
			if (!gruposAdicionados.isEmpty()) {
				descricao.append("; grupos adicionados: ").append(gruposAdicionados);
			}
			if (!gruposRemovidos.isEmpty()) {
				descricao.append("; grupos removidos: ").append(gruposRemovidos);
			}
			if (!mudancasExcecoes.isEmpty()) {
				descricao.append("; exceções: ").append(mudancasExcecoes);
			}
			auditoriaService.registrar(loginResponsavel, "ALTERAR_ACESSO", "USUARIO", usuario.getId(), descricao.toString());
		}
		permissaoService.limparCache();
		return obter(idUsuario);
	}

	/** Remove vínculos de acesso de um usuário que será excluído. */
	@Transactional
	public void removerVinculos(int idUsuario) {
		usuarioGrupoRepository.deleteAll(usuarioGrupoRepository.findByUsuario_Id(idUsuario));
		usuarioPermissaoRepository.deleteAll(usuarioPermissaoRepository.findByUsuario_Id(idUsuario));
		permissaoService.limparCache();
	}

	private Usuario buscarUsuario(int idUsuario) throws BusinessRuleException {
		Usuario usuario = usuarioRepository.findById(idUsuario);
		if (usuario == null) {
			throw new BusinessRuleException("Usuário de ID " + idUsuario + " não cadastrado.");
		}
		return usuario;
	}

}
