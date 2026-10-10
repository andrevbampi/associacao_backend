package com.projeto.associacao.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projeto.associacao.dto.acesso.GrupoRequest;
import com.projeto.associacao.dto.acesso.GrupoResponse;
import com.projeto.associacao.dto.acesso.PermissaoResponse;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.Grupo;
import com.projeto.associacao.model.GrupoPermissao;
import com.projeto.associacao.model.Permissao;
import com.projeto.associacao.repository.GrupoPermissaoRepository;
import com.projeto.associacao.repository.GrupoRepository;
import com.projeto.associacao.repository.PermissaoRepository;
import com.projeto.associacao.repository.UsuarioGrupoRepository;
import com.projeto.associacao.security.Permissoes;

@Service
public class GrupoService {

	@Autowired
	private GrupoRepository repository;

	@Autowired
	private PermissaoRepository permissaoRepository;

	@Autowired
	private GrupoPermissaoRepository grupoPermissaoRepository;

	@Autowired
	private UsuarioGrupoRepository usuarioGrupoRepository;

	@Autowired
	private PermissaoService permissaoService;

	@Autowired
	private AuditoriaAcessoService auditoriaService;

	public Iterable<GrupoResponse> selecionar() {
		List<GrupoResponse> respostas = new ArrayList<>();
		for (Grupo grupo : repository.findAll()) {
			respostas.add(converterParaResponse(grupo));
		}
		respostas.sort(Comparator.comparing(GrupoResponse::getNome, String.CASE_INSENSITIVE_ORDER));
		return respostas;
	}

	public GrupoResponse buscarPorId(int id) throws BusinessRuleException {
		Grupo grupo = repository.findById(id);
		if (grupo == null) {
			throw new BusinessRuleException("Grupo de ID " + id + " não cadastrado.");
		}
		return converterParaResponse(grupo);
	}

	/** Catálogo de permissões disponíveis (para montar a matriz na tela de grupos). */
	public List<PermissaoResponse> listarPermissoes() {
		List<PermissaoResponse> respostas = new ArrayList<>();
		for (Permissoes.Item item : Permissoes.CATALOGO) {
			PermissaoResponse r = new PermissaoResponse();
			r.setCodigo(item.codigo());
			r.setModulo(item.modulo());
			r.setDescricao(item.descricao());
			respostas.add(r);
		}
		return respostas;
	}

	@Transactional
	public GrupoResponse cadastrar(GrupoRequest request, String loginResponsavel) throws BusinessRuleException {
		request.setId(0);
		Set<String> codigos = validar(request);

		Grupo grupo = new Grupo();
		aplicarDados(grupo, request);
		grupo = repository.save(grupo);
		gravarPermissoes(grupo, codigos);

		auditoriaService.registrar(loginResponsavel, "CRIAR", "GRUPO", grupo.getId(),
				"Grupo '" + grupo.getNome() + "' criado" + descreverFlags(grupo) + " com " + codigos.size() + " permissão(ões): " + codigos);
		permissaoService.limparCache();
		return converterParaResponse(grupo);
	}

	@Transactional
	public GrupoResponse alterar(GrupoRequest request, String loginResponsavel) throws BusinessRuleException {
		if (request.getId() == 0) {
			throw new BusinessRuleException("ID não informado.");
		}
		Grupo grupo = repository.findById(request.getId());
		if (grupo == null) {
			throw new BusinessRuleException("Grupo de ID " + request.getId() + " não cadastrado.");
		}
		Set<String> novos = validar(request);

		boolean eraAdministradorAtivo = grupo.isAtivo() && grupo.isAdministrador();
		boolean seraAdministradorAtivo = request.isAtivo() && request.isAdministrador();
		if (eraAdministradorAtivo && !seraAdministradorAtivo && (permissaoService.contarAdministradoresAtivos(null, grupo.getId()) == 0)) {
			throw new BusinessRuleException("Não é possível remover o acesso de administrador deste grupo: o sistema ficaria sem nenhum administrador ativo.");
		}

		Set<String> antigos = codigosDoGrupo(grupo.getId());
		String nomeAntigo = grupo.getNome();
		String flagsAntigas = descreverFlags(grupo);

		aplicarDados(grupo, request);
		grupo = repository.save(grupo);
		gravarPermissoes(grupo, novos);

		Set<String> adicionadas = new TreeSet<>(novos);
		adicionadas.removeAll(antigos);
		Set<String> removidas = new TreeSet<>(antigos);
		removidas.removeAll(novos);

		StringBuilder descricao = new StringBuilder("Grupo '").append(nomeAntigo).append("' alterado");
		if (!nomeAntigo.equals(grupo.getNome())) {
			descricao.append("; novo nome: '").append(grupo.getNome()).append("'");
		}
		if (!flagsAntigas.equals(descreverFlags(grupo))) {
			String flagsNovas = descreverFlags(grupo);
			descricao.append("; situação:").append(flagsNovas.isEmpty() ? " comum" : flagsNovas);
		}
		if (!adicionadas.isEmpty()) {
			descricao.append("; permissões adicionadas: ").append(adicionadas);
		}
		if (!removidas.isEmpty()) {
			descricao.append("; permissões removidas: ").append(removidas);
		}
		auditoriaService.registrar(loginResponsavel, "ALTERAR", "GRUPO", grupo.getId(), descricao.toString());
		permissaoService.limparCache();
		return converterParaResponse(grupo);
	}

