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
@Table(name = "lp_cocktail_outlettype")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = CocktailOutletType.class)
public class CocktailOutletType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pk_id", nullable = false)
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "co_cocktail_pkid", nullable = false)
	private Cocktail cocktail;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "co_outlettype")
	private OutletType outletType;
	
	@Transient
	private String outletTypeString;
	
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

	public OutletType getOutletType() {
		return outletType;
	}

	public void setOutletType(OutletType outletType) {
		this.outletType = outletType;
	}

	public String getOutletTypeString() {
		return this.outletType == null ? null : this.outletType.toString();
	}

	public void setOutletTypeString(String outletTypeString) {
		this.outletTypeString = outletTypeString;
	}
}
