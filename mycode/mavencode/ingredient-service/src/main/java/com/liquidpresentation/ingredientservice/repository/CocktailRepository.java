package com.liquidpresentation.ingredientservice.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.liquidpresentation.ingredientservice.model.Cocktail;

@Repository
public interface CocktailRepository extends PagingAndSortingRepository<Cocktail, Integer> {
	public List<Cocktail> findByBrandPkId(Long brandPkId);
}
