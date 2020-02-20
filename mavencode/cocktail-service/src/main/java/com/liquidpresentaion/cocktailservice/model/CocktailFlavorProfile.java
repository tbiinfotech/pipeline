package com.liquidpresentaion.cocktailservice.model;

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
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.liquidpresentaion.cocktailservice.constants.FlavorProfile;

@Entity
@Table(name = "lp_cocktail_flavorprofile")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = CocktailFlavorProfile.class)
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
	
	@Transient
	private String flavorProfileString;
	
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

	public String getFlavorProfileString() {
		return this.flavorProfile == null ? null : this.flavorProfile.toString();
	}

	public void setFlavorProfileString(String flavorProfileString) {
		this.flavorProfileString = flavorProfileString;
	}
}
