package com.liquidpresentation.ingredientservice.validator;


import com.liquidpresentation.common.GroupType;
import com.liquidpresentation.ingredientservice.model.Price;
import com.liquidpresentation.ingredientservice.services.GroupService;
import org.apache.commons.lang.StringUtils;

public class SalesGroupIdValidator extends PriceValidatorAdaptor implements PriceValidator {

			
	private GroupService groupService;

	public SalesGroupIdValidator(GroupService groupService){
		this.groupService = groupService;
	}

	@Override
	public ValidationResult validate(Price price) {
		Long salesGroupId = null;
		try {
			if (StringUtils.isNotEmpty(price.getCsvPrSalesGroupPkid())) {
				salesGroupId = Long.valueOf(price.getCsvPrSalesGroupPkid());
				price.setPrSalesGroupPkid(salesGroupId);
			}
		} catch (Exception e) {
			return new ValidationResult(true, price, "SalesGroupId is not valid");
		}
		price.setOldSalesGroupPkid(salesGroupId);
		if (salesGroupId != null) {
			if (!groupService.existsByIdAndType(salesGroupId, GroupType.sales)) {
				return new ValidationResult(true, price, "SalesGroupId is not valid");
			}
		}
		return this.resultSucess(price);
	}
}
