package com.projeto.associacao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.TipoEvento;
import com.projeto.associacao.repository.HistoricoMembroRepository;
import com.projeto.associacao.repository.TipoEventoRepository;

@Service
public class TipoEventoService {

	@Autowired
	private TipoEventoRepository repository;

	@Autowired
	private HistoricoMembroRepository historicoMembroRepository;

	public Iterable<TipoEvento> selecionar() {
		return repository.findAll();
	}

	public TipoEvento cadastrar(TipoEvento tipoEvento) throws BusinessRuleException {
		tipoEvento.setId(0);
		this.validarTipoEvento(tipoEvento);
		return repository.save(tipoEvento);
	}

	public TipoEvento alterar(TipoEvento tipoEvento) throws BusinessRuleException {
		if (tipoEvento.getId() == 0) {
			throw new BusinessRuleException("ID não informado.");
		}

		if (repository.findById(tipoEvento.getId()) == null) {
			throw new BusinessRuleException("Tipo de evento de ID " + tipoEvento.getId() + " não cadastrado.");
		}

		this.validarTipoEvento(tipoEvento);
		return repository.save(tipoEvento);
	}

	public void remover(int id) throws BusinessRuleException {
		if (id == 0) {
			throw new BusinessRuleException("ID não informado.");
		}

		if (repository.findById(id) == null) {
			throw new BusinessRuleException("Tipo de evento de ID " + id + " não cadastrado.");
		}

		if (historicoMembroRepository.existsByTipoEvento_Id(id)) {
			throw new BusinessRuleException("O tipo de evento de ID " + id + " está vinculado a registros do histórico de membros.");
		}

		repository.deleteById(id);
	}

	private void validarTipoEvento(TipoEvento tipoEvento) throws BusinessRuleException {
		if ((tipoEvento.getDescricao() == null) || tipoEvento.getDescricao().isBlank()) {
			throw new BusinessRuleException("Descrição não informada.");
		}

		TipoEvento tipoEventoAux = repository.findByDescricaoIgnoreCase(tipoEvento.getDescricao().trim());
		if ((tipoEventoAux != null) && (tipoEventoAux.getId() != tipoEvento.getId())) {
			throw new BusinessRuleException("Já existe um tipo de evento com a descrição " + tipoEvento.getDescricao() + ".");
		}
	}
}
