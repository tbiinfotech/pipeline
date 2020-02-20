package com.liquidpresentaion.cocktailservice.repository;




import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.liquidpresentaion.cocktailservice.model.Ingredient;
import com.liquidpresentation.common.IngredientType;


@Repository
public interface IngredientRepository extends CrudRepository<Ingredient, Long> {
	public Ingredient findByInBrandPkidAndInType(long inBrandPkid,IngredientType ingredientType);
	Ingredient findByInBrandPkid(long inBrandPkid);
}

