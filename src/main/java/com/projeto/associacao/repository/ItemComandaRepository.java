package com.projeto.associacao.repository;

import org.springframework.data.repository.CrudRepository;

import com.projeto.associacao.model.ItemComanda;
import com.projeto.associacao.model.StatusComanda;

public interface ItemComandaRepository extends CrudRepository<ItemComanda, Integer> {

	ItemComanda findById(int id);

	Iterable<ItemComanda> findByComanda_Id(int idComanda);

	Iterable<ItemComanda> findByProduto_IdAndComanda_Status(int idProduto, StatusComanda status);

	boolean existsByProduto_Id(int idProduto);

}
