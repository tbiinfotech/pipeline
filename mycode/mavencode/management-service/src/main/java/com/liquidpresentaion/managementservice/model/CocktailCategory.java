package com.liquidpresentaion.managementservice.model;

import com.liquidpresentaion.managementservice.constants.Category;

public class CocktailCategory {

	private int id;

	private Cocktail cocktail;

	private Category category;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Cocktail getCocktail() {
		return cocktail;
	}

	public void setCocktail(Cocktail cocktail) {
		this.cocktail = cocktail;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
}
