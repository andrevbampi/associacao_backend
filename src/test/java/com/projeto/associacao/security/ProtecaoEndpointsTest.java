package com.projeto.associacao.security;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * Garante que nenhum endpoint novo "escapa" do controle de permissões e que
 * todo código usado em @PreAuthorize existe no catálogo.
 */
class ProtecaoEndpointsTest {

	private static final String PACOTE = "com.projeto.associacao.controller";

	// Endpoints que de propósito exigem apenas estar autenticado (ou são públicos).
	private static final Set<String> EXCECOES = Set.of(
			"AuthController.login", "AuthController.me",
			"PublicController.config", "PublicController.logo",
			"PessoaController.buscarFoto", "ProdutoController.buscarFoto");

	@Test
	void todoEndpointTemPreAuthorizeOuEstaNaListaDeExcecoes() throws Exception {
		List<String> semProtecao = new ArrayList<>();
		for (Class<?> controller : controllers()) {
			for (Method metodo : controller.getDeclaredMethods()) {
				boolean ehEndpoint = metodo.isAnnotationPresent(GetMapping.class) || metodo.isAnnotationPresent(PostMapping.class)
						|| metodo.isAnnotationPresent(PutMapping.class) || metodo.isAnnotationPresent(DeleteMapping.class);
				if (!ehEndpoint) {
					continue;
				}
				String nome = controller.getSimpleName() + "." + metodo.getName();
				if (!metodo.isAnnotationPresent(PreAuthorize.class) && !EXCECOES.contains(nome)) {
					semProtecao.add(nome);
				}
			}
		}
		assertTrue(semProtecao.isEmpty(), "Endpoints sem @PreAuthorize: " + semProtecao);
	}

	@Test
	void todoCodigoUsadoNosPreAuthorizeExisteNoCatalogo() throws Exception {
		Pattern codigo = Pattern.compile("'([^']+)'");
		List<String> invalidos = new ArrayList<>();
		for (Class<?> controller : controllers()) {
			for (Method metodo : controller.getDeclaredMethods()) {
				PreAuthorize pre = metodo.getAnnotation(PreAuthorize.class);
				if (pre == null) {
					continue;
				}
				Matcher m = codigo.matcher(pre.value());
				while (m.find()) {
					if (!Permissoes.existe(m.group(1))) {
						invalidos.add(controller.getSimpleName() + "." + metodo.getName() + " -> " + m.group(1));
					}
				}
			}
		}
		assertTrue(invalidos.isEmpty(), "Permissões inexistentes no catálogo: " + invalidos);
	}

	@Test
	void catalogoNaoTemCodigosDuplicados() {
		long distintos = Permissoes.CATALOGO.stream().map(Permissoes.Item::codigo).distinct().count();
		assertTrue(distintos == Permissoes.CATALOGO.size());
	}

	private List<Class<?>> controllers() throws Exception {
		List<Class<?>> classes = new ArrayList<>();
		File pasta = new File("target/classes/" + PACOTE.replace('.', '/'));
		for (File arquivo : pasta.listFiles((d, n) -> n.endsWith("Controller.class"))) {
			String nome = arquivo.getName().replace(".class", "");
			classes.add(Class.forName(PACOTE + "." + nome));
		}
		return classes;
	}
}
