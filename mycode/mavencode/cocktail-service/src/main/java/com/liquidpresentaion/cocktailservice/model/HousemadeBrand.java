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

import com.liquidpresentation.common.Category;

@Entity
@Table(name="lp_brand_housemade")
public class HousemadeBrand {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="pk_id")
	private long pkid;
	
	@Column(name="ih_housemade_ingredient_pkid")
	private long ihHousemadeIngredientPkId;
	
	@ManyToOne
	@JoinColumn(name = "ih_housemade_ingredient_pkid", insertable=false,updatable=false)
	private Ingredient ingredient;
	
	@Column(name="ih_brand_pkid")
	private long ihBrandPkId;
	
	@ManyToOne
	@JoinColumn(name = "ih_brand_pkid", insertable=false,updatable=false)
	private Brand inBrand;
	
	@Column(name="ih_Uom")
	private String ihUom;

	@Column(name = "ih_quantity")
	private double ihQuantity;
	
	@Column(name = "ih_index", nullable = true)
	private int index;
	
	/**
	 * Ingredient category
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "ih_category")
	private Category inCategory;
	
	public long getPkid() {
		return pkid;
	}

	public void setPkid(long pkid) {
		this.pkid = pkid;
	}

	public long getIhHousemadeIngredientPkId() {
		return ihHousemadeIngredientPkId;
	}

	public void setIhHousemadeIngredientPkId(long ihHousemadeIngredientPkId) {
		this.ihHousemadeIngredientPkId = ihHousemadeIngredientPkId;
	}

	public long getIhBrandPkId() {
		return ihBrandPkId;
	}

	public void setIhBrandPkId(long ihBrandPkId) {
		this.ihBrandPkId = ihBrandPkId;
	}

	public String getIhUom() {
		return ihUom;
	}

	public void setIhUom(String ihUom) {
		this.ihUom = ihUom;
	}

	public double getIhQuantity() {
		return ihQuantity;
	}

	public void setIhQuantity(double ihQuantity) {
		this.ihQuantity = ihQuantity;
	}

	public Category getInCategory() {
		return inCategory;
	}

	public void setInCategory(Category inCategory) {
		this.inCategory = inCategory;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Ingredient getIngredient() {
		return ingredient;
	}

	public void setIngredient(Ingredient ingredient) {
		this.ingredient = ingredient;
	}

	public Brand getInBrand() {
		return inBrand;
	}

	public void setInBrand(Brand inBrand) {
		this.inBrand = inBrand;
	}
}
