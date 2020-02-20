package com.liquidpresentaion.users.model;

import java.text.DecimalFormat;

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
import com.liquidpresentation.common.Category;

@Entity
@Table(name = "lp_cocktail_brand")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = CocktailBrand.class)
public class CocktailBrand {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pk_id", nullable = false)
	private int id;

	@Column(name = "cb_brand_pkid", nullable = false)
	private Integer brandPkId;
	
	@Column(name = "cb_brand_name")
	private String brandName;
	
	@Column(name = "cb_quantity")
	private Double quantity;
	
	@Column(name = "cb_UOM")
	private String uom;
	
//	@ManyToOne
//	@JoinColumn(name = "cb_brand_pkid", insertable = false, updatable = false)
	@Transient
	private Brand brand;
	
	@Column(name = "cb_index", nullable = true)
	private int index;
	
	public String getQuantityString() {
		DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
		return decimalFormat.format(quantity);
	}

	public void setQuantityString(String quantityString) {
		this.quantityString = quantityString;
	}

	@Transient
	private String quantityString;
	@Transient
	private int isDeleted;
	
	/**
	 * Ingredient category
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "cb_category")
	private Category category;
	
	@ManyToOne
	@JoinColumn(name = "cb_cocktail_pkid", nullable = false)
	private Cocktail cocktail;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getBrandPkId() {
		return brandPkId;
	}

	public void setBrandPkId(Integer brandPkId) {
		this.brandPkId = brandPkId;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
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

	public Cocktail getCocktail() {
		return cocktail;
	}

	public void setCocktail(Cocktail cocktail) {
		this.cocktail = cocktail;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	public int getIsDeleted() {
		if (this.brand != null) {
			isDeleted = this.brand.getDeleted();
		}
		return isDeleted;
	}

	public void setIsDeleted(int isDeleted) {
		this.isDeleted = isDeleted;
	}
}
