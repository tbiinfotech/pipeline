package com.liquidpresentation.ingredientservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.liquidpresentation.common.BaseSpiritCategory;
import com.liquidpresentation.common.Category;

@Entity
@Table(name="lp_distributor_category")
public class DistributorCategory {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="pk_id")
	private long pkId;
	
	@Column(name="dc_distributor_pkid")
	private long dcPkid;
	
	@Column(name="dc_distributor_category")
	private String dcCategory;
	
	@Enumerated(EnumType.STRING)
	@Column(name="dc_ingredient_category")
	private Category dcIngredientCategory;
	
	@Enumerated(EnumType.STRING)
	@Column(name="dc_base_spirit_category")
	private BaseSpiritCategory dcBaseSpiritCategory;

	public long getPkId() {
		return pkId;
	}

	public void setPkId(long pkId) {
		this.pkId = pkId;
	}

	public long getDcPkid() {
		return dcPkid;
	}

	public void setDcPkid(long dcPkid) {
		this.dcPkid = dcPkid;
	}

	public String getDcCategory() {
		return dcCategory;
	}

	public void setDcCategory(String dcCategory) {
		this.dcCategory = dcCategory;
	}

	public Category getDcIngredientCategory() {
		return dcIngredientCategory;
	}

	public void setDcIngredientCategory(Category dcIngredientCategory) {
		this.dcIngredientCategory = dcIngredientCategory;
	}

	public BaseSpiritCategory getDcBaseSpiritCategory() {
		return dcBaseSpiritCategory;
	}

	public void setDcBaseSpiritCategory(BaseSpiritCategory dcBaseSpiritCategory) {
		this.dcBaseSpiritCategory = dcBaseSpiritCategory;
	}
}
