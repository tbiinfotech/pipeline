package com.liquidpresentation.ingredientservice.validator;

import com.liquidpresentation.ingredientservice.model.Price;

public interface PriceValidator {

	ValidationResult validate(Price price);
}
