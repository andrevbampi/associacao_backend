package com.projeto.associacao.repository;

import org.springframework.data.repository.CrudRepository;

import com.projeto.associacao.model.ItemComanda;

public interface ItemComandaRepository extends CrudRepository<ItemComanda, Integer> {

	ItemComanda findById(int id);

	Iterable<ItemComanda> findByComanda_Id(int idComanda);

	boolean existsByProduto_Id(int idProduto);

}
