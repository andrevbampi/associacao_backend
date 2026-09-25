package com.projeto.associacao.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projeto.associacao.dto.produto.ProdutoRequest;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.CategoriaProduto;
import com.projeto.associacao.model.Produto;
import com.projeto.associacao.repository.CategoriaProdutoRepository;
import com.projeto.associacao.repository.ItemComandaRepository;
import com.projeto.associacao.repository.ProdutoRepository;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

	@Mock
	private ProdutoRepository repository;

	@Mock
	private CategoriaProdutoRepository categoriaRepository;

	@Mock
	private ItemComandaRepository itemComandaRepository;

	@InjectMocks
	private ProdutoService service;

	private ProdutoRequest requestValido() {
		ProdutoRequest request = new ProdutoRequest();
		request.setDescricao("Refrigerante");
		request.setPreco(new BigDecimal("6.00"));
		request.setPrecoMembro(new BigDecimal("5.00"));
		request.setIdCategoria(1);
		request.setAtivo(true);
		return request;
	}

	@Test
	void cadastrarRejeitaPrecoNegativo() {
		ProdutoRequest request = requestValido();
		request.setPreco(new BigDecimal("-1"));
		assertThrows(BusinessRuleException.class, () -> service.cadastrar(request));
	}

	@Test
	void cadastrarRejeitaPrecoMembroNulo() {
		ProdutoRequest request = requestValido();
		request.setPrecoMembro(null);
		assertThrows(BusinessRuleException.class, () -> service.cadastrar(request));
	}

	@Test
	void cadastrarRejeitaCategoriaNaoInformada() {
		ProdutoRequest request = requestValido();
		request.setIdCategoria(0);
		assertThrows(BusinessRuleException.class, () -> service.cadastrar(request));
	}

	@Test
	void cadastrarRejeitaCategoriaInexistente() {
		when(categoriaRepository.findById(1)).thenReturn(null);
		assertThrows(BusinessRuleException.class, () -> service.cadastrar(requestValido()));
		verify(repository, never()).save(any());
	}

	@Test
	void cadastrarComSucessoVinculaCategoria() throws BusinessRuleException {
		CategoriaProduto categoria = new CategoriaProduto();
		categoria.setId(1);
		categoria.setDescricao("Bebidas");
		when(categoriaRepository.findById(1)).thenReturn(categoria);
		when(repository.save(any(Produto.class))).thenAnswer(inv -> inv.getArgument(0));

		var resultado = service.cadastrar(requestValido());

		assertEquals("Bebidas", resultado.getCategoria().getDescricao());
	}

	@Test
	void removerRejeitaProdutoJaUsadoEmComanda() {
		Produto produto = new Produto();
		when(repository.findById(1)).thenReturn(produto);
		when(itemComandaRepository.existsByProduto_Id(1)).thenReturn(true);
		assertThrows(BusinessRuleException.class, () -> service.remover(1));
		verify(repository, never()).deleteById(any());
	}
}
