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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.liquidpresentation.common.BaseSpiritCategory;
import com.liquidpresentation.common.Category;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

@Entity
@Table(name = "lp_distributor_category")
@JsonIdentityInfo(generator=JSOGGenerator.class)
public class DistributorCategory {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pk_id", nullable = false)
	private int id;

	@ManyToOne
	@JoinColumn(name = "dc_distributor_pkid", nullable = false)
	private Distributor distributor;
	
	@Column(name = "dc_distributor_category")
	private String distributorCategory;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "dc_ingredient_category")
	private Category ingredientCategory;

	@Enumerated(EnumType.STRING)
	@Column(name = "dc_base_spirit_category")
	private BaseSpiritCategory baseSpiritCategory;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Distributor getDistributor() {
		return distributor;
	}

	public void setDistributor(Distributor distributor) {
		this.distributor = distributor;
	}

	public String getDistributorCategory() {
		return distributorCategory;
	}

	public void setDistributorCategory(String distributorCategory) {
		this.distributorCategory = distributorCategory;
	}

	public Category getIngredientCategory() {
		return ingredientCategory;
	}

	public void setIngredientCategory(Category ingredientCategory) {
		this.ingredientCategory = ingredientCategory;
	}

	public BaseSpiritCategory getBaseSpiritCategory() {
		return baseSpiritCategory;
	}

	public void setBaseSpiritCategory(BaseSpiritCategory baseSpiritCategory) {
		this.baseSpiritCategory = baseSpiritCategory;
	}
}
