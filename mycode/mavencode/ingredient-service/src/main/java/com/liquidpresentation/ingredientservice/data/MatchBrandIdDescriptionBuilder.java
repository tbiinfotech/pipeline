package com.liquidpresentation.ingredientservice.data;

import java.util.List;

import com.liquidpresentation.common.IngredientType;
import com.liquidpresentation.common.utils.StringUtil;
import com.liquidpresentation.ingredientservice.model.Ingredient;
import com.liquidpresentation.ingredientservice.model.Price;
import com.liquidpresentation.ingredientservice.services.BrandService;
import com.liquidpresentation.ingredientservice.services.IngredientService;
import com.liquidpresentation.ingredientservice.services.PriceService;

public class MatchBrandIdDescriptionBuilder extends DataBuilderAdaptor {

	private PriceService priceService;
	private IngredientService ingredientService;

	public MatchBrandIdDescriptionBuilder(PriceService priceService, IngredientService ingredientService, BrandService brandService){
		this.priceService = priceService;
		this.ingredientService = ingredientService;
	}
	
	/**
	 * 检索Distributor ID, DistributorBrandId 找到一条Ingredient匹配记录之后，
	 * 取得匹配记录的in_brand_pkid，保存到当前Ingredient对象中，
	 * 把当前Ingredient对象插入到数据库，生成一条新记录，
	 * 取得新Ingredient的主键，保存到当前Price对象，
	 * 把当前Price对象插入到数据库，生成一条新记录。
	 */
	@Override
	protected boolean matchAndBuildImportData(Price price) {
		boolean matched = false;
		
		boolean existsDistributorBrandId = StringUtil.isNotEmpty(price.getPrDistributorBrandId());
		
		if (existsDistributorBrandId) {
			List<Ingredient> ingredients = ingredientService.findByDistributorIdAndDistributorBrandId(price.getPrDistributorPkid(), price.getPrDistributorBrandId());
			if (!ingredients.isEmpty()) {
				long brandPkid = ingredients.get(0).getInBrandPkid();
				this.addNewIngredient(this.generateIngredient(price, brandPkid));
				this.priceService.savePrice(price);
				this.addNewPrice(price);
				
				matched = true;
			}
		}
		return matched;
	}

	private Ingredient generateIngredient(Price price, Long brandPkid) {
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
		ingredient.setInBrandPkid(brandPkid);

		ingredient.setInDistribtorPkid(price.getPrDistributorPkid());
		ingredient.setDistributorBrandId(price.getPrDistributorBrandId());

		ingredient.setDistSupplierId(price.getDistSupplierId());
		ingredient.setDistSupplierName(price.getCsvSupplierName());

		long ingredientId = ingredientService.saveIngredient(ingredient);
		price.setPrIngredientPkid(ingredientId);

		return ingredient;
	}
}
