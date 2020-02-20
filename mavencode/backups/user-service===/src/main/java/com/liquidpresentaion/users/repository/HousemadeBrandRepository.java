package com.liquidpresentaion.users.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.liquidpresentaion.users.model.HousemadeBrand;

@Repository
public interface HousemadeBrandRepository extends CrudRepository<HousemadeBrand, Long> {
	public List<HousemadeBrand> findByIhHousemadeIngredientPkId(long findByIhHousemadeIngredientPkId);
}