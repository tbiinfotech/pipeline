package com.liquidpresentation.ingredientservice.data;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liquidpresentation.ingredientservice.model.Brand;
import com.liquidpresentation.ingredientservice.model.Ingredient;
import com.liquidpresentation.ingredientservice.model.Price;
import com.liquidpresentation.ingredientservice.services.BrandService;
import com.liquidpresentation.ingredientservice.services.IngredientService;
import com.liquidpresentation.ingredientservice.services.PriceService;

public class MatchDistributorItemCodeBuilder extends DataBuilderAdaptor {

	private PriceService priceService;
	private IngredientService ingredientService;
	private BrandService brandService;
	
    private static final Logger logger = LoggerFactory.getLogger(MatchDistributorItemCodeBuilder.class);
    		
    public MatchDistributorItemCodeBuilder(PriceService priceService, IngredientService ingredientService, BrandService brandService){
		this.priceService = priceService;
		this.ingredientService = ingredientService;
		this.brandService = brandService;
	}
	
	@Override
	protected boolean matchAndBuildImportData(Price price) {
		boolean matched = false;
		
		Optional<Price> opt = priceService.findByPrDistributorPkidAndPrDistribtorItemCodeAndPrSalesGroupPkidOrPrState(price.getPrDistributorPkid(), price.getPrDistribtorItemCode(), price.getPrSalesGroupPkid(), price.getPrState());
		if (opt.isPresent()) {
			Price updatePrice = opt.get();
			updatePrice.setPrCasePrice(price.getPrCasePrice());
			updatePrice.setPrSupplierGroupPkid(price.getPrSupplierGroupPkid());
			this.priceService.updatePrice(updatePrice);
			logger.info("Update Price: [" + updatePrice.toString() + "]");
			this.ingredientService.updateIngredientDistSupplierId(updatePrice.getPrIngredientPkid(), price.getPrSupplierGroupPkid(), price.getDistSupplierId(), price.getPrSupplierName());
			Brand brand;
			if (updatePrice.getIngredient() == null && updatePrice.getPrIngredientPkid() != null) {
				Ingredient ingredient = ingredientService.getIngredient(updatePrice.getPrIngredientPkid());
				brand = ingredient.getBrand();
			} else {
				brand = updatePrice.getIngredient().getBrand();
			}
			if(brand != null) {
				brand.setBrSupplierGroupPkid(price.getPrSupplierGroupPkid());
				brandService.updateBrand(brand);
			}
			this.addUpdatePrice(updatePrice);
			matched = true;
		} else  {
			Optional<Ingredient> ingredientOptional = ingredientService.findByInDistribtorPkidAndInDistribtorItemCodeOrPkId(price.getPrDistributorPkid(), price.getPrDistribtorItemCode(), price.getPrIngredientPkid());
			if (ingredientOptional.isPresent()) {
				price.setPrIngredientPkid(ingredientOptional.get().getPkId());
				this.priceService.savePrice(price);
				this.ingredientService.updateIngredientDistSupplierId(ingredientOptional.get().getPkId(), price.getPrSupplierGroupPkid(), price.getDistSupplierId(), price.getPrSupplierName());
				Brand brand = ingredientOptional.get().getBrand();
				brand.setBrSupplierGroupPkid(price.getPrSupplierGroupPkid());
				brandService.updateBrand(brand);
				this.addNewPrice(price);
				matched = true;
			}
		}
		
		return matched;
	}
}
