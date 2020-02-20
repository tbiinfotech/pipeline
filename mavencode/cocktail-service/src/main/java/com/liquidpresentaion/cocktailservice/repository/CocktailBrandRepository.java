package com.liquidpresentaion.cocktailservice.repository;



import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.liquidpresentaion.cocktailservice.model.CocktailBrand;


@Repository
public interface CocktailBrandRepository extends CrudRepository<CocktailBrand, Long> {
	public Set<CocktailBrand> findByBrandPkId(Integer brandPkId);
}

