package com.liquidpresentation.ingredientservice.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.liquidpresentation.ingredientservice.model.CocktailBrand;

@Repository
public interface CocktailBrandRepository extends CrudRepository<CocktailBrand, Long> {
	public List<CocktailBrand> findByBrandPkId(Long brandPkId);
}

