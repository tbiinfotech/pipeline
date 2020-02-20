package com.liquidpresentaion.users.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.liquidpresentaion.users.model.Client;

@Repository
public interface ClientRepository extends PagingAndSortingRepository<Client, Integer> {
	public Page<Client> findByNameContainingIgnoreCase(String name, Pageable pageable);
	public boolean existsByName(String name);
	public boolean existsByNameAndIdIsNot(String name, int id);
}
