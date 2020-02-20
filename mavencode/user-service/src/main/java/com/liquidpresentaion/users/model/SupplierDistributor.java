package com.liquidpresentaion.users.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="lp_suppliers_distributors")
public class SupplierDistributor {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="pk_id")
	private int pkId;
	
	@Column(name="sd_supplier_group_pkid")
	private Integer supplierGroupPkId;
	
	@Column(name="sd_distributor_pkid")
	private Integer distributorPkId;
	
	@Column(name="sd_dist_supplier_id")
	private String distSupplierId;
	
	@Column(name="sd_dist_supplier_name")
	private String distSupplierName;
	
	@ManyToOne
	@JoinColumn(name = "sd_supplier_group_pkid", insertable=false, updatable=false)
	private Group supplierGroup;
	
	@ManyToOne
	@JoinColumn(name = "sd_distributor_pkid", insertable=false, updatable=false)
	private Distributor distributor;

	public int getPkId() {
		return pkId;
	}

	public void setPkId(int pkId) {
		this.pkId = pkId;
	}

	public Integer getSupplierGroupPkId() {
		return supplierGroupPkId;
	}

	public void setSupplierGroupPkId(Integer supplierGroupPkId) {
		this.supplierGroupPkId = supplierGroupPkId;
	}

	public Integer getDistributorPkId() {
		return distributorPkId;
	}

	public void setDistributorPkId(Integer distributorPkId) {
		this.distributorPkId = distributorPkId;
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

	public Group getSupplierGroup() {
		return supplierGroup;
	}

	public void setSupplierGroup(Group supplierGroup) {
		this.supplierGroup = supplierGroup;
	}

	public Distributor getDistributor() {
		return distributor;
	}

	public void setDistributor(Distributor distributor) {
		this.distributor = distributor;
	}

	
	
}
