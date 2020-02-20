package com.liquidpresentaion.cocktailservice.repository;




import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.liquidpresentaion.cocktailservice.model.HousemadeBrand;


@Repository
public interface HousemadeBrandRepository extends CrudRepository<HousemadeBrand, Long> {
	List<HousemadeBrand> findByIhHousemadeIngredientPkId(Long ihHousemadeIngredientPkId);
}

