package com.liquidpresentation.ingredientservice.data;

import com.liquidpresentation.ingredientservice.model.Price;
import com.liquidpresentation.ingredientservice.services.DistributorSupplierService;

public class MatchDistributorSupplierBuilder extends DataBuilderAdaptor {

	private DistributorSupplierService distributorSupplierService;
	
	public MatchDistributorSupplierBuilder(DistributorSupplierService distributorSupplierService){
		this.distributorSupplierService = distributorSupplierService;
	}
	
	@Override
	protected boolean matchAndBuildImportData(Price price) {
		if (!distributorSupplierService.existsByDistributorPkidAndDistSupplierId(price)) {
			this.addBadPrice(price);
		}
		
		//returns false all the time.  supplier mapping error does not stop the import but rather output the error message only.
		return false;
	}
}
