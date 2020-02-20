package com.liquidpresentation.ingredientservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

@Entity
@Table(name = "lp_group_sales_supplier")
@JsonIdentityInfo(generator=JSOGGenerator.class)
public class GroupSalesSupplier {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pk_id", nullable = false)
	private long pkId;

	@Column(name = "gss_sales_group_pkid", nullable = false)
	private long salesGroupId;
	
	@Column(name = "gss_supplier_group_pkid", nullable = false)
	private long supplierGroupId;

	public long getPkId() {
		return pkId;
	}

	public void setPkId(long pkId) {
		this.pkId = pkId;
	}

	public long getSalesGroupId() {
		return salesGroupId;
	}

	public void setSalesGroupId(long salesGroupId) {
		this.salesGroupId = salesGroupId;
	}

	public long getSupplierGroupId() {
		return supplierGroupId;
	}

	public void setSupplierGroupId(long supplierGroupId) {
		this.supplierGroupId = supplierGroupId;
	}
	
}
