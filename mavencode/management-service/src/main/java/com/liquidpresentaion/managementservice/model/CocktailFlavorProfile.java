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
import com.liquidpresentaion.managementservice.constants.FlavorProfile;

public class CocktailFlavorProfile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pk_id", nullable = false)
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "cf_cocktail_pkid", nullable = false)
	private Cocktail cocktail;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "cf_flavorprofile")
	private FlavorProfile flavorProfile;

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

	public FlavorProfile getFlavorProfile() {
		return flavorProfile;
	}

	public void setFlavorProfile(FlavorProfile flavorProfile) {
		this.flavorProfile = flavorProfile;
	}
}
