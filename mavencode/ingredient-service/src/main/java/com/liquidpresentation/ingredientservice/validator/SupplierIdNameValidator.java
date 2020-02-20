package com.liquidpresentation.ingredientservice.validator;

import com.liquidpresentation.common.utils.StringUtil;
import com.liquidpresentation.ingredientservice.model.Price;

public class SupplierIdNameValidator extends PriceValidatorAdaptor implements PriceValidator {

	@Override
	public ValidationResult validate(Price price) {
		String supplierName = price.getPrSupplierName();
		String supplierId = price.getDistSupplierId();
		if (StringUtil.isAllEmpty(supplierName)) {
			return new ValidationResult(true, price, "SupplierName is Blank");
		}
		if (StringUtil.isAllEmpty(supplierId)) {
			return new ValidationResult(true, price, "DistributorSupplierId is Blank");
		}
		return this.resultSucess(price);
	}
}
