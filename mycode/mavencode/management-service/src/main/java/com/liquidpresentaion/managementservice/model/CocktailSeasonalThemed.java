package com.liquidpresentaion.managementservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.liquidpresentaion.managementservice.constants.SeasonalThemed;

public class CocktailSeasonalThemed {

	private int id;
	
	private Cocktail cocktail;
	
	private SeasonalThemed seasonalThemed;

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

	public SeasonalThemed getSeasonalThemed() {
		return seasonalThemed;
	}

	public void setSeasonalThemed(SeasonalThemed seasonalThemed) {
		this.seasonalThemed = seasonalThemed;
	}
}
