package com.liquidpresentation.ingredientservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="lp_suppliers_distributors")
public class DistributorSupplier {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="pk_id")
	private long pkId;
	
	@Column(name="sd_distributor_pkid")
	private long distributorPkid;
	
	@Column(name="sd_supplier_group_pkid")
	private long supplierGroupPkid;
	
	@Column(name="sd_dist_supplier_id")
	private String distSupplierId;
	
	@Column(name="sd_dist_supplier_name")
	private String distSupplierName;

	public long getPkId() {
		return pkId;
	}

	public void setPkId(long pkId) {
		this.pkId = pkId;
	}

	public long getDistributorPkid() {
		return distributorPkid;
	}

	public void setDistributorPkid(long distributorPkid) {
		this.distributorPkid = distributorPkid;
	}

	public long getSupplierGroupPkid() {
		return supplierGroupPkid;
	}

	public void setSupplierGroupPkid(long supplierGroupPkid) {
		this.supplierGroupPkid = supplierGroupPkid;
	}

	public String getDistSupplierId() {
		return distSupplierId;
	}

	public void setDistSupplierId(String distSupplierId) {
		this.distSupplierId = distSupplierId;
	}

	public String getDistSupplierName() {
		return distSupplierName;
	}

	public void setDistSupplierName(String distSupplierName) {
		this.distSupplierName = distSupplierName;
	}
}
