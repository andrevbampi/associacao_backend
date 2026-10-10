package com.projeto.associacao.security;

import java.util.ArrayList;
import java.util.List;

/**
 * Catálogo de permissões do sistema. Cada permissão é um código "modulo:acao".
 * As constantes são usadas nos @PreAuthorize dos controllers; a lista
 * {@link #CATALOGO} é sincronizada com a tabela "permissao" na inicialização.
 *
 * Ao criar uma funcionalidade nova: adicione a constante e o item do catálogo
 * aqui, proteja o endpoint e dê a permissão aos grupos adequados (o grupo
 * administrador recebe tudo automaticamente).
 *
 * As constantes LOOKUP_* são expressões para as listagens que alimentam
 * seletores de outras telas: aceitam a permissão "visualizar" do próprio
 * módulo OU a de quem precisa dessa lista para trabalhar.
 */
public final class Permissoes {

	private Permissoes() {
	}

	public record Item(String codigo, String modulo, String descricao) {
	}

	public static final String PESSOA_VISUALIZAR = "pessoa:visualizar";
	public static final String PESSOA_CRIAR = "pessoa:criar";
	public static final String PESSOA_EDITAR = "pessoa:editar";
	public static final String PESSOA_EXCLUIR = "pessoa:excluir";
	public static final String PESSOA_FOTO = "pessoa:foto";
	public static final String PESSOA_DOCUMENTO_VISUALIZAR = "pessoa:documento-visualizar";
	public static final String PESSOA_DOCUMENTO_GERENCIAR = "pessoa:documento-gerenciar";
	public static final String USUARIO_VISUALIZAR = "usuario:visualizar";
	public static final String USUARIO_CRIAR = "usuario:criar";
	public static final String USUARIO_EDITAR = "usuario:editar";
	public static final String USUARIO_EXCLUIR = "usuario:excluir";
	public static final String USUARIO_GERENCIAR_ACESSO = "usuario:gerenciar-acesso";
	public static final String GRUPO_VISUALIZAR = "grupo:visualizar";
	public static final String GRUPO_CRIAR = "grupo:criar";
	public static final String GRUPO_EDITAR = "grupo:editar";
	public static final String GRUPO_EXCLUIR = "grupo:excluir";
	public static final String AUDITORIA_VISUALIZAR = "auditoria:visualizar";
	public static final String MEMBRO_VISUALIZAR = "membro:visualizar";
	public static final String MEMBRO_CRIAR = "membro:criar";
	public static final String MEMBRO_EDITAR = "membro:editar";
	public static final String MEMBRO_EXCLUIR = "membro:excluir";
	public static final String HISTORICO_MEMBRO_VISUALIZAR = "historico-membro:visualizar";
	public static final String HISTORICO_MEMBRO_GERENCIAR = "historico-membro:gerenciar";
	public static final String STATUS_MEMBRO_VISUALIZAR = "status-membro:visualizar";
	public static final String STATUS_MEMBRO_CRIAR = "status-membro:criar";
	public static final String STATUS_MEMBRO_EDITAR = "status-membro:editar";
	public static final String STATUS_MEMBRO_EXCLUIR = "status-membro:excluir";
	public static final String TIPO_EVENTO_VISUALIZAR = "tipo-evento:visualizar";
	public static final String TIPO_EVENTO_CRIAR = "tipo-evento:criar";
	public static final String TIPO_EVENTO_EDITAR = "tipo-evento:editar";
	public static final String TIPO_EVENTO_EXCLUIR = "tipo-evento:excluir";
	public static final String PRODUTO_VISUALIZAR = "produto:visualizar";
	public static final String PRODUTO_CRIAR = "produto:criar";
	public static final String PRODUTO_EDITAR = "produto:editar";
	public static final String PRODUTO_EXCLUIR = "produto:excluir";
	public static final String PRODUTO_FOTO = "produto:foto";
	public static final String CATEGORIA_PRODUTO_VISUALIZAR = "categoria-produto:visualizar";
	public static final String CATEGORIA_PRODUTO_CRIAR = "categoria-produto:criar";
	public static final String CATEGORIA_PRODUTO_EDITAR = "categoria-produto:editar";
	public static final String CATEGORIA_PRODUTO_EXCLUIR = "categoria-produto:excluir";
	public static final String COMANDA_VISUALIZAR = "comanda:visualizar";
	public static final String COMANDA_ABRIR = "comanda:abrir";
	public static final String COMANDA_LANCAR_ITEM = "comanda:lancar-item";
	public static final String COMANDA_FECHAR = "comanda:fechar";
	public static final String COMANDA_RECEBER_PAGAMENTO = "comanda:receber-pagamento";
	public static final String COMANDA_DESFAZER_PAGAMENTO = "comanda:desfazer-pagamento";
	public static final String COMANDA_DESFAZER_FECHAMENTO = "comanda:desfazer-fechamento";
	public static final String COMANDA_CANCELAR = "comanda:cancelar";
	public static final String ESTOQUE_VISUALIZAR = "estoque:visualizar";
	public static final String ESTOQUE_MOVIMENTAR = "estoque:movimentar";
	public static final String FINANCEIRO_VISUALIZAR = "financeiro:visualizar";
	public static final String FINANCEIRO_CRIAR = "financeiro:criar";
	public static final String FINANCEIRO_EDITAR = "financeiro:editar";
	public static final String FINANCEIRO_EXCLUIR = "financeiro:excluir";
	public static final String FINANCEIRO_PAGAR = "financeiro:pagar";
	public static final String FINANCEIRO_RESUMO = "financeiro:resumo";
	public static final String CATEGORIA_FINANCEIRA_VISUALIZAR = "categoria-financeira:visualizar";
	public static final String CATEGORIA_FINANCEIRA_CRIAR = "categoria-financeira:criar";
	public static final String CATEGORIA_FINANCEIRA_EDITAR = "categoria-financeira:editar";
	public static final String CATEGORIA_FINANCEIRA_EXCLUIR = "categoria-financeira:excluir";
	public static final String CAIXA_VISUALIZAR = "caixa:visualizar";
	public static final String CAIXA_CRIAR = "caixa:criar";
	public static final String CAIXA_EDITAR = "caixa:editar";
	public static final String CAIXA_EXCLUIR = "caixa:excluir";
	public static final String ATA_VISUALIZAR = "ata:visualizar";
	public static final String ATA_CRIAR = "ata:criar";
	public static final String ATA_EDITAR = "ata:editar";
	public static final String ATA_EXCLUIR = "ata:excluir";
	public static final String ATA_DOCUMENTO_VISUALIZAR = "ata:documento-visualizar";
	public static final String ATA_DOCUMENTO_GERENCIAR = "ata:documento-gerenciar";
	public static final String RELATORIO_CONSUMO_PRODUTOS = "relatorio:consumo-produtos";
	public static final String RELATORIO_LIVRO_CAIXA = "relatorio:livro-caixa";
	public static final String RELATORIO_IMPRIMIR = "relatorio:imprimir";
	public static final String PARAMETRO_VISUALIZAR = "parametro:visualizar";
	public static final String PARAMETRO_ALTERAR = "parametro:alterar";
	public static final String PARAMETRO_LOGO = "parametro:logo";

