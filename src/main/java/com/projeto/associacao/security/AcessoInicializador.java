package com.projeto.associacao.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.projeto.associacao.model.Grupo;
import com.projeto.associacao.model.GrupoPermissao;
import com.projeto.associacao.model.Permissao;
import com.projeto.associacao.model.Usuario;
import com.projeto.associacao.model.UsuarioGrupo;
import com.projeto.associacao.repository.GrupoPermissaoRepository;
import com.projeto.associacao.repository.GrupoRepository;
import com.projeto.associacao.repository.PermissaoRepository;
import com.projeto.associacao.repository.UsuarioGrupoRepository;
import com.projeto.associacao.repository.UsuarioRepository;
import com.projeto.associacao.service.PermissaoService;

/**
 * Na inicialização:
 * 1. sincroniza a tabela "permissao" com o catálogo do código ({@link Permissoes});
 * 2. se ainda não existe nenhum grupo (primeira vez), cria os grupos padrão e
 *    coloca todos os usuários já cadastrados no grupo Administrador — assim
 *    ninguém perde acesso ao ativar o controle de permissões.
 *
 * As tabelas precisam existir (ver src/main/resources/db/schema.sql).
 */
@Component
public class AcessoInicializador implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(AcessoInicializador.class);

	@Autowired
	private PermissaoRepository permissaoRepository;

	@Autowired
	private GrupoRepository grupoRepository;

	@Autowired
	private GrupoPermissaoRepository grupoPermissaoRepository;

	@Autowired
	private UsuarioGrupoRepository usuarioGrupoRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PermissaoService permissaoService;

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		Map<String, Permissao> permissoes = sincronizarCatalogo();

		if (grupoRepository.count() == 0) {
			criarGruposPadrao(permissoes);
		}
		permissaoService.limparCache();
	}

	private Map<String, Permissao> sincronizarCatalogo() {
		Map<String, Permissao> porCodigo = new HashMap<>();
		for (Permissoes.Item item : Permissoes.CATALOGO) {
			Permissao permissao = permissaoRepository.findByCodigo(item.codigo());
			if (permissao == null) {
				permissao = new Permissao();
				permissao.setCodigo(item.codigo());
			}
			if (!item.modulo().equals(permissao.getModulo()) || !item.descricao().equals(permissao.getDescricao())) {
				permissao.setModulo(item.modulo());
				permissao.setDescricao(item.descricao());
				permissao = permissaoRepository.save(permissao);
			}
			porCodigo.put(item.codigo(), permissao);
		}
		return porCodigo;
	}

	private void criarGruposPadrao(Map<String, Permissao> permissoes) {
		Grupo administrador = criarGrupo("Administrador", "Acesso total ao sistema, inclusive usuários, grupos e parâmetros.", true, Set.of(), permissoes);

		criarGrupo("Diretoria", "Visualiza tudo (exceto acessos), gerencia membros e atas e acessa todos os relatórios.", false,
				codigos(c -> c.endsWith(":visualizar") && !c.startsWith("grupo:") && !c.startsWith("auditoria:")
						|| c.startsWith("relatorio:")
						|| c.equals("financeiro:resumo")
						|| c.startsWith("membro:")
						|| c.equals("historico-membro:gerenciar")
						|| c.equals("ata:criar") || c.equals("ata:editar") || c.equals("ata:documento-gerenciar")
						|| c.equals("pessoa:documento-visualizar") || c.equals("ata:documento-visualizar")),
				permissoes);

		criarGrupo("Financeiro", "Lançamentos financeiros, caixas, categorias financeiras, relatórios e recebimento de comandas.", false,
				codigos(c -> c.startsWith("financeiro:") || c.startsWith("caixa:") || c.startsWith("categoria-financeira:")
						|| c.startsWith("relatorio:")
						|| c.equals("comanda:visualizar") || c.equals("comanda:receber-pagamento") || c.equals("comanda:desfazer-pagamento")
						|| c.equals("pessoa:visualizar") || c.equals("membro:visualizar")),
				permissoes);

		criarGrupo("Operador de bar/comanda", "Abre comandas, lança itens, fecha e recebe pagamentos. Sem acesso ao financeiro.", false,
				codigos(c -> c.equals("comanda:visualizar") || c.equals("comanda:abrir") || c.equals("comanda:lancar-item")
						|| c.equals("comanda:fechar") || c.equals("comanda:receber-pagamento")
						|| c.equals("produto:visualizar") || c.equals("pessoa:visualizar") || c.equals("estoque:visualizar")),
				permissoes);

		criarGrupo("Estoquista", "Estoque, produtos e categorias de produto.", false,
				codigos(c -> c.startsWith("estoque:") || c.startsWith("produto:") || c.startsWith("categoria-produto:")),
				permissoes);

		criarGrupo("Secretaria", "Pessoas, membros, histórico, atas e seus documentos.", false,
				codigos(c -> c.startsWith("pessoa:") || c.startsWith("membro:") || c.startsWith("historico-membro:")
						|| c.startsWith("status-membro:") || c.startsWith("tipo-evento:") || c.startsWith("ata:")),
				permissoes);

		criarGrupo("Consulta", "Somente leitura e relatórios; não altera nada.", false,
				codigos(c -> (c.endsWith(":visualizar") && !c.startsWith("grupo:") && !c.startsWith("auditoria:") && !c.startsWith("usuario:"))
						|| c.equals("relatorio:consumo-produtos") || c.equals("relatorio:livro-caixa") || c.equals("relatorio:imprimir")
						|| c.equals("financeiro:resumo")),
				permissoes);

		List<Usuario> usuarios = (List<Usuario>) usuarioRepository.findAll();
		for (Usuario usuario : usuarios) {
			UsuarioGrupo vinculo = new UsuarioGrupo();
			vinculo.setUsuario(usuario);
			vinculo.setGrupo(administrador);
			usuarioGrupoRepository.save(vinculo);
		}
		log.info("Controle de acesso inicializado: grupos padrão criados e {} usuário(s) existente(s) incluído(s) no grupo Administrador.", usuarios.size());
	}

	private Set<String> codigos(Predicate<String> filtro) {
		Set<String> escolhidos = new TreeSet<>();
		for (Permissoes.Item item : Permissoes.CATALOGO) {
			if (filtro.test(item.codigo())) {
				escolhidos.add(item.codigo());
			}
		}
		return escolhidos;
	}

	private Grupo criarGrupo(String nome, String descricao, boolean administrador, Set<String> codigos, Map<String, Permissao> permissoes) {
		Grupo grupo = new Grupo();
		grupo.setNome(nome);
		grupo.setDescricao(descricao);
		grupo.setAtivo(true);
		grupo.setAdministrador(administrador);
		grupo = grupoRepository.save(grupo);
		for (String codigo : codigos) {
			GrupoPermissao gp = new GrupoPermissao();
			gp.setGrupo(grupo);
			gp.setPermissao(permissoes.get(codigo));
			grupoPermissaoRepository.save(gp);
		}
		return grupo;
	}

}
