package com.projeto.associacao.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projeto.associacao.dto.relatorio.RelatorioLivroCaixaResponse;
import com.projeto.associacao.model.CategoriaFinanceira;
import com.projeto.associacao.model.Caixa;
import com.projeto.associacao.model.LancamentoFinanceiro;
import com.projeto.associacao.model.TipoLancamento;
import com.projeto.associacao.repository.LancamentoFinanceiroRepository;

@ExtendWith(MockitoExtension.class)
class RelatorioLivroCaixaServiceTest {

	@Mock
	private LancamentoFinanceiroRepository repository;

	@InjectMocks
	private RelatorioLivroCaixaService service;

	@BeforeEach
	void montarLancamentos() {
		Caixa a = caixa(1, "A");
		Caixa b = caixa(2, "B");
		when(repository.findAll()).thenReturn(List.of(
				lancamento(a, TipoLancamento.ENTRADA, "100.00", LocalDate.of(2026, 5, 10)),
				lancamento(a, TipoLancamento.SAIDA, "30.00", LocalDate.of(2026, 6, 10)),
				lancamento(a, TipoLancamento.ENTRADA, "50.00", LocalDate.of(2026, 7, 10)),
				lancamento(b, TipoLancamento.ENTRADA, "20.00", LocalDate.of(2026, 8, 10)),
				lancamento(b, TipoLancamento.ENTRADA, "999.00", LocalDate.of(2026, 9, 10))));
	}

	@Test
	void acumuladoIgnoraDataInicioEVaiAteDataFim() {
		RelatorioLivroCaixaResponse r = service.gerarLivroCaixa(null, LocalDate.of(2026, 7, 1), LocalDate.of(2026, 8, 31), false, false);

		assertEquals(0, new BigDecimal("70.00").compareTo(r.getSaldoGeral()));
		assertEquals(0, new BigDecimal("140.00").compareTo(r.getAcumuladoGeral().getSaldo()));
		assertEquals(LocalDate.of(2026, 8, 31), r.getDataAcumuladoAte());
	}

	@Test
	void acumuladoPorCaixaQuandoAgrupado() {
		RelatorioLivroCaixaResponse r = service.gerarLivroCaixa(null, LocalDate.of(2026, 7, 1), LocalDate.of(2026, 8, 31), true, false);

		assertEquals(2, r.getAcumuladosPorCaixa().size());
		assertEquals(0, new BigDecimal("120.00").compareTo(r.getAcumuladosPorCaixa().get(0).getSaldo()));
		assertEquals(0, new BigDecimal("20.00").compareTo(r.getAcumuladosPorCaixa().get(1).getSaldo()));
	}

	@Test
	void acumuladoRespeitaFiltroDeCaixa() {
		RelatorioLivroCaixaResponse r = service.gerarLivroCaixa(2, null, LocalDate.of(2026, 8, 31), false, false);

		assertEquals(0, new BigDecimal("20.00").compareTo(r.getAcumuladoGeral().getSaldo()));
	}

	@Test
	void periodoSemMovimentosAindaTrazAcumulado() {
		RelatorioLivroCaixaResponse r = service.gerarLivroCaixa(null, LocalDate.of(2027, 1, 1), LocalDate.of(2027, 1, 31), false, false);

		assertEquals(0, r.getLinhas().size());
		assertEquals(0, new BigDecimal("1139.00").compareTo(r.getAcumuladoGeral().getSaldo()));
	}

	private Caixa caixa(int id, String nome) {
		Caixa c = new Caixa();
		c.setId(id);
		c.setNome(nome);
		return c;
	}

	private LancamentoFinanceiro lancamento(Caixa caixa, TipoLancamento tipo, String valor, LocalDate data) {
		CategoriaFinanceira categoria = new CategoriaFinanceira();
		categoria.setDescricao("Outros");
		LancamentoFinanceiro l = new LancamentoFinanceiro();
		l.setCaixa(caixa);
		l.setTipo(tipo);
		l.setValor(new BigDecimal(valor));
		l.setData(data);
		l.setDescricao("x");
		l.setPago(true);
		l.setCategoriaFinanceira(categoria);
		return l;
	}
}
