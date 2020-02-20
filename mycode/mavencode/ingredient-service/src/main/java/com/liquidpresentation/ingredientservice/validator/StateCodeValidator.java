package com.liquidpresentation.ingredientservice.validator;

import java.util.List;

import com.liquidpresentation.common.UspsState;
import com.liquidpresentation.common.utils.StringUtil;
import com.liquidpresentation.ingredientservice.model.Group;
import com.liquidpresentation.ingredientservice.model.Price;
import com.liquidpresentation.ingredientservice.services.GroupService;

public class StateCodeValidator extends PriceValidatorAdaptor implements PriceValidator {

	private String selectedStateCode;
	private GroupService groupService;

	public StateCodeValidator(String selectedStateCode, GroupService groupService){
		this.selectedStateCode = selectedStateCode;
		this.groupService = groupService;
	}

	private boolean isValid(String stateCode) {
		if (StringUtil.isAllEmpty(stateCode)) {
			return false;
		} else {
			for (UspsState code : UspsState.values()) {
				if (code.name().equalsIgnoreCase(stateCode)) {
					return true;
				}
			}
			return false;
		}
	}
	
	@Override
	public ValidationResult validate(Price price) {
		String stateCode = price.getPrState();
		if (isValid(stateCode)) {
			if (!stateCode.equalsIgnoreCase(selectedStateCode)) {
				return new ValidationResult(true, price, "State does not match the selected sales group");
			}
		} else {
			return new ValidationResult(true, price, "State is not valid");
		}
		
		if (price.getPrDistributorPkid() != null) {
			List<Group> groups = groupService.findByDistributorIdAndState(price.getPrDistributorPkid(), stateCode);
			if (!groups.isEmpty()) {
				Group stateGroup = groups.get(0);
				price.setOldSalesGroupPkid(price.getPrSalesGroupPkid());
				price.setPrSalesGroupPkid(stateGroup.getPkId());
			}
		}
		return this.resultSucess(price);
	}
}
