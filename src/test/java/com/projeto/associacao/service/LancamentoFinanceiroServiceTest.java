package com.projeto.associacao.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projeto.associacao.dto.financeiro.LancamentoFinanceiroRequest;
import com.projeto.associacao.dto.usuario.UsuarioResponse;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.CategoriaFinanceira;
import com.projeto.associacao.model.LancamentoFinanceiro;
import com.projeto.associacao.model.TipoCategoriaFinanceira;
import com.projeto.associacao.model.Usuario;
import com.projeto.associacao.repository.CategoriaFinanceiraRepository;
import com.projeto.associacao.repository.LancamentoFinanceiroRepository;
import com.projeto.associacao.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class LancamentoFinanceiroServiceTest {

	@Mock
	private LancamentoFinanceiroRepository repository;

	@Mock
	private CategoriaFinanceiraRepository categoriaRepository;

	@Mock
	private UsuarioRepository usuarioRepository;

	@Mock
	private UsuarioService usuarioService;

	@Mock
	private CategoriaFinanceiraService categoriaFinanceiraService;

	@InjectMocks
	private LancamentoFinanceiroService service;

	private CategoriaFinanceira categoria(TipoCategoriaFinanceira tipo) {
		CategoriaFinanceira categoria = new CategoriaFinanceira();
		categoria.setId(1);
		categoria.setDescricao("Categoria Teste");
		categoria.setTipo(tipo);
		return categoria;
	}

	private LancamentoFinanceiroRequest requestValido(String tipo) {
		LancamentoFinanceiroRequest request = new LancamentoFinanceiroRequest();
		request.setIdCategoriaFinanceira(1);
		request.setTipo(tipo);
		request.setValor(new BigDecimal("100.00"));
		request.setData(LocalDate.now());
		return request;
	}

	@Test
	void cadastrarRejeitaTipoIncompativelComCategoria() {
		when(categoriaRepository.findById(1)).thenReturn(categoria(TipoCategoriaFinanceira.DESPESA));

		assertThrows(BusinessRuleException.class, () -> service.cadastrar(requestValido("ENTRADA"), "usuarioteste"));
	}

	@Test
	void cadastrarRejeitaValorZeroOuNegativo() {
		when(categoriaRepository.findById(1)).thenReturn(categoria(TipoCategoriaFinanceira.RECEITA));
		LancamentoFinanceiroRequest request = requestValido("ENTRADA");
		request.setValor(BigDecimal.ZERO);

		assertThrows(BusinessRuleException.class, () -> service.cadastrar(request, "usuarioteste"));
	}

	@Test
	void cadastrarComSucessoParaCategoriaCompativel() throws BusinessRuleException {
		when(categoriaRepository.findById(1)).thenReturn(categoria(TipoCategoriaFinanceira.RECEITA));
		when(usuarioRepository.findByLogin("usuarioteste")).thenReturn(new Usuario());
		when(repository.save(any(LancamentoFinanceiro.class))).thenAnswer(inv -> inv.getArgument(0));
		when(usuarioService.converterParaResponse(any(Usuario.class))).thenReturn(new UsuarioResponse());

		var resultado = service.cadastrar(requestValido("ENTRADA"), "usuarioteste");

		assertEquals(new BigDecimal("100.00"), resultado.getValor());
	}

	@Test
	void registrarPagamentoRejeitaLancamentoJaPago() {
		LancamentoFinanceiro lancamento = new LancamentoFinanceiro();
		lancamento.setId(1);
		lancamento.setPago(true);
		when(repository.findById(1)).thenReturn(lancamento);

		assertThrows(BusinessRuleException.class, () -> service.registrarPagamento(1, "PIX"));
	}
}
