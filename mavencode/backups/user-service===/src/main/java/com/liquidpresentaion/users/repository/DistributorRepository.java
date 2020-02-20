package com.liquidpresentaion.users.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.liquidpresentaion.users.model.Distributor;

@Repository
public interface DistributorRepository extends PagingAndSortingRepository<Distributor, Integer> {
	public Page<Distributor> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
