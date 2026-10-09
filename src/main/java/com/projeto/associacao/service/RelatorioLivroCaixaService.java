package com.projeto.associacao.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projeto.associacao.dto.relatorio.RelatorioLivroCaixaAcumulado;
import com.projeto.associacao.dto.relatorio.RelatorioLivroCaixaLinha;
import com.projeto.associacao.dto.relatorio.RelatorioLivroCaixaResponse;
import com.projeto.associacao.model.LancamentoFinanceiro;
import com.projeto.associacao.model.TipoLancamento;
import com.projeto.associacao.repository.LancamentoFinanceiroRepository;

/**
 * Livro Caixa: todo movimento financeiro efetivamente pago (entrada ou saída
 * real de dinheiro), venha ou não de uma comanda. Vendas de comanda e
 * mensalidades de membro são resumidas em uma linha por grupo (não listadas
 * uma a uma); os demais lançamentos aparecem individualmente.
 */
@Service
public class RelatorioLivroCaixaService {

	private static final String CATEGORIA_MENSALIDADE = "Mensalidade de membro";
	private static final DateTimeFormatter FORMATO_MES = DateTimeFormatter.ofPattern("yyyy-MM");

	@Autowired
	private LancamentoFinanceiroRepository repository;

	public RelatorioLivroCaixaResponse gerarLivroCaixa(Integer idCaixa, LocalDate dataInicio, LocalDate dataFim,
			boolean agruparPorCaixa, boolean agruparPorMes) {

		List<RelatorioLivroCaixaLinha> linhasDetalhe = new ArrayList<>();
		Map<String, RelatorioLivroCaixaLinha> vendasComandas = new LinkedHashMap<>();
		Map<String, RelatorioLivroCaixaLinha> mensalidades = new LinkedHashMap<>();

		for (LancamentoFinanceiro lancamento : repository.findAll()) {
			if (!lancamento.isPago()) {
				continue;
			}
			if ((idCaixa != null) && (lancamento.getCaixa().getId() != idCaixa)) {
				continue;
			}
			if ((dataInicio != null) && lancamento.getData().isBefore(dataInicio)) {
				continue;
			}
			if ((dataFim != null) && lancamento.getData().isAfter(dataFim)) {
				continue;
			}

			String nomeCaixa = agruparPorCaixa ? lancamento.getCaixa().getNome() : null;
			String mes = agruparPorMes ? lancamento.getData().format(FORMATO_MES) : null;
			// Chave de agrupamento das linhas resumidas (vendas de comanda/mensalidades):
			// só diferencia por caixa/mês quando o respectivo agrupamento está ativo —
			// senão, tudo cai no mesmo grupo (uma única linha), como pedido.
			String chaveGrupo = (agruparPorCaixa ? String.valueOf(lancamento.getCaixa().getId()) : "") + "|" + mes;

			boolean deComanda = lancamento.getComanda() != null;
			boolean deMensalidade = !deComanda && (lancamento.getCategoriaFinanceira().getDescricao() != null)
					&& lancamento.getCategoriaFinanceira().getDescricao().trim().equalsIgnoreCase(CATEGORIA_MENSALIDADE);

			if (deComanda) {
				RelatorioLivroCaixaLinha linha = vendasComandas.get(chaveGrupo);
				if (linha == null) {
					linha = new RelatorioLivroCaixaLinha();
					linha.setTipoLinha("COMANDAS");
					linha.setCaixa(nomeCaixa);
					linha.setMes(mes);
					linha.setDescricao("Vendas de Comandas");
					linha.setValorEntrada(BigDecimal.ZERO);
					linha.setValorSaida(BigDecimal.ZERO);
					vendasComandas.put(chaveGrupo, linha);
				}
				somarValor(linha, lancamento);
			} else if (deMensalidade) {
				RelatorioLivroCaixaLinha linha = mensalidades.get(chaveGrupo);
				if (linha == null) {
					linha = new RelatorioLivroCaixaLinha();
					linha.setTipoLinha("MENSALIDADES");
					linha.setCaixa(nomeCaixa);
					linha.setMes(mes);
					linha.setDescricao("Mensalidades de Membros");
					linha.setValorEntrada(BigDecimal.ZERO);
					linha.setValorSaida(BigDecimal.ZERO);
					mensalidades.put(chaveGrupo, linha);
				}
				somarValor(linha, lancamento);
			} else {
				RelatorioLivroCaixaLinha linha = new RelatorioLivroCaixaLinha();
				linha.setTipoLinha("DETALHE");
				linha.setCaixa(nomeCaixa);
				linha.setMes(mes);
				linha.setData(lancamento.getData());
				linha.setDescricao(lancamento.getDescricao());
				linha.setCategoria(lancamento.getCategoriaFinanceira().getDescricao());
				linha.setObservacao(lancamento.getObservacao());
				linha.setValorEntrada(BigDecimal.ZERO);
				linha.setValorSaida(BigDecimal.ZERO);
				somarValor(linha, lancamento);
				linhasDetalhe.add(linha);
			}
		}

		List<RelatorioLivroCaixaLinha> linhas = new ArrayList<>();
		linhas.addAll(linhasDetalhe);
		linhas.addAll(vendasComandas.values());
		linhas.addAll(mensalidades.values());

		// Prioridade de ordenação: caixa > mês > data (linhas agregadas, sem data
		// específica, ficam depois das linhas de detalhe daquele mesmo grupo).
		linhas.sort(Comparator
				.comparing((RelatorioLivroCaixaLinha l) -> l.getCaixa() == null ? "" : l.getCaixa(), String.CASE_INSENSITIVE_ORDER)
				.thenComparing(l -> l.getMes() == null ? "" : l.getMes())
				.thenComparing(l -> l.getData() == null ? LocalDate.MAX : l.getData())
				.thenComparing(RelatorioLivroCaixaLinha::getDescricao, String.CASE_INSENSITIVE_ORDER));

		RelatorioLivroCaixaResponse response = new RelatorioLivroCaixaResponse();
		response.setLinhas(linhas);

		BigDecimal totalEntradas = BigDecimal.ZERO;
		BigDecimal totalSaidas = BigDecimal.ZERO;
		for (RelatorioLivroCaixaLinha linha : linhas) {
			totalEntradas = totalEntradas.add(linha.getValorEntrada());
			totalSaidas = totalSaidas.add(linha.getValorSaida());
		}
		response.setTotalEntradasGeral(totalEntradas);
		response.setTotalSaidasGeral(totalSaidas);
		response.setSaldoGeral(totalEntradas.subtract(totalSaidas));

		// Acumulado: mesmos filtros (só o caixa), mas ignora a data de início e vai
		// do começo de tudo até a data fim (ou até hoje, se não houver data fim).
		LocalDate dataAcumuladoAte = (dataFim != null) ? dataFim : LocalDate.now();
		response.setDataAcumuladoAte(dataAcumuladoAte);
		calcularAcumulado(response, idCaixa, dataAcumuladoAte, agruparPorCaixa);

		return response;
	}

