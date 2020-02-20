package com.liquidpresentation.ingredientservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.liquidpresentation.common.Category;

@Entity
@Table(name = "lp_cocktail_brand")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = CocktailBrand.class)
public class CocktailBrand {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pk_id", nullable = false)
	private int id;

	@Column(name = "cb_cocktail_pkid", nullable = false)
	private long cocktailPkId;
	
	@Column(name = "cb_brand_pkid", nullable = false)
	private long brandPkId;
	
	@Column(name = "cb_brand_name")
	private String brandName;
	
	@Column(name = "cb_quantity")
	private double quantity;
	
	@Column(name = "cb_UOM")
	private String uom;
	
	/**
	 * Ingredient category
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "cb_category")
	private Category category;

	@Column(name = "cb_index", nullable = true)
	private int index;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getCocktailPkId() {
		return cocktailPkId;
	}

	public void setCocktailPkId(long cocktailPkId) {
		this.cocktailPkId = cocktailPkId;
	}

	public long getBrandPkId() {
		return brandPkId;
	}

	public void setBrandPkId(long brandPkId) {
		this.brandPkId = brandPkId;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
