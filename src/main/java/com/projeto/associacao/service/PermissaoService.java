package com.projeto.associacao.service;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projeto.associacao.model.EfeitoPermissao;
import com.projeto.associacao.model.Grupo;
import com.projeto.associacao.model.GrupoPermissao;
import com.projeto.associacao.model.Usuario;
import com.projeto.associacao.model.UsuarioGrupo;
import com.projeto.associacao.model.UsuarioPermissao;
import com.projeto.associacao.repository.GrupoPermissaoRepository;
import com.projeto.associacao.repository.UsuarioGrupoRepository;
import com.projeto.associacao.repository.UsuarioPermissaoRepository;
import com.projeto.associacao.repository.UsuarioRepository;
import com.projeto.associacao.security.Permissoes;

/**
 * Calcula as permissões efetivas de um usuário.
 *
 * Regra (ver {@link #calcular}):
 *   efetivas = (união das permissões dos grupos ativos [ou TODAS, se algum grupo
 *              ativo for administrador] + exceções PERMITIR do usuário)
 *              - exceções NEGAR do usuário
 * A negação do usuário sempre vence. Sem grupo e sem exceção: nenhuma permissão.
 *
 * O resultado fica em um cache curto por login (limpo sempre que grupos,
 * permissões ou usuários mudam), para não consultar o banco várias vezes por
 * requisição sem deixar uma alteração de acesso "presa" por muito tempo.
 */
@Service
public class PermissaoService {

	private static final long TTL_CACHE_MS = 30_000;

	private record Entrada(Set<String> permissoes, long expiraEm) {
	}

	private final Map<String, Entrada> cache = new ConcurrentHashMap<>();

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private UsuarioGrupoRepository usuarioGrupoRepository;

	@Autowired
	private GrupoPermissaoRepository grupoPermissaoRepository;

	@Autowired
	private UsuarioPermissaoRepository usuarioPermissaoRepository;

	/** Função pura (sem banco): aplica as regras de prioridade entre grupos e exceções. */
	public static Set<String> calcular(boolean administrador, Set<String> deGrupos, Set<String> permitidas, Set<String> negadas) {
		Set<String> efetivas = new TreeSet<>();
		if (administrador) {
			Permissoes.CATALOGO.forEach(item -> efetivas.add(item.codigo()));
		} else {
			efetivas.addAll(deGrupos);
		}
		efetivas.addAll(permitidas);
		efetivas.removeAll(negadas);
		return efetivas;
	}

	/**
	 * Permissões efetivas do usuário com esse login, ou null se o usuário não
	 * existe ou está inativo (nesse caso a requisição não deve ser autenticada).
	 */
	public Set<String> permissoesDoLogin(String login) {
		long agora = System.currentTimeMillis();
		Entrada entrada = cache.get(login);
		if ((entrada != null) && (entrada.expiraEm() > agora)) {
			return entrada.permissoes();
		}

		Set<String> permissoes = carregar(login);
		if (permissoes != null) {
			cache.put(login, new Entrada(permissoes, agora + TTL_CACHE_MS));
		} else {
			cache.remove(login);
		}
		return permissoes;
	}

	public void limparCache() {
		cache.clear();
	}

	@Transactional(readOnly = true)
	public Set<String> carregar(String login) {
		Usuario usuario = usuarioRepository.findByLogin(login);
		if ((usuario == null) || !usuario.isAtivo()) {
			return null;
		}
		return permissoesEfetivas(usuario.getId());
	}

	@Transactional(readOnly = true)
	public Set<String> permissoesEfetivas(int idUsuario) {
		Set<String> deGrupos = new HashSet<>();
		boolean administrador = false;
		for (UsuarioGrupo ug : usuarioGrupoRepository.findByUsuario_Id(idUsuario)) {
			Grupo grupo = ug.getGrupo();
			if (!grupo.isAtivo()) {
				continue;
			}
			administrador = administrador || grupo.isAdministrador();
			for (GrupoPermissao gp : grupoPermissaoRepository.findByGrupo_Id(grupo.getId())) {
				deGrupos.add(gp.getPermissao().getCodigo());
			}
		}

		Set<String> permitidas = new HashSet<>();
		Set<String> negadas = new HashSet<>();
		for (UsuarioPermissao up : usuarioPermissaoRepository.findByUsuario_Id(idUsuario)) {
			(up.getEfeito() == EfeitoPermissao.NEGAR ? negadas : permitidas).add(up.getPermissao().getCodigo());
		}
		return calcular(administrador, deGrupos, permitidas, negadas);
	}