	private void calcularAcumulado(RelatorioLivroCaixaResponse response, Integer idCaixa, LocalDate ate, boolean agruparPorCaixa) {
		RelatorioLivroCaixaAcumulado geral = novoAcumulado(null);
		Map<String, RelatorioLivroCaixaAcumulado> porCaixa = new LinkedHashMap<>();

		for (LancamentoFinanceiro lancamento : repository.findAll()) {
			if (!lancamento.isPago()) {
				continue;
			}
			if ((idCaixa != null) && (lancamento.getCaixa().getId() != idCaixa)) {
				continue;
			}
			if (lancamento.getData().isAfter(ate)) {
				continue;
			}

			somarAcumulado(geral, lancamento);
			if (agruparPorCaixa) {
				porCaixa.computeIfAbsent(lancamento.getCaixa().getNome(), this::novoAcumulado);
				somarAcumulado(porCaixa.get(lancamento.getCaixa().getNome()), lancamento);
			}
		}

		response.setAcumuladoGeral(geral);
		List<RelatorioLivroCaixaAcumulado> lista = new ArrayList<>(porCaixa.values());
		lista.sort(Comparator.comparing(RelatorioLivroCaixaAcumulado::getCaixa, String.CASE_INSENSITIVE_ORDER));
		response.setAcumuladosPorCaixa(lista);
	}

	private RelatorioLivroCaixaAcumulado novoAcumulado(String caixa) {
		RelatorioLivroCaixaAcumulado acumulado = new RelatorioLivroCaixaAcumulado();
		acumulado.setCaixa(caixa);
		acumulado.setTotalEntradas(BigDecimal.ZERO);
		acumulado.setTotalSaidas(BigDecimal.ZERO);
		acumulado.setSaldo(BigDecimal.ZERO);
		return acumulado;
	}

	private void somarAcumulado(RelatorioLivroCaixaAcumulado acumulado, LancamentoFinanceiro lancamento) {
		if (lancamento.getTipo() == TipoLancamento.ENTRADA) {
			acumulado.setTotalEntradas(acumulado.getTotalEntradas().add(lancamento.getValor()));
			acumulado.setSaldo(acumulado.getSaldo().add(lancamento.getValor()));
		} else {
			acumulado.setTotalSaidas(acumulado.getTotalSaidas().add(lancamento.getValor()));
			acumulado.setSaldo(acumulado.getSaldo().subtract(lancamento.getValor()));
		}
	}

	private void somarValor(RelatorioLivroCaixaLinha linha, LancamentoFinanceiro lancamento) {
		if (lancamento.getTipo() == TipoLancamento.ENTRADA) {
			linha.setValorEntrada(linha.getValorEntrada().add(lancamento.getValor()));
		} else {
			linha.setValorSaida(linha.getValorSaida().add(lancamento.getValor()));
		}
	}

}
