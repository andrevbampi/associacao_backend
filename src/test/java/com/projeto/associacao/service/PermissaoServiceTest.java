package com.projeto.associacao.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projeto.associacao.model.EfeitoPermissao;
import com.projeto.associacao.model.Grupo;
import com.projeto.associacao.model.GrupoPermissao;
import com.projeto.associacao.model.Permissao;
import com.projeto.associacao.model.Usuario;
import com.projeto.associacao.model.UsuarioGrupo;
import com.projeto.associacao.model.UsuarioPermissao;
import com.projeto.associacao.repository.GrupoPermissaoRepository;
import com.projeto.associacao.repository.UsuarioGrupoRepository;
import com.projeto.associacao.repository.UsuarioPermissaoRepository;
import com.projeto.associacao.repository.UsuarioRepository;
import com.projeto.associacao.security.Permissoes;

@ExtendWith(MockitoExtension.class)
class PermissaoServiceTest {

	@Mock
	private UsuarioRepository usuarioRepository;
	@Mock
	private UsuarioGrupoRepository usuarioGrupoRepository;
	@Mock
	private GrupoPermissaoRepository grupoPermissaoRepository;
	@Mock
	private UsuarioPermissaoRepository usuarioPermissaoRepository;

	@InjectMocks
	private PermissaoService service;

	// ---------- regras de prioridade (função pura) ----------

	@Test
	void usuarioSemGrupoENemExcecaoNaoTemNada() {
		assertTrue(PermissaoService.calcular(false, Set.of(), Set.of(), Set.of()).isEmpty());
	}

	@Test
	void usuarioSemGrupoUsaApenasExcecoesPermitidas() {
		assertEquals(Set.of("relatorio:livro-caixa"), PermissaoService.calcular(false, Set.of(), Set.of("relatorio:livro-caixa"), Set.of()));
	}

	@Test
	void multiplosGruposSomam() {
		Set<String> r = PermissaoService.calcular(false, Set.of("comanda:abrir", "financeiro:visualizar"), Set.of(), Set.of());
		assertEquals(Set.of("comanda:abrir", "financeiro:visualizar"), r);
	}

	@Test
	void excecaoPermitirComplementaOGrupo() {
		Set<String> r = PermissaoService.calcular(false, Set.of("comanda:abrir"), Set.of("comanda:fechar"), Set.of());
		assertEquals(Set.of("comanda:abrir", "comanda:fechar"), r);
	}

	@Test
	void negacaoDoUsuarioVenceOGrupo() {
		Set<String> r = PermissaoService.calcular(false, Set.of("comanda:abrir", "comanda:fechar"), Set.of(), Set.of("comanda:fechar"));
		assertEquals(Set.of("comanda:abrir"), r);
	}

	@Test
	void negacaoVenceAteQuandoOUsuarioTambemTemPermitir() {
		Set<String> r = PermissaoService.calcular(false, Set.of(), Set.of("comanda:fechar"), Set.of("comanda:fechar"));
		assertTrue(r.isEmpty());
	}

	@Test
	void administradorTemTodoOCatalogoMasAindaPodeSerRestringido() {
		Set<String> todos = PermissaoService.calcular(true, Set.of(), Set.of(), Set.of());
		assertEquals(Permissoes.CATALOGO.size(), todos.size());

		Set<String> restrito = PermissaoService.calcular(true, Set.of(), Set.of(), Set.of("parametro:alterar"));
		assertFalse(restrito.contains("parametro:alterar"));
		assertEquals(Permissoes.CATALOGO.size() - 1, restrito.size());
	}

	// ---------- carregamento a partir do banco ----------

	@Test
	void usuarioInativoOuInexistenteNaoEhAutenticado() {
		when(usuarioRepository.findByLogin("fantasma")).thenReturn(null);
		Usuario inativo = usuario(2, "inativo", false);
		when(usuarioRepository.findByLogin("inativo")).thenReturn(inativo);

		assertNull(service.permissoesDoLogin("fantasma"));
		assertNull(service.permissoesDoLogin("inativo"));
	}

