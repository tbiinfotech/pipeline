package com.liquidpresentation.ingredientservice.validator;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

import com.liquidpresentation.common.utils.StringUtil;
import com.liquidpresentation.ingredientservice.model.Price;

public class SizeUomValidator extends PriceValidatorAdaptor implements PriceValidator {

	@Override
	public ValidationResult validate(Price price) {
		String csvPrSize = price.getCsvPrSize().replaceAll(" ", "");
		price.setCsvPrUom(price.getPrUom());
		if (StringUtils.isEmpty(csvPrSize)) {
			return new ValidationResult(true, price, " Size is Blank");
		}
		if(!csvPrSize.matches("[0-9]+([.]?[0-9]*)([A-Za-z]+)*$")) {
			return new ValidationResult(true, price, " Size is not valid");
		}
		if (StringUtil.isNumber(csvPrSize)) {
			price.setPrSize(new BigDecimal(csvPrSize));
		} else {
			price.setPrSize(new BigDecimal(StringUtil.getNumbers(csvPrSize)));
			price.setPrUom(StringUtil.splitNotNumber(csvPrSize));
		}
		if (StringUtils.isNotEmpty(price.getPrUom())) {
			String uom = price.getPrUom();
			switch (uom.toUpperCase()) {
				case "Z":
					price.setPrUom("OZ");
					break;
				case "L":
					price.setPrUom("LTR");
					break;
				case "G":
					price.setPrUom("Gal");
					break;
				case "LB":
					price.setPrUom("Lbs");
					break;
				default:
					break;
			}
		}
		return this.resultSucess(price);
	}

}
