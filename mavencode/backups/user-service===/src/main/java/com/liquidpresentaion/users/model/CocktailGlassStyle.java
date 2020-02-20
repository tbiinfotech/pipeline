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
@Table(name = "lp_cocktail_glassstyle")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = CocktailGlassStyle.class)
public class CocktailGlassStyle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pk_id", nullable = false)
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "cg_cocktail_pkid", nullable = false)
	private Cocktail cocktail;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "cg_glassstyle")
	private GlassStyle glassStyle;
	
	@Transient
	private String glassStyleString;
	
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

	public GlassStyle getGlassStyle() {
		return glassStyle;
	}

	public void setGlassStyle(GlassStyle glassStyle) {
		this.glassStyle = glassStyle;
	}

	public String getGlassStyleString() {
		return this.glassStyle == null ? null : this.glassStyle.toString();
	}

	public void setGlassStyleString(String glassStyleString) {
		this.glassStyleString = glassStyleString;
	}
}
