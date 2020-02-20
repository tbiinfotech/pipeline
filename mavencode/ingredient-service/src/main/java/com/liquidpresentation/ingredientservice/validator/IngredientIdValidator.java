package com.liquidpresentation.ingredientservice.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liquidpresentation.ingredientservice.model.Price;
import com.liquidpresentation.ingredientservice.services.IngredientService;

public class IngredientIdValidator extends PriceValidatorAdaptor implements PriceValidator {

	private long ID_MAX = 4294967295L;
	private long ID_MIN = 0L;
	private IngredientService ingredientService;
	
    private static final Logger logger = LoggerFactory.getLogger(IngredientIdValidator.class);

	public IngredientIdValidator(IngredientService ingredientService){
		this.ingredientService = ingredientService;
	}

	@Override
	public ValidationResult validate(Price price) {
		
		Long pkId = price.getPrIngredientPkid();
		if (pkId != null) {
			if (pkId < ID_MIN || pkId > ID_MAX) {
//				logger.inf
				return new ValidationResult(true, price, "IngredientId is not valid.");
			}
			if (!ingredientService.existsByPkId(pkId)) {
				return new ValidationResult(true, price, "IngredientId is not valid.");
			}
		}
		return this.resultSucess(price);
	}
}
