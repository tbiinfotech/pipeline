package com.liquidpresentaion.users.model;

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

@Entity
@Table(name = "lp_cocktail_seasonalthemed")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = CocktailSeasonalThemed.class)
public class CocktailSeasonalThemed {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pk_id", nullable = false)
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "cs_cocktail_pkid", nullable = false)
	private Cocktail cocktail;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "cs_seasonalthemed")
	private SeasonalThemed seasonalThemed;
	
	@Transient
	private String seasonalThemedString;
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

	public String getSeasonalThemedString() {
		return this.seasonalThemed == null ? null : this.seasonalThemed.toString();
	}

	public void setSeasonalThemedString(String seasonalThemedString) {
		this.seasonalThemedString = seasonalThemedString;
	}
}
