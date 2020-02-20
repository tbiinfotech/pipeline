package com.liquidpresentation.ingredientservice.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liquidpresentation.ingredientservice.model.Brand;
import com.liquidpresentation.ingredientservice.model.Ingredient;
import com.liquidpresentation.ingredientservice.model.Price;

import static com.liquidpresentation.ingredientservice.data.DataKey.*;

public abstract class DataBuilderAdaptor implements DataBuilder {

	private List<Price> newPriceList = new LinkedList<Price>();
	private List<Price> updatePriceList = new LinkedList<Price>();
	private List<Price> deletePriceList = new LinkedList<Price>();
	private List<Price> badPriceList = new LinkedList<Price>();
	private List<Ingredient> newIngredientList = new LinkedList<>();
	private List<Brand> newBrandList = new LinkedList<>();

	private DataBuilder nextBuilder = null;
	
    private static final Logger logger = LoggerFactory.getLogger(DataBuilderAdaptor.class);
    

	protected abstract boolean matchAndBuildImportData(Price price);

	@Override
	public Map<DataKey, List> collect() {
		if (hasNext()) {
			Map<DataKey, List> nextDataMap = next().collect();
			newPriceList.addAll(nextDataMap.get(NEWPRICE));
			updatePriceList.addAll(nextDataMap.get(UPDATEPRICE));
			deletePriceList.addAll(nextDataMap.get(DELETEPRICE));
			badPriceList.addAll(nextDataMap.get(BADPRICE));
			newIngredientList.addAll(nextDataMap.get(NEWINGREDIENT));
			newBrandList.addAll(nextDataMap.get(NEWBRAND));
		}
		
		Map<DataKey, List> dataMap = new HashMap<>();
		dataMap.put(NEWPRICE, newPriceList);
		dataMap.put(UPDATEPRICE, updatePriceList);
		dataMap.put(DELETEPRICE, deletePriceList);
		dataMap.put(BADPRICE, badPriceList);
		dataMap.put(NEWINGREDIENT, newIngredientList);
		dataMap.put(NEWBRAND, newBrandList);
		return dataMap;
	}

	@Override
	public void setNext(DataBuilder nextBuilder) {
		if (this.nextBuilder == null) {
			this.nextBuilder = nextBuilder;
		} else {
			this.nextBuilder.setNext(nextBuilder);
		}
	}

	@Override
	public boolean hasNext() {
		return this.nextBuilder != null;
	}

	@Override
	public DataBuilder next() {
		return this.nextBuilder;
	}
	
	@Override
	public boolean build(Price price) {
		boolean built = false;
		if (matchAndBuildImportData(price)) {
			built = true;
			logger.info("Match [" + this.getClass().getSimpleName() + "] - Price Data:[" + price.toString() + "]");
		} else if(hasNext()) {
			//logger.info("Bypass [" + this.getClass().getSimpleName() + "]");
			built = this.next().build(price);
		}
		return built;
	}

	protected void addNewPrice(Price price) {
		this.newPriceList.add(price);
	}

	protected void addUpdatePrice(Price price) {
		this.updatePriceList.add(price);
	}

	protected void addDeletePrice(Price price) {
		this.deletePriceList.add(price);
	}

	protected void addBadPrice(Price price) {
		this.badPriceList.add(price);
	}

	protected void addNewIngredient(Ingredient ingredient) {
		this.newIngredientList.add(ingredient);
	}

	protected void addNewBrand(Brand brand) {
		this.newBrandList.add(brand);
	}

	protected List<Price> getNewPriceList() {
		return newPriceList;
	}

	protected List<Price> getUpdatePriceList() {
		return updatePriceList;
	}

	protected List<Price> getDeletePriceList() {
		return deletePriceList;
	}

	protected List<Price> getBadPriceList() {
		return badPriceList;
	}

	protected List<Ingredient> getNewIngredientList() {
		return newIngredientList;
	}

	protected List<Brand> getNewBrandList() {
		return newBrandList;
	}
}