	@Test
	void grupoInativoNaoConcedeNada() {
		Usuario u = usuario(1, "ana", true);
		when(usuarioRepository.findByLogin("ana")).thenReturn(u);
		Grupo inativo = grupo(10, "Antigo", false, false);
		when(usuarioGrupoRepository.findByUsuario_Id(1)).thenReturn(List.of(vinculo(u, inativo)));
		when(usuarioPermissaoRepository.findByUsuario_Id(1)).thenReturn(List.of());

		assertTrue(service.permissoesDoLogin("ana").isEmpty());
	}

	@Test
	void combinaGrupoEExcecoesDoBanco() {
		Usuario u = usuario(1, "bia", true);
		when(usuarioRepository.findByLogin("bia")).thenReturn(u);
		Grupo bar = grupo(10, "Bar", true, false);
		when(usuarioGrupoRepository.findByUsuario_Id(1)).thenReturn(List.of(vinculo(u, bar)));
		when(grupoPermissaoRepository.findByGrupo_Id(10)).thenReturn(List.of(gp(bar, "comanda:abrir"), gp(bar, "comanda:fechar")));
		when(usuarioPermissaoRepository.findByUsuario_Id(1)).thenReturn(List.of(
				excecao(u, "comanda:fechar", EfeitoPermissao.NEGAR),
				excecao(u, "estoque:visualizar", EfeitoPermissao.PERMITIR)));

		assertEquals(Set.of("comanda:abrir", "estoque:visualizar"), service.permissoesDoLogin("bia"));
	}

	@Test
	void resultadoFicaEmCacheAteLimpar() {
		Usuario u = usuario(1, "cris", true);
		when(usuarioRepository.findByLogin("cris")).thenReturn(u);
		when(usuarioGrupoRepository.findByUsuario_Id(1)).thenReturn(List.of());
		when(usuarioPermissaoRepository.findByUsuario_Id(1)).thenReturn(List.of());

		service.permissoesDoLogin("cris");
		service.permissoesDoLogin("cris");
		verify(usuarioRepository, times(1)).findByLogin("cris");

		service.limparCache();
		service.permissoesDoLogin("cris");
		verify(usuarioRepository, times(2)).findByLogin("cris");
	}

	@Test
	void contaAdministradoresAtivosIgnorandoUsuarioOuGrupo() {
		Usuario a = usuario(1, "a", true);
		Usuario b = usuario(2, "b", true);
		Usuario inativo = usuario(3, "c", false);
		Grupo admin = grupo(10, "Admin", true, true);
		Grupo comum = grupo(11, "Comum", true, false);
		when(usuarioGrupoRepository.findAll()).thenReturn(List.of(vinculo(a, admin), vinculo(b, admin), vinculo(inativo, admin), vinculo(a, comum)));

		assertEquals(2, service.contarAdministradoresAtivos(null, null));
		assertEquals(1, service.contarAdministradoresAtivos(1, null));
		assertEquals(0, service.contarAdministradoresAtivos(null, 10));
	}

	// ---------- helpers ----------

	private Usuario usuario(int id, String login, boolean ativo) {
		Usuario u = new Usuario();
		u.setId(id);
		u.setLogin(login);
		u.setAtivo(ativo);
		return u;
	}

	private Grupo grupo(int id, String nome, boolean ativo, boolean administrador) {
		Grupo g = new Grupo();
		g.setId(id);
		g.setNome(nome);
		g.setAtivo(ativo);
		g.setAdministrador(administrador);
		return g;
	}

	private UsuarioGrupo vinculo(Usuario u, Grupo g) {
		UsuarioGrupo ug = new UsuarioGrupo();
		ug.setUsuario(u);
		ug.setGrupo(g);
		return ug;
	}

	private GrupoPermissao gp(Grupo g, String codigo) {
		GrupoPermissao gp = new GrupoPermissao();
		gp.setGrupo(g);
		gp.setPermissao(permissao(codigo));
		return gp;
	}

	private UsuarioPermissao excecao(Usuario u, String codigo, EfeitoPermissao efeito) {
		UsuarioPermissao up = new UsuarioPermissao();
		up.setUsuario(u);
		up.setPermissao(permissao(codigo));
		up.setEfeito(efeito);
		return up;
	}

	private Permissao permissao(String codigo) {
		Permissao p = new Permissao();
		p.setCodigo(codigo);
		return p;
	}
}
