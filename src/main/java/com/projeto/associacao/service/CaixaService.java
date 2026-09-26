package com.projeto.associacao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.Caixa;
import com.projeto.associacao.repository.CaixaRepository;
import com.projeto.associacao.repository.LancamentoFinanceiroRepository;

@Service
public class CaixaService {

	@Autowired
	private CaixaRepository repository;

	@Autowired
	private LancamentoFinanceiroRepository lancamentoRepository;

	public Iterable<Caixa> selecionar() {
		return repository.findAll();
	}

	public Caixa cadastrar(Caixa caixa) throws BusinessRuleException {
		caixa.setId(0);
		this.validarCaixa(caixa);
		return repository.save(caixa);
	}

	public Caixa alterar(Caixa caixa) throws BusinessRuleException {
		if (caixa.getId() == 0) {
			throw new BusinessRuleException("ID não informado.");
		}

		if (repository.findById(caixa.getId()) == null) {
			throw new BusinessRuleException("Caixa de ID " + caixa.getId() + " não cadastrado.");
		}

		this.validarCaixa(caixa);
		return repository.save(caixa);
	}

	public void remover(int id) throws BusinessRuleException {
		if (id == 0) {
			throw new BusinessRuleException("ID não informado.");
		}

		if (repository.findById(id) == null) {
			throw new BusinessRuleException("Caixa de ID " + id + " não cadastrado.");
		}

		if (lancamentoRepository.existsByCaixa_Id(id)) {
			throw new BusinessRuleException("O caixa de ID " + id + " já possui lançamentos financeiros e não pode ser excluído. Desative-o em vez de excluir.");
		}

		repository.deleteById(id);
	}

	private void validarCaixa(Caixa caixa) throws BusinessRuleException {
		if ((caixa.getNome() == null) || caixa.getNome().isBlank()) {
			throw new BusinessRuleException("Nome não informado.");
		}

		Caixa caixaAux = repository.findByNomeIgnoreCase(caixa.getNome().trim());
		if ((caixaAux != null) && (caixaAux.getId() != caixa.getId())) {
			throw new BusinessRuleException("Já existe um caixa com o nome " + caixa.getNome() + ".");
		}
	}

}
