package com.liquidpresentation.ingredientservice.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.liquidpresentation.ingredientservice.model.HousemadeBrand;

@Repository
public interface HousemadeBrandRepository extends CrudRepository<HousemadeBrand, Long> {
	public List<HousemadeBrand> findByIhHousemadeIngredientPkIdOrderByIndex(long findByIhHousemadeIngredientPkId);
	public void deleteByIhHousemadeIngredientPkId(long findByIhHousemadeIngredientPkId);
	public List<HousemadeBrand> findByIhBrandPkId(long ihBrandPkId);
}