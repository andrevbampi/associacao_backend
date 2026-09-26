package com.projeto.associacao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projeto.associacao.dto.parametroSistema.ParametroSistemaRequest;
import com.projeto.associacao.model.BusinessRuleException;
import com.projeto.associacao.model.ParametroSistema;
import com.projeto.associacao.repository.ParametroSistemaRepository;

@Service
public class ParametroSistemaService {

	public static final String NOME_ASSOCIACAO = "NOME_ASSOCIACAO";
	public static final String DOCUMENTO_ASSOCIACAO = "DOCUMENTO_ASSOCIACAO";
	public static final String ENDERECO_ASSOCIACAO = "ENDERECO_ASSOCIACAO";
	public static final String TELEFONE_ASSOCIACAO = "TELEFONE_ASSOCIACAO";
	public static final String EMAIL_ASSOCIACAO = "EMAIL_ASSOCIACAO";
	public static final String CAIXA_COMANDA = "CAIXA_COMANDA";

	@Autowired
	private ParametroSistemaRepository repository;

	public Iterable<ParametroSistema> selecionar() {
		return repository.findAll();
	}

	public ParametroSistema alterar(ParametroSistemaRequest request) throws BusinessRuleException {
		if (request.getId() == 0) {
			throw new BusinessRuleException("ID não informado.");
		}

		ParametroSistema parametro = repository.findById(request.getId());
		if (parametro == null) {
			throw new BusinessRuleException("Parâmetro de ID " + request.getId() + " não cadastrado.");
		}

		// A chave é fixa: só o valor e a descrição podem ser alterados pela tela.
		parametro.setValor(request.getValor());
		parametro.setDescricao(request.getDescricao());
		return repository.save(parametro);
	}

	// Devolve null se o parâmetro não existir ou estiver vazio.
	public String buscarValor(String chave) {
		ParametroSistema parametro = repository.findByChave(chave);
		if ((parametro == null) || (parametro.getValor() == null) || parametro.getValor().isBlank()) {
			return null;
		}
		return parametro.getValor();
	}

	// Devolve null se o parâmetro não existir, estiver vazio, zerado ou não for um número válido.
	public Integer buscarValorInteiro(String chave) {
		String valor = buscarValor(chave);
		if (valor == null) {
			return null;
		}
		try {
			int numero = Integer.parseInt(valor.trim());
			return (numero == 0) ? null : numero;
		} catch (NumberFormatException ex) {
			return null;
		}
	}

}