	public static final String LOOKUP_PESSOA = "hasAnyAuthority('" + PESSOA_VISUALIZAR + "', '" + USUARIO_CRIAR + "', '" + USUARIO_EDITAR + "', '" + MEMBRO_CRIAR + "', '" + MEMBRO_EDITAR + "', '" + MEMBRO_VISUALIZAR + "', '" + COMANDA_ABRIR + "', '" + COMANDA_VISUALIZAR + "', '" + FINANCEIRO_CRIAR + "', '" + FINANCEIRO_EDITAR + "', '" + ATA_CRIAR + "', '" + ATA_EDITAR + "', '" + RELATORIO_CONSUMO_PRODUTOS + "')";
	public static final String LOOKUP_PRODUTO = "hasAnyAuthority('" + PRODUTO_VISUALIZAR + "', '" + COMANDA_LANCAR_ITEM + "', '" + COMANDA_VISUALIZAR + "', '" + ESTOQUE_VISUALIZAR + "', '" + ESTOQUE_MOVIMENTAR + "', '" + RELATORIO_CONSUMO_PRODUTOS + "')";
	public static final String LOOKUP_CATEGORIA_PRODUTO = "hasAnyAuthority('" + CATEGORIA_PRODUTO_VISUALIZAR + "', '" + PRODUTO_CRIAR + "', '" + PRODUTO_EDITAR + "', '" + PRODUTO_VISUALIZAR + "', '" + RELATORIO_CONSUMO_PRODUTOS + "')";
	public static final String LOOKUP_CAIXA = "hasAnyAuthority('" + CAIXA_VISUALIZAR + "', '" + FINANCEIRO_VISUALIZAR + "', '" + FINANCEIRO_CRIAR + "', '" + FINANCEIRO_EDITAR + "', '" + FINANCEIRO_PAGAR + "', '" + COMANDA_FECHAR + "', '" + COMANDA_RECEBER_PAGAMENTO + "', '" + RELATORIO_LIVRO_CAIXA + "', '" + PARAMETRO_VISUALIZAR + "')";
	public static final String LOOKUP_CATEGORIA_FINANCEIRA = "hasAnyAuthority('" + CATEGORIA_FINANCEIRA_VISUALIZAR + "', '" + FINANCEIRO_VISUALIZAR + "', '" + FINANCEIRO_CRIAR + "', '" + FINANCEIRO_EDITAR + "')";
	public static final String LOOKUP_STATUS_MEMBRO = "hasAnyAuthority('" + STATUS_MEMBRO_VISUALIZAR + "', '" + MEMBRO_VISUALIZAR + "', '" + MEMBRO_CRIAR + "', '" + MEMBRO_EDITAR + "')";
	public static final String LOOKUP_TIPO_EVENTO = "hasAnyAuthority('" + TIPO_EVENTO_VISUALIZAR + "', '" + HISTORICO_MEMBRO_VISUALIZAR + "', '" + HISTORICO_MEMBRO_GERENCIAR + "')";
	public static final String LOOKUP_PARAMETRO = "hasAnyAuthority('" + PARAMETRO_VISUALIZAR + "', '" + COMANDA_FECHAR + "', '" + COMANDA_RECEBER_PAGAMENTO + "', '" + COMANDA_VISUALIZAR + "')";
	public static final String LOOKUP_GRUPO = "hasAnyAuthority('" + GRUPO_VISUALIZAR + "', '" + USUARIO_GERENCIAR_ACESSO + "')";

