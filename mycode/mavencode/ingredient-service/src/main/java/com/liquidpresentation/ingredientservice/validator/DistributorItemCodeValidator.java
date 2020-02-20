package com.liquidpresentation.ingredientservice.validator;

import com.liquidpresentation.common.utils.StringUtil;
import com.liquidpresentation.ingredientservice.model.Price;

public class DistributorItemCodeValidator extends PriceValidatorAdaptor implements PriceValidator {

	@Override
	public ValidationResult validate(Price price) {
		String itemCode = price.getPrDistribtorItemCode();
		
		if (StringUtil.isAllEmpty(itemCode)) {
			return new ValidationResult(true, price, "Distributor Item Code is not valid");
		}
		return this.resultSucess(price);
	}
}
