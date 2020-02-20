package com.liquidpresentation.ingredientservice.validator;

import com.liquidpresentation.ingredientservice.model.Price;
import com.liquidpresentation.ingredientservice.services.GroupService;
import org.apache.commons.lang.StringUtils;

public class DistributorIdValidator extends PriceValidatorAdaptor implements PriceValidator {

	private GroupService groupService;
	private long pageGroupId;

	public DistributorIdValidator(long pageGroupId, GroupService groupService){
		this.groupService = groupService;
		this.pageGroupId = pageGroupId;
	}

	/**
	 *  Value must NOT be null or blank;
	 *  DistributorID must match the DistributorID of the selected sales group on the upload page;
	 *  If SalesGroupId and the distributorID are both present in the same import record, the DistributorID must match the DistributorID of the SalesGroupId;
	 *  A Sales Group with such Distributor ID attached to must exist in the database;
	 */
	@Override
	public ValidationResult validate(Price price) {
		Long distributorPkId = null;
		try {
			if (StringUtils.isNotEmpty(price.getCsvPrDistributorPkid())) {
				distributorPkId = Long.valueOf(price.getCsvPrDistributorPkid());
				price.setPrDistributorPkid(distributorPkId);
			}
		} catch (Exception e) {
			return new ValidationResult(true, price, "DistributorId is not valid");
		}
		if (distributorPkId == null) {
			return new ValidationResult(true, price, "DistributorId is not valid");
		} else {
			long pageDistributorPkid = this.groupService.findDistributorPkid(pageGroupId);
			if (distributorPkId != pageDistributorPkid) {
				return new ValidationResult(true, price, "DistributorId is not valid");
			}
			
			Long importSalesGroupPkid = price.getPrSalesGroupPkid();
			if (importSalesGroupPkid != null) {
				long salesGroupDistributorPkid = this.groupService.findDistributorPkid(importSalesGroupPkid);
				if (distributorPkId != salesGroupDistributorPkid) {
					return new ValidationResult(true, price, "DistributorId is not valid");
				}
			}
			
			if (!groupService.existsByDistributorPkId(distributorPkId)) {
				return new ValidationResult(true, price, "Distributor ID: [" + distributorPkId + "] has no assigned Sales Group!");
			}
		}
		return this.resultSucess(price);
	}
}
