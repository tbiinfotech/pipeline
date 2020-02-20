package com.liquidpresentaion.cocktailservice.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

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

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.annotation.CreatedBy;

import com.liquidpresentation.common.BaseSpiritCategory;
import com.liquidpresentation.common.Category;
import com.liquidpresentation.common.IngredientType;


/**
 * The persistent class for the lp_ingredient database table.
 * 
 */
@Entity
@Table(name="lp_ingredient")
public class Ingredient  {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="pk_id")
	private Long pkId;

	@Enumerated(EnumType.STRING)
	@Column(name="in_base_spirit_category")
	private BaseSpiritCategory inBaseSpriteCategory;

	@Column(name="in_brand_pkid")
	private Long inBrandPkid;

	@Column(name="in_case_pack")
	private Integer inCasePack;

	@Enumerated(EnumType.STRING)
	@Column(name="in_category")
	private Category inCategory;

	@Column(name="in_create_timestamp", insertable=false, updatable=false)
	private Timestamp inCreateTimestamp;

	@Column(name="in_create_user_pkid", updatable=false)
	@CreatedBy
	private Long inCreateUserPkid;
	
	@Column(name="in_create_user_pkid",insertable=false, updatable=false)
	private Integer inCreateGroupUserPkid;

	@Column(name="in_distribtor_item_code")
	private String inDistribtorItemCode;

	@Column(name="in_group_name")
	private String inGroupName;

	@Column(name="in_method")
	private String inMethod;

	@Column(name="in_mpc")
	private String inMpc;

	@Column(name="in_name")
	private String inName;

	@Column(name="in_new_cust")
	private Boolean inNewCust;

	@Transient
	private String inRawsize;

	@Column(name="in_supplier_group_pkid")
	private Long inSupplierGroupPkid;

//	@ManyToOne
//	@JoinColumn(name="in_supplier_group_pkid", insertable=false, updatable=false)
//	@NotFound(action = NotFoundAction.IGNORE)
//	private Group supplierGroup;
	
//	@ManyToOne(targetEntity = User.class)
//	@JoinColumn(name = "in_create_user_pkid", insertable=false,updatable=false)
//	@NotFound(action = NotFoundAction.IGNORE)
//	private User createUser;
	
//	@ManyToOne(targetEntity = Brand.class)
//	@JoinColumn(name = "in_brand_pkid", insertable=false,updatable=false)
//	@NotFound(action = NotFoundAction.IGNORE)
//	private Brand brand;
	
	@Enumerated(EnumType.STRING)
	@Column(name="in_type")
	private IngredientType inType = IngredientType.general;
	
	@Column(name="in_type",insertable = false, updatable = false)
	private String inTypeString;
	
	@Column(name="in_uom")
	private String inUom;

	@Column(name="in_upc")
	private String inUpc;

	@Column(name="in_update_timestamp", insertable=false)
	private Timestamp inUpdateTimestamp;

	@Column(name="in_update_user_pkid")
	private Long inUpdateUserPkid;
	
	@Column(name="in_size")
	private BigDecimal inSize;
	
	@Column(name="in_distributor_pkid")
	private long inDistribtorPkid;
	
	@Transient
	private String dcCategory;
	
	@Transient
	private String supplierName;
	
	@Transient
	private String brandName;
	
	@Transient
	private String createdUserName;

	@Column(name="in_category",insertable = false, updatable = false)
	private String inCategoryString;
	
