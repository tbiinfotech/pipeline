package com.liquidpresentaion.cocktailservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "lp_brand_group")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = BrandGroup.class)
public class BrandGroup {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pk_id", nullable = false)
	private int id;
	
	@Column(name = "bdg_brand_pkid", nullable = false)
	private long bdgBrandPkId;
	
	@Column(name = "bdg_group_pkid", nullable = false)
	private long bdgGroupPkId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getBdgBrandPkId() {
		return bdgBrandPkId;
	}

	public void setBdgBrandPkId(long bdgBrandPkId) {
		this.bdgBrandPkId = bdgBrandPkId;
	}

	public long getBdgGroupPkId() {
		return bdgGroupPkId;
	}

	public void setBdgGroupPkId(long bdgGroupPkId) {
		this.bdgGroupPkId = bdgGroupPkId;
	}
}
