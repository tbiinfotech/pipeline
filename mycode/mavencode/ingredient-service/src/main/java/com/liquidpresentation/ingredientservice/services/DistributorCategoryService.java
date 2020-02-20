package com.liquidpresentation.ingredientservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.liquidpresentation.ingredientservice.model.DistributorCategory;
import com.liquidpresentation.ingredientservice.repository.DistributorCategoryRepository;

@Service
public class DistributorCategoryService {
	
	@Autowired
	private DistributorCategoryRepository distributorCategoryRepository;
	
	public DistributorCategory getDistributorCategory(long dcPkid, String dcCategory) {
		return distributorCategoryRepository.findByDcPkidAndDcCategory(dcPkid, dcCategory);
	}

	public boolean existsDistributorCategory(long dcPkid, String dcCategory) {
		return distributorCategoryRepository.existsByDcPkidAndDcCategory(dcPkid, dcCategory);
	}
}
