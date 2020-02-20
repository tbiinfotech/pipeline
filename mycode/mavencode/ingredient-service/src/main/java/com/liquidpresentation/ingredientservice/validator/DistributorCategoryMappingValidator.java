package com.liquidpresentation.ingredientservice.validator;

import com.liquidpresentation.ingredientservice.model.DistributorCategory;
import com.liquidpresentation.ingredientservice.model.Price;
import com.liquidpresentation.ingredientservice.services.DistributorCategoryService;

public class DistributorCategoryMappingValidator extends PriceValidatorAdaptor implements PriceValidator {

	private DistributorCategoryService distributorCategoryService;

	public DistributorCategoryMappingValidator(DistributorCategoryService distributorCategoryService){
		this.distributorCategoryService = distributorCategoryService;
	}

	@Override
	public ValidationResult validate(Price price) {
		if (price.getPrDistributorPkid() != null) {
			if (!distributorCategoryService.existsDistributorCategory(price.getPrDistributorPkid(), price.getDcCategory())) {
				return new ValidationResult(true, price, "No mapping found for distributor [" + price.getPrDistributorPkid() + "] category [" + price.getDcCategory() + "]");
			} else {
				DistributorCategory dc = distributorCategoryService.getDistributorCategory(price.getPrDistributorPkid(), price.getDcCategory());
				price.setPrCategory(dc.getDcIngredientCategory());
				price.setPrBaseCategory(dc.getDcBaseSpiritCategory());
			}
		}
		return this.resultSucess(price);
	}
}
