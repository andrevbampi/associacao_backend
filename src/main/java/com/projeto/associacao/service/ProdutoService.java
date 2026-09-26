package com.projeto.associacao.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.projeto.associacao.dto.produto.ProdutoRequest;
import com.projeto.associacao.dto.produto.ProdutoResponse;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.CategoriaProduto;
import com.projeto.associacao.model.Produto;
import com.projeto.associacao.model.StatusComanda;
import com.projeto.associacao.repository.CategoriaProdutoRepository;
import com.projeto.associacao.repository.ItemComandaRepository;
import com.projeto.associacao.repository.ProdutoRepository;
import com.projeto.associacao.util.ArquivoValidador;

@Service
public class ProdutoService {

	@Autowired
	private ProdutoRepository repository;

	@Autowired
	private CategoriaProdutoRepository categoriaRepository;

	@Autowired
	private ItemComandaRepository itemComandaRepository;

	public Iterable<ProdutoResponse> selecionar(String descricao, Integer idCategoria, Boolean ativo) {
		List<ProdutoResponse> responses = new ArrayList<>();
		for (Produto produto : repository.findAll()) {
			if ((descricao != null) && !descricao.isBlank()
					&& !produto.getDescricao().toLowerCase().contains(descricao.trim().toLowerCase())) {
				continue;
			}
			if ((idCategoria != null) && (produto.getCategoria().getId() != idCategoria)) {
				continue;
			}
			if ((ativo != null) && (produto.isAtivo() != ativo)) {
				continue;
			}
			responses.add(converterParaResponse(produto));
		}
		return responses;
	}

	public ProdutoResponse cadastrar(ProdutoRequest request) throws BusinessRuleException {
		Produto produto = this.validarProduto(request, false);
		return converterParaResponse(repository.save(produto));
	}

	public ProdutoResponse alterar(ProdutoRequest request) throws BusinessRuleException {
		Produto produto = this.validarProduto(request, true);
		return converterParaResponse(repository.save(produto));
	}

	public void remover(int id) throws BusinessRuleException {
		if (id == 0) {
			throw new BusinessRuleException("ID não informado.");
		}

		if (repository.findById(id) == null) {
			throw new BusinessRuleException("Produto de ID " + id + " não cadastrado.");
		}

		if (itemComandaRepository.existsByProduto_Id(id)) {
			throw new BusinessRuleException("O produto de ID " + id + " já foi usado em alguma comanda e não pode ser excluído. Desative-o em vez de excluir.");
		}

		repository.deleteById(id);
	}

	public void salvarFoto(int id, MultipartFile arquivo) throws BusinessRuleException {
		Produto produto = buscarOuFalhar(id);
		ArquivoValidador.validarImagem(arquivo.getContentType(), arquivo.getSize());

		try {
			produto.setFoto(arquivo.getBytes());
		} catch (IOException ex) {
			throw new BusinessRuleException("Não foi possível ler o arquivo enviado.");
		}
		produto.setFotoContentType(arquivo.getContentType());
		produto.setFotoNomeOriginal(arquivo.getOriginalFilename());
		repository.save(produto);
	}

	public void removerFoto(int id) throws BusinessRuleException {
		Produto produto = buscarOuFalhar(id);
		produto.setFoto(null);
		produto.setFotoContentType(null);
		produto.setFotoNomeOriginal(null);
		repository.save(produto);
	}

	public Produto buscarOuFalhar(int id) throws BusinessRuleException {
		if (id == 0) {
			throw new BusinessRuleException("ID não informado.");
		}
		Produto produto = repository.findById(id);
		if (produto == null) {
			throw new BusinessRuleException("Produto de ID " + id + " não cadastrado.");
		}
		return produto;
	}

	private Produto validarProduto(ProdutoRequest request, boolean edicao) throws BusinessRuleException {
		if ((request.getDescricao() == null) || request.getDescricao().isBlank()) {
			throw new BusinessRuleException("Descrição não informada.");
		}

		if ((request.getPreco() == null) || (request.getPreco().compareTo(BigDecimal.ZERO) < 0)) {
			throw new BusinessRuleException("Preço inválido.");
		}

		if ((request.getPrecoMembro() == null) || (request.getPrecoMembro().compareTo(BigDecimal.ZERO) < 0)) {
			throw new BusinessRuleException("Preço para membro inválido.");
		}

		if (request.getIdCategoria() == 0) {
			throw new BusinessRuleException("Categoria não informada.");
		}

		CategoriaProduto categoria = categoriaRepository.findById(request.getIdCategoria());
		if (categoria == null) {
			throw new BusinessRuleException("Não existe categoria cadastrada com o ID " + request.getIdCategoria());
		}

		Produto produto;
		if (edicao) {
			if (request.getId() == 0) {
				throw new BusinessRuleException("ID não informado.");
			}

			produto = repository.findById(request.getId());
			if (produto == null) {
				throw new BusinessRuleException("Produto de ID " + request.getId() + " não cadastrado.");
			}
		} else {
			produto = new Produto();
		}

		produto.setDescricao(request.getDescricao());
		produto.setPreco(request.getPreco());
		produto.setPrecoMembro(request.getPrecoMembro());
		produto.setCategoria(categoria);
		produto.setAtivo(request.isAtivo());
		produto.setEstoqueMinimo(request.getEstoqueMinimo());
		produto.setControlaEstoque(request.isControlaEstoque());
		// O estoque atual só é definido diretamente na criação (estoque inicial).
		// Na edição, ele só muda através de uma movimentação (EstoqueService), para
		// manter o histórico de movimentações sempre consistente com o saldo.
		if (!edicao) {
			produto.setEstoqueAtual(request.getEstoqueAtual());
		}
		return produto;
	}

	// Público porque é reaproveitado por EstoqueService e ComandaService para
	// montar o produto de outros DTOs, sem duplicar a montagem.
	public ProdutoResponse converterParaResponse(Produto produto) {
		ProdutoResponse response = new ProdutoResponse();
		response.setId(produto.getId());
		response.setDescricao(produto.getDescricao());
		response.setPreco(produto.getPreco());
		response.setPrecoMembro(produto.getPrecoMembro());
		response.setCategoria(produto.getCategoria());
		response.setAtivo(produto.isAtivo());
		response.setEstoqueAtual(produto.getEstoqueAtual());
		response.setEstoqueMinimo(produto.getEstoqueMinimo());
		response.setControlaEstoque(produto.isControlaEstoque());
		response.setEstoqueDisponivel(calcularEstoqueDisponivel(produto));
		response.setTemFoto((produto.getFoto() != null) && (produto.getFoto().length > 0));
		return response;
	}

	private int calcularEstoqueDisponivel(Produto produto) {
		if (!produto.isControlaEstoque()) {
			return produto.getEstoqueAtual();
		}
		int reservado = 0;
		for (var item : itemComandaRepository.findByProduto_IdAndComanda_Status(produto.getId(), StatusComanda.ABERTA)) {
			reservado += item.getQuantidade();
		}
		return produto.getEstoqueAtual() - reservado;
	}
}