//	public String getCreatedUserName() {
//		if(this.createUser != null){
//			return this.createUser.getFirstName()+ " " +this.createUser.getLastName();
//		}
//		return null;
//	}

	public void setCreatedUserName(String createdByUserName) {
		this.createdUserName = createdByUserName;
	}

	
	@Transient
	private String createdBySupplierGroup;

	public Ingredient() {
	}

	public Long getPkId() {
		return this.pkId;
	}

	public void setPkId(Long pkId) {
		this.pkId = pkId;
	}

	public BaseSpiritCategory getInBaseSpriteCategory() {
		return inBaseSpriteCategory;
	}

	public void setInBaseSpriteCategory(BaseSpiritCategory inBaseSpriteCategory) {
		this.inBaseSpriteCategory = inBaseSpriteCategory;
	}

	public Long getInBrandPkid() {
		return this.inBrandPkid;
	}

	public void setInBrandPkid(Long inBrandPkid) {
		this.inBrandPkid = inBrandPkid;
	}

	public Integer getInCasePack() {
		return this.inCasePack;
	}

	public void setInCasePack(Integer inCasePack) {
		this.inCasePack = inCasePack;
	}

	public Category getInCategory() {
		return this.inCategory;
	}

	public void setInCategory(Category inCategory) {
		this.inCategory = inCategory;
	}

	public Timestamp getInCreateTimestamp() {
		return this.inCreateTimestamp;
	}

	public void setInCreateTimestamp(Timestamp inCreateTimestamp) {
		this.inCreateTimestamp = inCreateTimestamp;
	}

	public Long getInCreateUserPkid() {
		return this.inCreateUserPkid;
	}

	public void setInCreateUserPkid(Long inCreateUserPkid) {
		this.inCreateUserPkid = inCreateUserPkid;
	}

	public String getInDistribtorItemCode() {
		return this.inDistribtorItemCode;
	}

	public void setInDistribtorItemCode(String inDistribtorItemCode) {
		this.inDistribtorItemCode = inDistribtorItemCode;
	}

	public String getInGroupName() {
		return this.inGroupName;
	}

	public void setInGroupName(String inGroupName) {
		this.inGroupName = inGroupName;
	}

	public String getInMethod() {
		return this.inMethod;
	}

	public void setInMethod(String inMethod) {
		this.inMethod = inMethod;
	}

	public String getInMpc() {
		return this.inMpc;
	}

	public void setInMpc(String inMpc) {
		this.inMpc = inMpc;
	}

	public String getInName() {
		return this.inName;
	}

	public void setInName(String inName) {
		this.inName = inName;
	}

	public Boolean getInNewCust() {
		return this.inNewCust;
	}

	public void setInNewCust(Boolean inNewCust) {
		this.inNewCust = inNewCust;
	}

	public String getInRawsize() {
		return  (this.inSize == null || this.inUom == null) ? "" : (this.inSize.stripTrailingZeros().toPlainString() + " " + this.inUom);
	}

	public void setInRawsize(String inRawsize) {
		this.inRawsize = inRawsize;
	}

	public Long getInSupplierGroupPkid() {
		return this.inSupplierGroupPkid;
	}

	public void setInSupplierGroupPkid(Long inSupplierGroupPkid) {
		this.inSupplierGroupPkid = inSupplierGroupPkid;
	}

	public IngredientType getInType() {
		return this.inType;
	}

	public void setInType(IngredientType inType) {
		this.inType = inType;
	}

	public String getInUom() {
		return this.inUom;
	}

	public void setInUom(String inUom) {
		this.inUom = inUom;
	}

	public String getInUpc() {
		return this.inUpc;
	}

	public void setInUpc(String inUpc) {
		this.inUpc = inUpc;
	}

	public Timestamp getInUpdateTimestamp() {
		return this.inUpdateTimestamp;
	}

	public void setInUpdateTimestamp(Timestamp inUpdateTimestamp) {
		this.inUpdateTimestamp = inUpdateTimestamp;
	}

	public Long getInUpdateUserPkid() {
		return this.inUpdateUserPkid;
	}

	public void setInUpdateUserPkid(Long inUpdateUserPkid) {
		this.inUpdateUserPkid = inUpdateUserPkid;
	}

	public BigDecimal getInSize() {
		return inSize;
	}

	public void setInSize(BigDecimal inSize) {
		this.inSize = inSize;
	}

	public String getDcCategory() {
		return dcCategory;
	}

	public void setDcCategory(String dcCategory) {
		this.dcCategory = dcCategory;
	}

	public long getInDistribtorPkid() {
		return inDistribtorPkid;
	}

	public void setInDistribtorPkid(long inDistribtorPkid) {
		this.inDistribtorPkid = inDistribtorPkid;
	}

//	public String getSupplierName() {
//		if(this.supplierGroup != null) {
//			return this.supplierGroup.getName();
//		}
//		return null;
//	}
//
//	public void setSupplierName(String supplierName) {
//		this.supplierName = supplierName;
//	}
//
//	public Group getSupplierGroup() {
//		return supplierGroup;
//	}
//
//	public void setSupplierGroup(Group supplierGroup) {
//		this.supplierGroup = supplierGroup;
//	}
//
//	public User getCreateUser() {
//		return createUser;
//	}
//
//	public void setCreateUser(User createUser) {
//		this.createUser = createUser;
//	}
//
//	public Brand getBrand() {
//		return brand;
//	}
//
//	public void setBrand(Brand brand) {
//		this.brand = brand;
//	}

	public String getInCategoryString() {
		return inCategoryString;
	}

	public void setInCategoryString(String inCategoryString) {
		this.inCategoryString = inCategoryString;
	}

	public String getInTypeString() {
		return inTypeString;
	}

	public void setInTypeString(String inTypeString) {
		this.inTypeString = inTypeString;
	}

	public String getCreatedBySupplierGroup() {
		return createdBySupplierGroup;
	}

	public void setCreatedBySupplierGroup(String createdBySupplierGroup) {
		this.createdBySupplierGroup = createdBySupplierGroup;
	}

	public Integer getInCreateGroupUserPkid() {
		return inCreateGroupUserPkid;
	}

	public void setInCreateGroupUserPkid(Integer inCreateGroupUserPkid) {
		this.inCreateGroupUserPkid = inCreateGroupUserPkid;
	}
}