	/**
	 * Para a tela de acesso do usuário: cada permissão efetiva com a explicação
	 * de onde ela vem (código -> origem).
	 */
	@Transactional(readOnly = true)
	public Map<String, String> permissoesEfetivasComOrigem(int idUsuario) {
		Map<String, Set<String>> gruposPorPermissao = new LinkedHashMap<>();
		Set<String> administradores = new TreeSet<>();
		for (UsuarioGrupo ug : usuarioGrupoRepository.findByUsuario_Id(idUsuario)) {
			Grupo grupo = ug.getGrupo();
			if (!grupo.isAtivo()) {
				continue;
			}
			if (grupo.isAdministrador()) {
				administradores.add(grupo.getNome());
			}
			for (GrupoPermissao gp : grupoPermissaoRepository.findByGrupo_Id(grupo.getId())) {
				gruposPorPermissao.computeIfAbsent(gp.getPermissao().getCodigo(), k -> new TreeSet<>()).add(grupo.getNome());
			}
		}

		Set<String> permitidas = new HashSet<>();
		for (UsuarioPermissao up : usuarioPermissaoRepository.findByUsuario_Id(idUsuario)) {
			if (up.getEfeito() == EfeitoPermissao.PERMITIR) {
				permitidas.add(up.getPermissao().getCodigo());
			}
		}

		Map<String, String> origens = new LinkedHashMap<>();
		for (String codigo : permissoesEfetivas(idUsuario)) {
			Set<String> grupos = gruposPorPermissao.get(codigo);
			if ((grupos != null) && !grupos.isEmpty()) {
				origens.put(codigo, "Grupo: " + String.join(", ", grupos));
			} else if (!administradores.isEmpty()) {
				origens.put(codigo, "Administrador: " + String.join(", ", administradores));
			} else if (permitidas.contains(codigo)) {
				origens.put(codigo, "Exceção do usuário (permitida)");
			}
		}
		return origens;
	}

	/**
	 * Quantos usuários ativos continuam administradores se ignorarmos um usuário
	 * e/ou um grupo (usado para nunca deixar o sistema sem administrador).
	 */
	@Transactional(readOnly = true)
	public int contarAdministradoresAtivos(Integer ignorarUsuario, Integer ignorarGrupo) {
		Set<Integer> ids = new HashSet<>();
		for (UsuarioGrupo ug : usuarioGrupoRepository.findAll()) {
			Usuario usuario = ug.getUsuario();
			Grupo grupo = ug.getGrupo();
			if (!usuario.isAtivo() || !grupo.isAtivo() || !grupo.isAdministrador()) {
				continue;
			}
			if ((ignorarUsuario != null) && (usuario.getId() == ignorarUsuario)) {
				continue;
			}
			if ((ignorarGrupo != null) && (grupo.getId() == ignorarGrupo)) {
				continue;
			}
			ids.add(usuario.getId());
		}
		return ids.size();
	}

	@Transactional(readOnly = true)
	public boolean ehAdministradorAtivo(int idUsuario) {
		Usuario usuario = usuarioRepository.findById(idUsuario);
		if ((usuario == null) || !usuario.isAtivo()) {
			return false;
		}
		List<UsuarioGrupo> grupos = usuarioGrupoRepository.findByUsuario_Id(idUsuario);
		for (UsuarioGrupo ug : grupos) {
			if (ug.getGrupo().isAtivo() && ug.getGrupo().isAdministrador()) {
				return true;
			}
		}
		return false;
	}

}
