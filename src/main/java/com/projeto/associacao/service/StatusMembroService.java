package com.projeto.associacao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.StatusMembro;
import com.projeto.associacao.repository.MembroRepository;
import com.projeto.associacao.repository.StatusMembroRepository;

@Service
public class StatusMembroService {

	@Autowired
	private StatusMembroRepository repository;

	@Autowired
	private MembroRepository membroRepository;

	public Iterable<StatusMembro> selecionar() {
		return repository.findAll();
	}

	public StatusMembro cadastrar(StatusMembro status) throws BusinessRuleException {
		status.setId(0);
		this.validarStatus(status);
		return repository.save(status);
	}

	public StatusMembro alterar(StatusMembro status) throws BusinessRuleException {
		if (status.getId() == 0) {
			throw new BusinessRuleException("ID não informado.");
		}

		if (repository.findById(status.getId()) == null) {
			throw new BusinessRuleException("Status de ID " + status.getId() + " não cadastrado.");
		}

		this.validarStatus(status);
		return repository.save(status);
	}

	public void remover(int id) throws BusinessRuleException {
		if (id == 0) {
			throw new BusinessRuleException("ID não informado.");
		}

		if (repository.findById(id) == null) {
			throw new BusinessRuleException("Status de ID " + id + " não cadastrado.");
		}

		if (membroRepository.existsByStatus_Id(id)) {
			throw new BusinessRuleException("O status de ID " + id + " está vinculado a membros.");
		}

		repository.deleteById(id);
	}

	private void validarStatus(StatusMembro status) throws BusinessRuleException {
		if ((status.getDescricao() == null) || status.getDescricao().isBlank()) {
			throw new BusinessRuleException("Descrição não informada.");
		}

		StatusMembro statusAux = repository.findByDescricaoIgnoreCase(status.getDescricao().trim());
		if ((statusAux != null) && (statusAux.getId() != status.getId())) {
			throw new BusinessRuleException("Já existe um status com a descrição " + status.getDescricao() + ".");
		}
	}
}
