package com.liquidpresentation.ingredientservice.validator;

import com.liquidpresentation.common.utils.StringUtil;
import com.liquidpresentation.ingredientservice.model.Price;

public class DescriptionValidator extends PriceValidatorAdaptor implements PriceValidator {

	@Override
	public ValidationResult validate(Price price) {
		if (StringUtil.isAllEmpty(price.getPrDescription())) {
			return new ValidationResult(true, price, "Description must not be null or blank!!");
		}
		return this.resultSucess(price);
	}
}
