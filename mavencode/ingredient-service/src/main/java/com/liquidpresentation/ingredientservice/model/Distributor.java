package com.liquidpresentation.ingredientservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "lp_distributor")
public class Distributor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pk_id", nullable = false)
	private int id;
	
	@Column(name = "ds_name")
	private String name;
	
	@Transient
	private String distributorItemCode;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDistributorItemCode() {
		return distributorItemCode;
	}

	public void setDistributorItemCode(String distributorItemCode) {
		this.distributorItemCode = distributorItemCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
