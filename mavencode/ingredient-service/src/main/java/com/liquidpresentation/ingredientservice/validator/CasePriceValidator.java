package com.liquidpresentation.ingredientservice.validator;

import com.liquidpresentation.ingredientservice.model.Price;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;

public class CasePriceValidator extends PriceValidatorAdaptor implements PriceValidator {

	@Override
	public ValidationResult validate(Price price) {
		try {
			if (StringUtils.isEmpty(price.getCsvPrCasePrice())) {
				return new ValidationResult(true, price, " Case Price is Blank");
			}
			String csvCasePrice = price.getCsvPrCasePrice().replace(",", "");
			Double.valueOf(csvCasePrice);
			price.setPrCasePrice(new BigDecimal(csvCasePrice));
		} catch (Exception e) {
			return new ValidationResult(true, price, " Case Price is not valid");
		}
		if (price.getPrCasePrice().doubleValue() <= 0) {
			return new ValidationResult(true, price, " Case Price is not valid");
		}
		return this.resultSucess(price);
	}
}
