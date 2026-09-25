package com.projeto.associacao.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projeto.associacao.dto.comanda.ComandaAberturaRequest;
import com.projeto.associacao.dto.comanda.ItemComandaRequest;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.Comanda;
import com.projeto.associacao.model.ItemComanda;
import com.projeto.associacao.model.Membro;
import com.projeto.associacao.model.Pessoa;
import com.projeto.associacao.model.Produto;
import com.projeto.associacao.model.StatusComanda;
import com.projeto.associacao.repository.ComandaRepository;
import com.projeto.associacao.repository.ItemComandaRepository;
import com.projeto.associacao.repository.MembroRepository;
import com.projeto.associacao.repository.PessoaRepository;
import com.projeto.associacao.repository.ProdutoRepository;

@ExtendWith(MockitoExtension.class)
class ComandaServiceTest {

	@Mock
	private ComandaRepository repository;

	@Mock
	private ItemComandaRepository itemRepository;

	@Mock
	private PessoaRepository pessoaRepository;

	@Mock
	private ProdutoRepository produtoRepository;

	@Mock
	private MembroRepository membroRepository;

	@InjectMocks
	private ComandaService service;

	@Test
	void abrirRejeitaSemPessoaESemNomeTemporario() {
		ComandaAberturaRequest request = new ComandaAberturaRequest();
		assertThrows(BusinessRuleException.class, () -> service.abrir(request));
	}

	@Test
	void abrirComNomeTemporarioFunciona() throws BusinessRuleException {
		ComandaAberturaRequest request = new ComandaAberturaRequest();
		request.setNomeTemporario("Visitante da mesa 3");
		when(repository.save(any(Comanda.class))).thenAnswer(inv -> inv.getArgument(0));
		when(itemRepository.findByComanda_Id(0)).thenReturn(List.of());

		var resultado = service.abrir(request);

		assertEquals(StatusComanda.ABERTA, resultado.getStatus());
		assertEquals("Visitante da mesa 3", resultado.getNomeTemporario());
		assertEquals(BigDecimal.ZERO, resultado.getValorTotal());
	}

	@Test
	void adicionarItemRejeitaComandaFechada() {
		Comanda comanda = new Comanda();
		comanda.setId(1);
		comanda.setStatus(StatusComanda.FECHADA);
		when(repository.findById(1)).thenReturn(comanda);

		ItemComandaRequest request = new ItemComandaRequest();
		request.setIdProduto(1);
		request.setQuantidade(1);

		assertThrows(BusinessRuleException.class, () -> service.adicionarItem(1, request));
	}

	@Test
	void adicionarItemUsaPrecoNormalParaPessoaSemMembroAtivo() throws BusinessRuleException {
		Pessoa pessoa = new Pessoa();
		pessoa.setId(10);

		Comanda comanda = new Comanda();
		comanda.setId(1);
		comanda.setStatus(StatusComanda.ABERTA);
		comanda.setPessoa(pessoa);

		Produto produto = new Produto();
		produto.setId(2);
		produto.setAtivo(true);
		produto.setPreco(new BigDecimal("10.00"));
		produto.setPrecoMembro(new BigDecimal("8.00"));

		when(repository.findById(1)).thenReturn(comanda);
		when(produtoRepository.findById(2)).thenReturn(produto);
		when(membroRepository.findByPessoa_Id(10)).thenReturn(null);
		when(itemRepository.save(any(ItemComanda.class))).thenAnswer(inv -> inv.getArgument(0));
		when(itemRepository.findByComanda_Id(1)).thenReturn(List.of());
		when(repository.save(any(Comanda.class))).thenAnswer(inv -> inv.getArgument(0));

		ItemComandaRequest request = new ItemComandaRequest();
		request.setIdProduto(2);
		request.setQuantidade(3);

		service.adicionarItem(1, request);

		var captor = org.mockito.ArgumentCaptor.forClass(ItemComanda.class);
		org.mockito.Mockito.verify(itemRepository).save(captor.capture());
		assertEquals(new BigDecimal("10.00"), captor.getValue().getPrecoUnitario());
		assertEquals(new BigDecimal("30.00"), captor.getValue().getSubtotal());
	}

	@Test
	void adicionarItemUsaPrecoMembroParaMembroAtivo() throws BusinessRuleException {
		Pessoa pessoa = new Pessoa();
		pessoa.setId(10);

		Comanda comanda = new Comanda();
		comanda.setId(1);
		comanda.setStatus(StatusComanda.ABERTA);
		comanda.setPessoa(pessoa);

		Produto produto = new Produto();
		produto.setId(2);
		produto.setAtivo(true);
		produto.setPreco(new BigDecimal("10.00"));
		produto.setPrecoMembro(new BigDecimal("8.00"));

		Membro membro = new Membro();
		membro.setAtivo(true);

		when(repository.findById(1)).thenReturn(comanda);
		when(produtoRepository.findById(2)).thenReturn(produto);
		when(membroRepository.findByPessoa_Id(10)).thenReturn(membro);
		when(itemRepository.save(any(ItemComanda.class))).thenAnswer(inv -> inv.getArgument(0));
		when(itemRepository.findByComanda_Id(1)).thenReturn(List.of());
		when(repository.save(any(Comanda.class))).thenAnswer(inv -> inv.getArgument(0));

		ItemComandaRequest request = new ItemComandaRequest();
		request.setIdProduto(2);
		request.setQuantidade(2);

		service.adicionarItem(1, request);

		var captor = org.mockito.ArgumentCaptor.forClass(ItemComanda.class);
		org.mockito.Mockito.verify(itemRepository).save(captor.capture());
		assertEquals(new BigDecimal("8.00"), captor.getValue().getPrecoUnitario());
		assertEquals(new BigDecimal("16.00"), captor.getValue().getSubtotal());
	}

	@Test
	void adicionarItemRejeitaProdutoInativo() {
		Comanda comanda = new Comanda();
		comanda.setId(1);
		comanda.setStatus(StatusComanda.ABERTA);

		Produto produto = new Produto();
		produto.setAtivo(false);

		when(repository.findById(1)).thenReturn(comanda);
		when(produtoRepository.findById(2)).thenReturn(produto);

		ItemComandaRequest request = new ItemComandaRequest();
		request.setIdProduto(2);
		request.setQuantidade(1);

		assertThrows(BusinessRuleException.class, () -> service.adicionarItem(1, request));
	}

	@Test
	void fecharRejeitaComandaJaFechada() {
		Comanda comanda = new Comanda();
		comanda.setId(1);
		comanda.setStatus(StatusComanda.FECHADA);
		when(repository.findById(1)).thenReturn(comanda);

		assertThrows(BusinessRuleException.class, () -> service.fechar(1, new com.projeto.associacao.dto.comanda.ComandaFechamentoRequest()));
	}
}
