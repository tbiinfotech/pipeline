package com.liquidpresentation.ingredientservice.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.liquidpresentation.ingredientservice.model.DistributorCategory;

@Repository
public interface DistributorCategoryRepository extends CrudRepository<DistributorCategory, Long> {
	public DistributorCategory findByDcPkidAndDcCategory(long dcPkid, String dcCategory);
	public boolean existsByDcPkidAndDcCategory(long dcPkid, String dcCategory);
}