package com.liquidpresentaion.users.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.liquidpresentaion.users.model.Domain;

@Repository
public interface DomainRepository extends PagingAndSortingRepository<Domain, Integer> {
	public Page<Domain> findByNameContainingIgnoreCase(String name, Pageable pageable);
	public List<Domain> findByNameIgnoreCaseEndingWith(String name);
	public boolean existsByNameIgnoreCase(String name);
	public boolean existsByNameIgnoreCaseAndPkIdIsNot(String name, int pkId);
}
