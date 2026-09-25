package com.projeto.associacao.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projeto.associacao.dto.membro.MembroRequest;
import com.projeto.associacao.dto.membro.MembroResponse;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.Membro;
import com.projeto.associacao.model.Pessoa;
import com.projeto.associacao.model.StatusMembro;
import com.projeto.associacao.repository.MembroRepository;
import com.projeto.associacao.repository.PessoaRepository;
import com.projeto.associacao.repository.StatusMembroRepository;

@Service
public class MembroService {

	@Autowired
	private MembroRepository repository;

	@Autowired
	private PessoaRepository pessoaRepository;

	@Autowired
	private StatusMembroRepository statusRepository;

	public Iterable<MembroResponse> selecionar() {
		Iterable<Membro> membros = repository.findAll();
		List<MembroResponse> responses = new ArrayList<>();
		for (Membro membro : membros) {
			responses.add(converterParaResponse(membro));
		}
		return responses;
	}

	public MembroResponse cadastrar(MembroRequest request) throws BusinessRuleException {
		Membro membro = this.validarMembro(request, false);
		return converterParaResponse(repository.save(membro));
	}

	public MembroResponse alterar(MembroRequest request) throws BusinessRuleException {
		Membro membro = this.validarMembro(request, true);
		return converterParaResponse(repository.save(membro));
	}

	public void remover(int id) throws BusinessRuleException {
		if (id == 0) {
			throw new BusinessRuleException("ID não informado.");
		}

		if (repository.findById(id) == null) {
			throw new BusinessRuleException("Membro de ID " + id + " não cadastrado.");
		}

		repository.deleteById(id);
	}

	private Membro validarMembro(MembroRequest request, boolean edicao) throws BusinessRuleException {
		if (request.getIdPessoa() == 0) {
			throw new BusinessRuleException("Id da pessoa não informado.");
		}

		if (request.getIdStatus() == 0) {
			throw new BusinessRuleException("Status não informado.");
		}

		if (edicao) {
			if (request.getId() == 0) {
				throw new BusinessRuleException("ID não informado.");
			}

			Membro membroAux = repository.findById(request.getId());
			if (membroAux == null) {
				throw new BusinessRuleException("Membro de ID " + request.getId() + " não cadastrado.");
			}

			if (request.getIdPessoa() != membroAux.getPessoa().getId()) {
				if (repository.findByPessoa_Id(request.getIdPessoa()) != null) {
					throw new BusinessRuleException("Já existe um membro para a pessoa com o ID " + request.getIdPessoa());
				}
			}

			if (request.getDataInclusao() == null) {
				request.setDataInclusao(membroAux.getDataInclusao());
			}

		} else {
			if (repository.findByPessoa_Id(request.getIdPessoa()) != null) {
				throw new BusinessRuleException("Já existe um membro para a pessoa com o ID " + request.getIdPessoa());
			}

			request.setId(0);
			if (request.getDataInclusao() == null) {
				request.setDataInclusao(LocalDate.now());
			}
		}

		if ((request.getDataSaida() != null) && request.getDataSaida().isBefore(request.getDataInclusao())) {
			throw new BusinessRuleException("A data de saída não pode ser anterior à data de inclusão.");
		}

		Pessoa pessoa = pessoaRepository.findById(request.getIdPessoa());
		if (pessoa == null) {
			throw new BusinessRuleException("Não existe pessoa cadastrada com o ID " + request.getIdPessoa());
		}

		StatusMembro status = statusRepository.findById(request.getIdStatus());
		if (status == null) {
			throw new BusinessRuleException("Não existe status cadastrado com o ID " + request.getIdStatus());
		}

		Membro membro = new Membro();
		membro.setId(request.getId());
		membro.setPessoa(pessoa);
		membro.setStatus(status);
		membro.setAtivo(request.isAtivo());
		membro.setDataInclusao(request.getDataInclusao());
		membro.setDataSaida(request.getDataSaida());
		return membro;
	}

	public MembroResponse converterParaResponse(Membro membro) {
		MembroResponse response = new MembroResponse();
		response.setId(membro.getId());
		response.setPessoa(membro.getPessoa());
		response.setStatus(membro.getStatus());
		response.setAtivo(membro.isAtivo());
		response.setDataInclusao(membro.getDataInclusao());
		response.setDataSaida(membro.getDataSaida());
		return response;
	}
}
