package com.liquidpresentaion.users.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.liquidpresentaion.users.model.Ingredient;
import com.liquidpresentation.common.IngredientType;

@Repository
public interface IngredientRepository extends PagingAndSortingRepository<Ingredient, Integer> {

	public List<Ingredient> findByInBrandPkidAndInType(Integer inBrandPkid, IngredientType type);
	
	public List<Ingredient> findByInTypeNotInAndInBrandPkidNotNull(List<IngredientType> list);
	
	public List<Ingredient> findByInTypeAndInBrandPkidNotNullAndInCreateUserPkidIn(IngredientType type, List<Long> inCreateUserPkid);
	
	public List<Ingredient> findByInTypeAndInBrandPkidNotNullAndInDistribtorPkid(IngredientType type, Long inDistribtorPkid);
	
	public List<Ingredient> findByInTypeNotInAndInBrandPkidNotNullAndInSupplierGroupPkidIn(List<IngredientType> types, List<Long> inSupplierGroupPkid);
}

