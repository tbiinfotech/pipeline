package com.liquidpresentation.ingredientservice.data;

import com.liquidpresentation.ingredientservice.model.Price;
import com.liquidpresentation.ingredientservice.services.PriceService;

public class MatchInvalidPriceValueBuilder extends DataBuilderAdaptor {

	private PriceService priceService;
	
	public MatchInvalidPriceValueBuilder(PriceService priceService){
		this.priceService = priceService;
	}
	
	@Override
	protected boolean matchAndBuildImportData(Price price) {
		if (price.getPrCasePrice() == null) {
			priceService.deleteByPrDistributorPkidAndPrDistribtorItemCodeAndPrSalesGroupPkidOrPrState(price.getPrDistributorPkid(), price.getPrDistribtorItemCode(), price.getPrSalesGroupPkid(), price.getPrState());
			this.addDeletePrice(price);
			return true;
		}
		return false;
	}
}
