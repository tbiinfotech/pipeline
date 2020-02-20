package com.liquidpresentaion.users.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.liquidpresentation.common.Category;

@Entity
@Table(name="lp_brand_housemade")
public class HousemadeBrand {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="pk_id")
	private long pkid;
	
	@Column(name="ih_housemade_ingredient_pkid")
	private Long ihHousemadeIngredientPkId;
	
	@Column(name="ih_brand_pkid")
	private Long ihBrandPkId;
	
	@Column(name="ih_Uom")
	private String ihUom;

	@Column(name = "ih_quantity")
	private Double ihQuantity;
	
	@Column(name = "ih_index", nullable = true)
	private Integer index;
	/**
	 * Ingredient category
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "ih_category")
	private Category inCategory;
	
	@Transient
	private String brandName;
	
	@Transient
	private int deletedBrand;
	
	public int getDeletedBrand() {
		return deletedBrand;
	}

	public void setDeletedBrand(int deletedBrand) {
		this.deletedBrand = deletedBrand;
	}

	public String getBrandName() {
		return brandName;
	}
	
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	
	public long getPkid() {
		return pkid;
	}

	public void setPkid(long pkid) {
		this.pkid = pkid;
	}

	public Long getIhHousemadeIngredientPkId() {
		return ihHousemadeIngredientPkId;
	}

	public void setIhHousemadeIngredientPkId(Long ihHousemadeIngredientPkId) {
		this.ihHousemadeIngredientPkId = ihHousemadeIngredientPkId;
	}

	public Long getIhBrandPkId() {
		return ihBrandPkId;
	}

	public void setIhBrandPkId(Long ihBrandPkId) {
		this.ihBrandPkId = ihBrandPkId;
	}

	public String getIhUom() {
		return ihUom;
	}

	public void setIhUom(String ihUom) {
		this.ihUom = ihUom;
	}

	public Double getIhQuantity() {
		return ihQuantity;
	}

	public void setIhQuantity(Double ihQuantity) {
		this.ihQuantity = ihQuantity;
	}

	public Category getInCategory() {
		return inCategory;
	}

	public void setInCategory(Category inCategory) {
		this.inCategory = inCategory;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}
}
