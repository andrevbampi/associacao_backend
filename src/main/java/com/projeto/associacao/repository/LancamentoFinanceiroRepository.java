package com.projeto.associacao.repository;

import org.springframework.data.repository.CrudRepository;

import com.projeto.associacao.model.LancamentoFinanceiro;

public interface LancamentoFinanceiroRepository extends CrudRepository<LancamentoFinanceiro, Integer> {

	LancamentoFinanceiro findById(int id);

	Iterable<LancamentoFinanceiro> findAllByOrderByDataDesc();

	Iterable<LancamentoFinanceiro> findByComanda_Id(int idComanda);

	boolean existsByCategoriaFinanceira_Id(int idCategoriaFinanceira);

}
