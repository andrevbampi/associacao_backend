package com.projeto.associacao.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.CategoriaProduto;
import com.projeto.associacao.repository.CategoriaProdutoRepository;
import com.projeto.associacao.repository.ProdutoRepository;

@ExtendWith(MockitoExtension.class)
class CategoriaProdutoServiceTest {

	@Mock
	private CategoriaProdutoRepository repository;

	@Mock
	private ProdutoRepository produtoRepository;

	@InjectMocks
	private CategoriaProdutoService service;

	@Test
	void cadastrarRejeitaDescricaoDuplicada() {
		CategoriaProduto existente = new CategoriaProduto();
		existente.setId(1);
		existente.setDescricao("Bebidas");
		when(repository.findByDescricaoIgnoreCase("bebidas")).thenReturn(existente);

		CategoriaProduto nova = new CategoriaProduto();
		nova.setDescricao("bebidas");
		assertThrows(BusinessRuleException.class, () -> service.cadastrar(nova));
		verify(repository, never()).save(any());
	}

	@Test
	void removerRejeitaCategoriaEmUsoPorProduto() {
		when(repository.findById(1)).thenReturn(new CategoriaProduto());
		when(produtoRepository.existsByCategoria_Id(1)).thenReturn(true);
		assertThrows(BusinessRuleException.class, () -> service.remover(1));
		verify(repository, never()).deleteById(any());
	}
}