	public static final List<Item> CATALOGO = montarCatalogo();

	private static List<Item> montarCatalogo() {
		List<Item> itens = new ArrayList<>();
		itens.add(item(PESSOA_VISUALIZAR, "Pessoas", "Visualizar pessoas"));
		itens.add(item(PESSOA_CRIAR, "Pessoas", "Cadastrar pessoas"));
		itens.add(item(PESSOA_EDITAR, "Pessoas", "Alterar pessoas"));
		itens.add(item(PESSOA_EXCLUIR, "Pessoas", "Excluir pessoas"));
		itens.add(item(PESSOA_FOTO, "Pessoas", "Trocar ou remover a foto da pessoa"));
		itens.add(item(PESSOA_DOCUMENTO_VISUALIZAR, "Pessoas", "Visualizar/baixar documentos da pessoa"));
		itens.add(item(PESSOA_DOCUMENTO_GERENCIAR, "Pessoas", "Enviar e excluir documentos da pessoa"));
		itens.add(item(USUARIO_VISUALIZAR, "Usuários", "Visualizar usuários"));
		itens.add(item(USUARIO_CRIAR, "Usuários", "Cadastrar usuários"));
		itens.add(item(USUARIO_EDITAR, "Usuários", "Alterar usuários (inclui redefinir senha)"));
		itens.add(item(USUARIO_EXCLUIR, "Usuários", "Excluir usuários"));
		itens.add(item(USUARIO_GERENCIAR_ACESSO, "Usuários", "Definir grupos e exceções de permissão dos usuários"));
		itens.add(item(GRUPO_VISUALIZAR, "Grupos de acesso", "Visualizar grupos e suas permissões"));
		itens.add(item(GRUPO_CRIAR, "Grupos de acesso", "Criar grupos"));
		itens.add(item(GRUPO_EDITAR, "Grupos de acesso", "Alterar grupos e suas permissões"));
		itens.add(item(GRUPO_EXCLUIR, "Grupos de acesso", "Excluir grupos"));
		itens.add(item(AUDITORIA_VISUALIZAR, "Auditoria", "Consultar a auditoria de alterações de acesso"));
		itens.add(item(MEMBRO_VISUALIZAR, "Membros", "Visualizar membros"));
		itens.add(item(MEMBRO_CRIAR, "Membros", "Cadastrar membros"));
		itens.add(item(MEMBRO_EDITAR, "Membros", "Alterar membros"));
		itens.add(item(MEMBRO_EXCLUIR, "Membros", "Excluir membros"));
		itens.add(item(HISTORICO_MEMBRO_VISUALIZAR, "Histórico de membros", "Visualizar o histórico de membros"));
		itens.add(item(HISTORICO_MEMBRO_GERENCIAR, "Histórico de membros", "Lançar, alterar e excluir eventos do histórico"));
		itens.add(item(STATUS_MEMBRO_VISUALIZAR, "Status de membro", "Visualizar status de membro"));
		itens.add(item(STATUS_MEMBRO_CRIAR, "Status de membro", "Cadastrar status de membro"));
		itens.add(item(STATUS_MEMBRO_EDITAR, "Status de membro", "Alterar status de membro"));
		itens.add(item(STATUS_MEMBRO_EXCLUIR, "Status de membro", "Excluir status de membro"));
		itens.add(item(TIPO_EVENTO_VISUALIZAR, "Tipos de evento", "Visualizar tipos de evento"));
		itens.add(item(TIPO_EVENTO_CRIAR, "Tipos de evento", "Cadastrar tipos de evento"));
		itens.add(item(TIPO_EVENTO_EDITAR, "Tipos de evento", "Alterar tipos de evento"));
		itens.add(item(TIPO_EVENTO_EXCLUIR, "Tipos de evento", "Excluir tipos de evento"));
		itens.add(item(PRODUTO_VISUALIZAR, "Produtos", "Visualizar produtos"));
		itens.add(item(PRODUTO_CRIAR, "Produtos", "Cadastrar produtos"));
		itens.add(item(PRODUTO_EDITAR, "Produtos", "Alterar produtos"));
		itens.add(item(PRODUTO_EXCLUIR, "Produtos", "Excluir produtos"));
		itens.add(item(PRODUTO_FOTO, "Produtos", "Trocar ou remover a foto do produto"));
		itens.add(item(CATEGORIA_PRODUTO_VISUALIZAR, "Categorias de produto", "Visualizar categorias de produto"));
		itens.add(item(CATEGORIA_PRODUTO_CRIAR, "Categorias de produto", "Cadastrar categorias de produto"));
		itens.add(item(CATEGORIA_PRODUTO_EDITAR, "Categorias de produto", "Alterar categorias de produto"));
		itens.add(item(CATEGORIA_PRODUTO_EXCLUIR, "Categorias de produto", "Excluir categorias de produto"));
		itens.add(item(COMANDA_VISUALIZAR, "Comandas", "Visualizar comandas"));
		itens.add(item(COMANDA_ABRIR, "Comandas", "Abrir comandas"));
		itens.add(item(COMANDA_LANCAR_ITEM, "Comandas", "Adicionar, alterar e remover itens da comanda"));
		itens.add(item(COMANDA_FECHAR, "Comandas", "Fechar comandas"));
		itens.add(item(COMANDA_RECEBER_PAGAMENTO, "Comandas", "Registrar o pagamento de comandas"));
		itens.add(item(COMANDA_DESFAZER_PAGAMENTO, "Comandas", "Desfazer o pagamento de comandas"));
		itens.add(item(COMANDA_DESFAZER_FECHAMENTO, "Comandas", "Reabrir comandas fechadas"));
		itens.add(item(COMANDA_CANCELAR, "Comandas", "Cancelar comandas"));
		itens.add(item(ESTOQUE_VISUALIZAR, "Estoque", "Consultar estoque e movimentações"));
		itens.add(item(ESTOQUE_MOVIMENTAR, "Estoque", "Lançar movimentações de estoque"));
		itens.add(item(FINANCEIRO_VISUALIZAR, "Financeiro", "Visualizar lançamentos financeiros"));
		itens.add(item(FINANCEIRO_CRIAR, "Financeiro", "Criar lançamentos"));
		itens.add(item(FINANCEIRO_EDITAR, "Financeiro", "Alterar lançamentos"));
		itens.add(item(FINANCEIRO_EXCLUIR, "Financeiro", "Excluir lançamentos"));
		itens.add(item(FINANCEIRO_PAGAR, "Financeiro", "Registrar o pagamento (baixa) de lançamentos"));
		itens.add(item(FINANCEIRO_RESUMO, "Financeiro", "Ver totais e saldos do financeiro"));
		itens.add(item(CATEGORIA_FINANCEIRA_VISUALIZAR, "Categorias financeiras", "Visualizar categorias financeiras"));
		itens.add(item(CATEGORIA_FINANCEIRA_CRIAR, "Categorias financeiras", "Cadastrar categorias financeiras"));
		itens.add(item(CATEGORIA_FINANCEIRA_EDITAR, "Categorias financeiras", "Alterar categorias financeiras"));
		itens.add(item(CATEGORIA_FINANCEIRA_EXCLUIR, "Categorias financeiras", "Excluir categorias financeiras"));
		itens.add(item(CAIXA_VISUALIZAR, "Caixas", "Visualizar caixas"));
		itens.add(item(CAIXA_CRIAR, "Caixas", "Cadastrar caixas"));
		itens.add(item(CAIXA_EDITAR, "Caixas", "Alterar caixas"));
		itens.add(item(CAIXA_EXCLUIR, "Caixas", "Excluir caixas"));
		itens.add(item(ATA_VISUALIZAR, "Atas", "Visualizar atas"));
		itens.add(item(ATA_CRIAR, "Atas", "Cadastrar atas"));
		itens.add(item(ATA_EDITAR, "Atas", "Alterar atas"));
		itens.add(item(ATA_EXCLUIR, "Atas", "Excluir atas"));
		itens.add(item(ATA_DOCUMENTO_VISUALIZAR, "Atas", "Visualizar/baixar documentos da ata"));
		itens.add(item(ATA_DOCUMENTO_GERENCIAR, "Atas", "Enviar e excluir documentos da ata"));
		itens.add(item(RELATORIO_CONSUMO_PRODUTOS, "Relatórios", "Relatório de consumo de produtos"));
		itens.add(item(RELATORIO_LIVRO_CAIXA, "Relatórios", "Relatório de livro caixa"));
		itens.add(item(RELATORIO_IMPRIMIR, "Relatórios", "Imprimir / gerar PDF dos relatórios"));
		itens.add(item(PARAMETRO_VISUALIZAR, "Parâmetros do sistema", "Visualizar parâmetros do sistema"));
		itens.add(item(PARAMETRO_ALTERAR, "Parâmetros do sistema", "Alterar parâmetros do sistema"));
		itens.add(item(PARAMETRO_LOGO, "Parâmetros do sistema", "Trocar ou remover o logo da associação"));
		return List.copyOf(itens);
	}

	private static Item item(String codigo, String modulo, String descricao) {
		return new Item(codigo, modulo, descricao);
	}

	public static boolean existe(String codigo) {
		return CATALOGO.stream().anyMatch(i -> i.codigo().equals(codigo));
	}

}
