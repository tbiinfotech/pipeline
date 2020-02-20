package com.liquidpresentaion.cocktailservice.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.liquidpresentation.common.Category;

public class ExcelBrand implements Comparable<ExcelBrand>{

	private int id;
	private Category brandCategory;
	private int row;
	private String description;
	
	private List<Price> prices;
	private List<HousemadeBrand> housemadeBrands;
	private Set<String> uomUsedInCocktails = new HashSet<>();
	
	public ExcelBrand(){
		
	}
	
	public ExcelBrand(int id, Price price){
		this.id = id;
		this.prices = new ArrayList<>();
		this.prices.add(price);
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public List<Price> getPrices() {
		if(prices == null) {
			prices = new ArrayList<Price>();
		}
		return prices;
	}
	
	public void setPrices(List<Price> prices) {
		this.prices = prices;
	}
	
	public void addPrice(Price price){
		this.prices.add(price);
	}
	
	public Category getBrandCategory() {
		return brandCategory;
	}
	
	public void setBrandCategory(Category brandCategory) {
		this.brandCategory = brandCategory;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}
	
	private int categoryAsInt() {
		int ret;
		switch (this.brandCategory) {
			case BASESPIRIT:
				ret = 0;
				break;
			case JUICE:
				ret = 1;
				break;
			case SWEETENER:
				ret = 2;
				break;
			case SOLIDS:
				ret = 3;
				break;
			case GARNISH:
				ret = 4;
				break;
			default:
				ret = 5;
		}
		return ret;
	}

	@Override
	public int compareTo(ExcelBrand o) {
		return this.categoryAsInt() - o.categoryAsInt();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void addHousemadeBrand(HousemadeBrand housemadeBrand){
		this.housemadeBrands.add(housemadeBrand);
	}

	public List<HousemadeBrand> getHousemadeBrands() {
		return housemadeBrands;
	}

	public void setHousemadeBrands(List<HousemadeBrand> housemadeBrands) {
		this.housemadeBrands = housemadeBrands;
	}

	public void addUom(String uom) {
		this.uomUsedInCocktails.add(uom);
	}
	
	public boolean hasUnitInCocktail() {
		return this.uomUsedInCocktails.contains("UNIT");
	}
}
