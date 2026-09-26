package com.projeto.associacao.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projeto.associacao.dto.estoque.MovimentoEstoqueRequest;
import com.projeto.associacao.dto.produto.ProdutoResponse;
import com.projeto.associacao.dto.usuario.UsuarioResponse;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.MovimentoEstoque;
import com.projeto.associacao.model.Produto;
import com.projeto.associacao.model.Usuario;
import com.projeto.associacao.repository.MovimentoEstoqueRepository;
import com.projeto.associacao.repository.ProdutoRepository;
import com.projeto.associacao.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class EstoqueServiceTest {

	@Mock
	private MovimentoEstoqueRepository repository;

	@Mock
	private ProdutoRepository produtoRepository;

	@Mock
	private UsuarioRepository usuarioRepository;

	@Mock
	private ProdutoService produtoService;

	@Mock
	private LancamentoFinanceiroService lancamentoFinanceiroService;

	@Mock
	private UsuarioService usuarioService;

	@InjectMocks
	private EstoqueService service;

	private Produto produtoComEstoque(int estoqueAtual) {
		Produto produto = new Produto();
		produto.setId(1);
		produto.setDescricao("Refrigerante");
		produto.setControlaEstoque(true);
		produto.setEstoqueAtual(estoqueAtual);
		return produto;
	}

	private MovimentoEstoqueRequest requestValido(String tipo, int quantidade) {
		MovimentoEstoqueRequest request = new MovimentoEstoqueRequest();
		request.setIdProduto(1);
		request.setTipo(tipo);
		request.setQuantidade(quantidade);
		return request;
	}

	@Test
	void lancarRejeitaProdutoQueNaoControlaEstoque() {
		Produto produto = produtoComEstoque(10);
		produto.setControlaEstoque(false);
		when(produtoRepository.findById(1)).thenReturn(produto);

		assertThrows(BusinessRuleException.class, () -> service.lancar(requestValido("ENTRADA", 5), "usuarioteste"));
	}

	@Test
	void lancarSaidaRejeitaQuandoEstoqueFicariaNegativo() {
		Produto produto = produtoComEstoque(3);
		when(produtoRepository.findById(1)).thenReturn(produto);
		when(usuarioRepository.findByLogin("usuarioteste")).thenReturn(new Usuario());

		assertThrows(BusinessRuleException.class, () -> service.lancar(requestValido("SAIDA", 10), "usuarioteste"));
	}

	@Test
	void lancarEntradaAtualizaEstoqueDoProduto() throws BusinessRuleException {
		Produto produto = produtoComEstoque(10);
		when(produtoRepository.findById(1)).thenReturn(produto);
		when(usuarioRepository.findByLogin("usuarioteste")).thenReturn(new Usuario());
		when(repository.save(any(MovimentoEstoque.class))).thenAnswer(inv -> inv.getArgument(0));
		when(produtoService.converterParaResponse(any(Produto.class))).thenReturn(new ProdutoResponse());
		when(usuarioService.converterParaResponse(any(Usuario.class))).thenReturn(new UsuarioResponse());

		var resultado = service.lancar(requestValido("ENTRADA", 15), "usuarioteste");

		assertEquals(10, resultado.getEstoqueAnterior());
		assertEquals(25, resultado.getEstoquePosterior());
		assertEquals(25, produto.getEstoqueAtual());
	}

	@Test
	void lancarAjusteDefineValorAbsoluto() throws BusinessRuleException {
		Produto produto = produtoComEstoque(50);
		when(produtoRepository.findById(1)).thenReturn(produto);
		when(usuarioRepository.findByLogin("usuarioteste")).thenReturn(new Usuario());
		when(repository.save(any(MovimentoEstoque.class))).thenAnswer(inv -> inv.getArgument(0));
		when(produtoService.converterParaResponse(any(Produto.class))).thenReturn(new ProdutoResponse());
		when(usuarioService.converterParaResponse(any(Usuario.class))).thenReturn(new UsuarioResponse());

		var resultado = service.lancar(requestValido("AJUSTE", 8), "usuarioteste");

		assertEquals(50, resultado.getEstoqueAnterior());
		assertEquals(8, resultado.getEstoquePosterior());
		assertEquals(8, produto.getEstoqueAtual());
	}
}
