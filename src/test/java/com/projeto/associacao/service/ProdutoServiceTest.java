package com.projeto.associacao.service;

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

import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.Produto;
import com.projeto.associacao.repository.ItemComandaRepository;
import com.projeto.associacao.repository.ProdutoRepository;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

	@Mock
	private ProdutoRepository repository;

	@Mock
	private ItemComandaRepository itemComandaRepository;

	@InjectMocks
	private ProdutoService service;

	private Produto produtoValido() {
		Produto produto = new Produto();
		produto.setDescricao("Refrigerante");
		produto.setPreco(new BigDecimal("6.00"));
		produto.setPrecoMembro(new BigDecimal("5.00"));
		produto.setAtivo(true);
		return produto;
	}

	@Test
	void cadastrarRejeitaPrecoNegativo() {
		Produto produto = produtoValido();
		produto.setPreco(new BigDecimal("-1"));
		assertThrows(BusinessRuleException.class, () -> service.cadastrar(produto));
	}

	@Test
	void cadastrarRejeitaPrecoMembroNulo() {
		Produto produto = produtoValido();
		produto.setPrecoMembro(null);
		assertThrows(BusinessRuleException.class, () -> service.cadastrar(produto));
	}

	@Test
	void removerRejeitaProdutoJaUsadoEmComanda() {
		when(repository.findById(1)).thenReturn(produtoValido());
		when(itemComandaRepository.existsByProduto_Id(1)).thenReturn(true);
		assertThrows(BusinessRuleException.class, () -> service.remover(1));
		verify(repository, never()).deleteById(any());
	}
}
