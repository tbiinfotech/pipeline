package com.liquidpresentation.ingredientservice.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.liquidpresentation.common.utils.StringUtil;
import com.liquidpresentation.ingredientservice.model.Price;

public class MpcValidator extends PriceValidatorAdaptor implements PriceValidator {

	@Override
	public ValidationResult validate(Price price) {
		String mpcString = price.getPrMpc();
		
		if (StringUtil.isAllEmpty(mpcString)) {
			return new ValidationResult(true, price, "GTIN is not valid");
		} else {
			Pattern pattern = Pattern.compile("[1-9]+");
            Matcher matcher = pattern.matcher(mpcString);
            if (!matcher.find()) {
            	return new ValidationResult(true, price, "GTIN is not valid");
			}
		}
		return this.resultSucess(price);
	}
}
