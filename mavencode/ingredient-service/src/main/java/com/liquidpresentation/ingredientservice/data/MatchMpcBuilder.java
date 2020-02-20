package com.liquidpresentation.ingredientservice.data;

import java.util.Optional;

import com.liquidpresentation.common.IngredientType;
import com.liquidpresentation.ingredientservice.model.Brand;
import com.liquidpresentation.ingredientservice.model.Ingredient;
import com.liquidpresentation.ingredientservice.model.Price;
import com.liquidpresentation.ingredientservice.services.BrandService;
import com.liquidpresentation.ingredientservice.services.IngredientService;
import com.liquidpresentation.ingredientservice.services.PriceService;

public class MatchMpcBuilder extends DataBuilderAdaptor {
	
	private PriceService priceService;
	private IngredientService ingredientService;
	private BrandService brandService;

	public MatchMpcBuilder(PriceService priceService, IngredientService ingredientService, BrandService brandService){
		this.priceService = priceService;
		this.ingredientService = ingredientService;
		this.brandService = brandService;
	}
	
	@Override
	protected boolean matchAndBuildImportData(Price price) {
//		Optional<Brand> opt = this.brandService.findBrandByMpc(price.getPrMpc());
		Optional<Ingredient> opt = this.ingredientService.findByMpc(price.getPrMpc());
		if (opt.isPresent()) {
			price.setBrandPkid(opt.get().getInBrandPkid());
			this.addNewIngredient(generateIngredient(price));
			this.priceService.savePrice(price);
			this.addNewPrice(price);
		} else {
			this.addNewBrand(this.generatedBrand(price));
			this.addNewIngredient(this.generateIngredient(price));
			this.priceService.savePrice(price);
			this.addNewPrice(price);
		}
		return true;
	}
	
	private Brand generatedBrand(Price price){
		Brand brand = new Brand();
		brand.setBrName(price.getPrDistributorBrandDescription());
		brand.setDistributorBrandId(price.getPrDistributorBrandId());
		brand.setBrSupplierGroupPkid(price.getPrSupplierGroupPkid());
		brand.setBrReviewed(false);
		brand.setBrMpc(price.getPrMpc());
		brand.setBrBaseSpiritCategory(price.getPrBaseCategory());
		brand.setBrCategory(price.getPrCategory());
		
		long brandPkId = brandService.saveBrand(brand);
		brand.setPkId(brandPkId);
		price.setBrandPkid(brandPkId);
		return brand;
	}
	
	private Ingredient generateIngredient(Price price) {
		Ingredient ingredient = new Ingredient();
		ingredient.setInMpc(price.getPrMpc());
		ingredient.setInCategory(price.getPrCategory());
		ingredient.setInBaseSpriteCategory(price.getPrBaseCategory());
		ingredient.setInSupplierGroupPkid(price.getPrSupplierGroupPkid());
		ingredient.setInDistribtorItemCode(price.getPrDistribtorItemCode());
		ingredient.setInName(price.getPrDescription());
		ingredient.setInSize(price.getPrSize());
		ingredient.setInUom(price.getPrUom());
		ingredient.setInCasePack(price.getPrCasePack());
		ingredient.setInType(IngredientType.general);
		ingredient.setInMpc(price.getPrMpc());
		ingredient.setInBrandPkid(price.getBrandPkid());
		
		ingredient.setInDistribtorPkid(price.getPrDistributorPkid());
		ingredient.setDistributorBrandId(price.getPrDistributorBrandId());
		
		ingredient.setDistSupplierId(price.getDistSupplierId());
		ingredient.setDistSupplierName(price.getCsvSupplierName());
		
		long ingredientId = ingredientService.saveIngredient(ingredient);
		price.setPrIngredientPkid(ingredientId);
		
		return ingredient;
	}
}
