package com.liquidpresentation.ingredientservice.model;

import java.sql.Timestamp;

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
@Table(name="lp_brand")
public class Brand {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="pk_id")
	private long pkId;
	
	@Column(name="br_name")
	private String brName;
	
	@Column(name = "br_distributor_brand_id")
	private String distributorBrandId;

	@Column(name="br_supplier_group_pkid")
	private Long brSupplierGroupPkid;
	
	@Column(name="br_reviewed")
	private Boolean brReviewed;
	
	@Column(name="br_southern_brand_group_id")
	private Long brSouthernBrandGroupId;
	
	@Column(name="br_mpc")
	private String brMpc;
	
	@Enumerated(EnumType.STRING)
	@Column(name="br_base_spirit_category")
	private BaseSpiritCategory brBaseSpiritCategory;
	
	@Column(name="br_create_timestamp", insertable=false, updatable=false)
	private Timestamp brCreateTimestamp;

	@Column(name="br_create_user_pkid", updatable=false)
	private Long brCreateUserPkid;
	
	@Column(name="br_update_timestamp", insertable=false)
	private Timestamp brUpdateTimestamp;

	@Column(name="br_update_user_pkid")
	private Long  brUpdateUserPkid;
	
	@Enumerated(EnumType.STRING)
	@Column(name="br_category")
	private Category brCategory;
	
	@Column(name = "br_deleted")
	private int deleted;

	public long getPkId() {
		return pkId;
	}

	public void setPkId(long pkId) {
		this.pkId = pkId;
	}

	public String getBrName() {
		return brName;
	}

	public void setBrName(String brName) {
		this.brName = brName;
	}

	public String getDistributorBrandId() {
		return distributorBrandId;
	}

	public void setDistributorBrandId(String distributorBrandId) {
		this.distributorBrandId = distributorBrandId;
	}

	public long getBrSupplierGroupPkid() {
		return brSupplierGroupPkid==null?0:brSupplierGroupPkid;
	}

	public void setBrSupplierGroupPkid(Long brSupplierGroupPkid) {
		this.brSupplierGroupPkid = brSupplierGroupPkid;
	}

	public Boolean isBrReviewed() {
		return brReviewed;
	}

	public void setBrReviewed(Boolean brReviewed) {
		this.brReviewed = brReviewed;
	}
	
	public BaseSpiritCategory getBrBaseSpiritCategory() {
		return brBaseSpiritCategory;
	}

	public void setBrBaseSpiritCategory(BaseSpiritCategory brBaseSpiritCategory) {
		this.brBaseSpiritCategory = brBaseSpiritCategory;
	}

	public Long getBrSouthernBrandGroupId() {
		return brSouthernBrandGroupId;
	}

	public void setBrSouthernBrandGroupId(Long brSouthernBrandGroupId) {
		this.brSouthernBrandGroupId = brSouthernBrandGroupId;
	}	

	public String getBrMpc() {
		return brMpc;
	}

	public void setBrMpc(String brMpc) {
		this.brMpc = brMpc;
	}

	public Timestamp getBrCreateTimestamp() {
		return brCreateTimestamp;
	}

	public void setBrCreateTimestamp(Timestamp brCreateTimestamp) {
		this.brCreateTimestamp = brCreateTimestamp;
	}

	public Long getBrCreateUserPkid() {
		return brCreateUserPkid;
	}

	public void setBrCreateUserPkid(Long brCreateUserPkid) {
		this.brCreateUserPkid = brCreateUserPkid;
	}

	public Timestamp getBrUpdateTimestamp() {
		return brUpdateTimestamp;
	}

	public void setBrUpdateTimestamp(Timestamp brUpdateTimestamp) {
		this.brUpdateTimestamp = brUpdateTimestamp;
	}

	public Long getBrUpdateUserPkid() {
		return brUpdateUserPkid;
	}

	public void setBrUpdateUserPkid(Long brUpdateUserPkid) {
		this.brUpdateUserPkid = brUpdateUserPkid;
	}

	public Category getBrCategory() {
		return brCategory;
	}

	public void setBrCategory(Category brCategory) {
		this.brCategory = brCategory;
	}

	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}
}