	@Transactional
	public void remover(int id, String loginResponsavel) throws BusinessRuleException {
		Grupo grupo = repository.findById(id);
		if (grupo == null) {
			throw new BusinessRuleException("Grupo de ID " + id + " não cadastrado.");
		}
		if (usuarioGrupoRepository.existsByGrupo_Id(id)) {
			throw new BusinessRuleException("Não é possível excluir o grupo '" + grupo.getNome() + "': há usuários vinculados a ele.");
		}
		grupoPermissaoRepository.deleteAll(grupoPermissaoRepository.findByGrupo_Id(id));
		repository.deleteById(id);
		auditoriaService.registrar(loginResponsavel, "EXCLUIR", "GRUPO", id, "Grupo '" + grupo.getNome() + "' excluído.");
		permissaoService.limparCache();
	}

	private Set<String> validar(GrupoRequest request) throws BusinessRuleException {
		if ((request.getNome() == null) || request.getNome().isBlank()) {
			throw new BusinessRuleException("Nome do grupo não informado.");
		}
		Grupo existente = repository.findByNomeIgnoreCase(request.getNome().trim());
		if ((existente != null) && (existente.getId() != request.getId())) {
			throw new BusinessRuleException("Já existe um grupo com o nome " + request.getNome().trim() + ".");
		}
		Set<String> codigos = new TreeSet<>();
		if (request.getPermissoes() != null) {
			for (String codigo : request.getPermissoes()) {
				if (!Permissoes.existe(codigo)) {
					throw new BusinessRuleException("Permissão desconhecida: " + codigo + ".");
				}
				codigos.add(codigo);
			}
		}
		return codigos;
	}

	private void aplicarDados(Grupo grupo, GrupoRequest request) {
		grupo.setNome(request.getNome().trim());
		grupo.setDescricao((request.getDescricao() == null || request.getDescricao().isBlank()) ? null : request.getDescricao().trim());
		grupo.setAtivo(request.isAtivo());
		grupo.setAdministrador(request.isAdministrador());
	}

	private String descreverFlags(Grupo grupo) {
		return (grupo.isAdministrador() ? " [administrador]" : "") + (grupo.isAtivo() ? "" : " [inativo]");
	}

	private Set<String> codigosDoGrupo(int idGrupo) {
		Set<String> codigos = new TreeSet<>();
		for (GrupoPermissao gp : grupoPermissaoRepository.findByGrupo_Id(idGrupo)) {
			codigos.add(gp.getPermissao().getCodigo());
		}
		return codigos;
	}

	/** Grava só a diferença (evita apagar e reinserir tudo). */
	private void gravarPermissoes(Grupo grupo, Set<String> novos) {
		List<GrupoPermissao> atuais = grupoPermissaoRepository.findByGrupo_Id(grupo.getId());
		Set<String> existentes = new TreeSet<>();
		List<GrupoPermissao> remover = new ArrayList<>();
		for (GrupoPermissao gp : atuais) {
			existentes.add(gp.getPermissao().getCodigo());
			if (!novos.contains(gp.getPermissao().getCodigo())) {
				remover.add(gp);
			}
		}
		grupoPermissaoRepository.deleteAll(remover);
		for (String codigo : novos) {
			if (existentes.contains(codigo)) {
				continue;
			}
			Permissao permissao = permissaoRepository.findByCodigo(codigo);
			GrupoPermissao gp = new GrupoPermissao();
			gp.setGrupo(grupo);
			gp.setPermissao(permissao);
			grupoPermissaoRepository.save(gp);
		}
	}

	private GrupoResponse converterParaResponse(Grupo grupo) {
		GrupoResponse r = new GrupoResponse();
		r.setId(grupo.getId());
		r.setNome(grupo.getNome());
		r.setDescricao(grupo.getDescricao());
		r.setAtivo(grupo.isAtivo());
		r.setAdministrador(grupo.isAdministrador());
		r.setPermissoes(new ArrayList<>(codigosDoGrupo(grupo.getId())));
		r.setTotalUsuarios(usuarioGrupoRepository.findByGrupo_Id(grupo.getId()).size());
		return r;
	}

}
