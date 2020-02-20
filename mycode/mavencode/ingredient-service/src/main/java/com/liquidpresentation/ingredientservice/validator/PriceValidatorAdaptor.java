package com.liquidpresentation.ingredientservice.validator;

import com.liquidpresentation.ingredientservice.model.Price;

public abstract class PriceValidatorAdaptor implements PriceValidator {

	protected ValidationResult resultSucess(Price price){
		return new ValidationResult(false, price, "Price is valid!");
	}
}
