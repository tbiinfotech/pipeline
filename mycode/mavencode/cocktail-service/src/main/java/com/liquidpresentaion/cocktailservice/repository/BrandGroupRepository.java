package com.liquidpresentaion.cocktailservice.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.liquidpresentaion.cocktailservice.model.BrandGroup;

public interface BrandGroupRepository extends CrudRepository<BrandGroup, Long> {
	List<BrandGroup> findByBdgBrandPkId(long brandPkId);
}
