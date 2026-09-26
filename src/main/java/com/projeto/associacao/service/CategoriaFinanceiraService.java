package com.projeto.associacao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.CategoriaFinanceira;
import com.projeto.associacao.model.TipoCategoriaFinanceira;
import com.projeto.associacao.repository.CategoriaFinanceiraRepository;
import com.projeto.associacao.repository.LancamentoFinanceiroRepository;

@Service
public class CategoriaFinanceiraService {

	@Autowired
	private CategoriaFinanceiraRepository repository;

	@Autowired
	private LancamentoFinanceiroRepository lancamentoRepository;

	public Iterable<CategoriaFinanceira> selecionar() {
		return repository.findAll();
	}

	public CategoriaFinanceira cadastrar(CategoriaFinanceira categoria) throws BusinessRuleException {
		categoria.setId(0);
		this.validarCategoria(categoria);
		return repository.save(categoria);
	}

	public CategoriaFinanceira alterar(CategoriaFinanceira categoria) throws BusinessRuleException {
		if (categoria.getId() == 0) {
			throw new BusinessRuleException("ID não informado.");
		}

		if (repository.findById(categoria.getId()) == null) {
			throw new BusinessRuleException("Categoria financeira de ID " + categoria.getId() + " não cadastrada.");
		}

		this.validarCategoria(categoria);
		return repository.save(categoria);
	}

	public void remover(int id) throws BusinessRuleException {
		if (id == 0) {
			throw new BusinessRuleException("ID não informado.");
		}

		if (repository.findById(id) == null) {
			throw new BusinessRuleException("Categoria financeira de ID " + id + " não cadastrada.");
		}

		if (lancamentoRepository.existsByCategoriaFinanceira_Id(id)) {
			throw new BusinessRuleException("A categoria financeira de ID " + id + " está vinculada a lançamentos.");
		}

		repository.deleteById(id);
	}

	private void validarCategoria(CategoriaFinanceira categoria) throws BusinessRuleException {
		if ((categoria.getDescricao() == null) || categoria.getDescricao().isBlank()) {
			throw new BusinessRuleException("Descrição não informada.");
		}

		if (categoria.getTipo() == null) {
			throw new BusinessRuleException("Tipo não informado. Use RECEITA ou DESPESA.");
		}

		CategoriaFinanceira categoriaAux = repository.findByDescricaoIgnoreCase(categoria.getDescricao().trim());
		if ((categoriaAux != null) && (categoriaAux.getId() != categoria.getId())) {
			throw new BusinessRuleException("Já existe uma categoria financeira com a descrição " + categoria.getDescricao() + ".");
		}
	}

	// Usado pela integração automática da Comanda: encontra (ou cria) a
	// categoria de receita padrão para vendas de produtos no bar/caixa.
	public CategoriaFinanceira buscarOuCriarCategoriaVendaDeProdutos() throws BusinessRuleException {
		CategoriaFinanceira existente = repository.findByDescricaoIgnoreCase("Venda de Produtos");
		if (existente != null) {
			return existente;
		}

		CategoriaFinanceira categoria = new CategoriaFinanceira();
		categoria.setDescricao("Venda de Produtos");
		categoria.setTipo(TipoCategoriaFinanceira.RECEITA);
		return repository.save(categoria);
	}

}
