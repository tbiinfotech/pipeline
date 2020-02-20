package com.liquidpresentaion.cocktailservice.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.liquidpresentaion.cocktailservice.model.PresentationCocktail;

public interface PresentationCocktailRepository extends PagingAndSortingRepository<PresentationCocktail, Integer>{
	List<PresentationCocktail> findByCocktailIdIs(Integer cocktailId);
	List<PresentationCocktail> findByCocktailIdIn(List<Integer> cocktailId);
}
