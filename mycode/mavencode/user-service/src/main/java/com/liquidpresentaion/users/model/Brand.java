package com.liquidpresentaion.users.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.liquidpresentation.common.BaseSpiritCategory;
import com.liquidpresentation.common.Category;
import com.liquidpresentation.common.Constants;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

@Entity
@Table(name = "lp_brand")
@JsonIdentityInfo(generator=JSOGGenerator.class)
public class Brand {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pk_id", nullable = false)
	private int id;
	@Column(name = "br_name")
	private String name;
	@Column(name = "br_distributor_brand_id")
	private String distributorBrandId;
	@Column(name = "br_supplier_group_pkid")
	private Integer supplierGroupId;
	@ManyToOne(targetEntity = Group.class)
//	@JoinColumn(name = "br_supplier_group_pkid")
	@JoinColumn(name = "br_supplier_group_pkid", insertable=false, updatable=false)
	@NotFound(action = NotFoundAction.IGNORE)
	private Group supplierGroup;
	@Column(name = "br_default_image")
	private String defaultImage;
	@Column(name = "br_reviewed")
	private boolean reviewed;
	@Column(name = "br_southern_brand_group_id")
	private Integer southernBrandGroupId;
	@Column(name = "br_mpc")
	private String mpc;
	@Enumerated(EnumType.STRING)
	@Column(name = "br_base_spirit_category")
	private BaseSpiritCategory baseSpiritCategory;
	@Column(name = "br_create_user_pkid")
	private Integer createPkId;
	@Column(name = "br_create_timestamp", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMATE)
	private Date createDate;
	@Column(name = "br_update_user_pkid")
	private Integer updatePkId;
	@Column(name = "br_update_timestamp", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Date updateDate;
	
	@Column(name = "br_deleted")
	private int deleted;
	
	@Transient
	private String supplierGroupName;
	@Transient
	private String baseSpiritCategoryString;
	
	@ManyToMany(targetEntity = Group.class)
	@JoinTable(name = "lp_brand_group", joinColumns = {@JoinColumn(name = "bdg_brand_pkid")}, inverseJoinColumns = {@JoinColumn(name = "bdg_group_pkid")})
	private List<Group> salesGroups = new ArrayList<>();
	
	@Enumerated(EnumType.STRING)
	@Column(name="br_category")
	@NotFound(action = NotFoundAction.IGNORE)
	private Category brCategory;
	
	@Transient
	private Ingredient ingredient;
	@Transient
	private List<HousemadeBrand> housemadeList;
	
	public Brand() {
	}
	public Brand(int id, String name, Group supplierGroup, BaseSpiritCategory baseSpiritCategory, String defaultImage) {
		this.id = id;
		this.name = name;
		this.supplierGroup = supplierGroup;
		this.baseSpiritCategory = baseSpiritCategory;
		this.defaultImage = defaultImage;
	}
	public Brand(int id, String name, Group supplierGroup, BaseSpiritCategory baseSpiritCategory, String defaultImage, Date createDate) {
		this.id = id;
		this.name = name;
		this.supplierGroup = supplierGroup;
		this.baseSpiritCategory = baseSpiritCategory;
		this.defaultImage = defaultImage;
		this.createDate = createDate;
	}
	public Brand(int id, String name, Group supplierGroup, BaseSpiritCategory baseSpiritCategory, String defaultImage, Date createDate, Category brCategory, String baseSpiritCategoryString) {
		this.id = id;
		this.name = name;
		this.supplierGroup = supplierGroup;
		this.baseSpiritCategory = baseSpiritCategory;
		this.defaultImage = defaultImage;
		this.createDate = createDate;
		this.brCategory = brCategory;
		this.baseSpiritCategoryString = baseSpiritCategoryString;
	}
	
	public Brand(int id, String name, Group supplierGroup, BaseSpiritCategory baseSpiritCategory, String defaultImage, Date createDate, Category brCategory, String baseSpiritCategoryString, int deleted) {
		this.id = id;
		this.name = name;
		this.supplierGroup = supplierGroup;
		this.baseSpiritCategory = baseSpiritCategory;
		this.defaultImage = defaultImage;
		this.createDate = createDate;
		this.brCategory = brCategory;
		this.baseSpiritCategoryString = baseSpiritCategoryString;
		this.deleted = deleted;
	}
	
	public Brand(int id, String name, Group supplierGroup, BaseSpiritCategory baseSpiritCategory, String defaultImage, Date createDate, Category brCategory,Ingredient ingredient,String baseSpiritCategoryString) {
		this.id = id;
		this.name = name;
		this.supplierGroup = supplierGroup;
		this.baseSpiritCategory = baseSpiritCategory;
		this.defaultImage = defaultImage;
		this.createDate = createDate;
		this.brCategory = brCategory;
		this.ingredient = ingredient;
		this.baseSpiritCategoryString = baseSpiritCategoryString;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDefaultImage() {
		return defaultImage;
	}
	public void setDefaultImage(String defaultImage) {
		this.defaultImage = defaultImage;
	}
	public boolean isReviewed() {
		return reviewed;
	}
	public String getDistributorBrandId() {
		return distributorBrandId;
	}
	public void setDistributorBrandId(String distributorBrandId) {
		this.distributorBrandId = distributorBrandId;
	}
	public Integer getSupplierGroupId() {
		return supplierGroupId;
	}
	public void setSupplierGroupId(Integer supplierGroupId) {
		this.supplierGroupId = supplierGroupId;
	}
	public Group getSupplierGroup() {
		if (this.supplierGroup == null) {
			return new Group();
		}
		return new Group(this.supplierGroup.getPkId(), this.supplierGroup.getName());
	}
	public void setSupplierGroup(Group supplierGroup) {
		this.supplierGroup = supplierGroup;
	}
	public void setReviewed(boolean reviewed) {
		this.reviewed = reviewed;
	}
	public Integer getSouthernBrandGroupId() {
		return southernBrandGroupId;
	}
	public void setSouthernBrandGroupId(Integer southernBrandGroupId) {
		this.southernBrandGroupId = southernBrandGroupId;
	}
	public String getMpc() {
		return mpc;
	}
	public void setMpc(String mpc) {
		this.mpc = mpc;
	}
	public BaseSpiritCategory getBaseSpiritCategory() {
		return baseSpiritCategory;
	}
	public void setBaseSpiritCategory(BaseSpiritCategory baseSpiritCategory) {
		this.baseSpiritCategory = baseSpiritCategory;
	}
	public Integer getCreatePkId() {
		return createPkId;
	}
	public void setCreatePkId(Integer createPkId) {
		this.createPkId = createPkId;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Integer getUpdatePkId() {
		return updatePkId;
	}
	public void setUpdatePkId(Integer updatePkId) {
		this.updatePkId = updatePkId;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public List<Group> getSalesGroups() {
		return salesGroups;
	}
	public void setSalesGroups(List<Group> salesGroups) {
		this.salesGroups = salesGroups;
	}
	public String getSupplierGroupName() {
		return supplierGroupName;
	}
	public void setSupplierGroupName(String supplierGroupName) {
		this.supplierGroupName = supplierGroupName;
	}
	public Category getBrCategory() {
		return brCategory;
	}
	public void setBrCategory(Category brCategory) {
		this.brCategory = brCategory;
	}
	public Ingredient getIngredient() {
		return ingredient;
	}
	public void setIngredient(Ingredient ingredient) {
		this.ingredient = ingredient;
	}
	public List<HousemadeBrand> getHousemadeList() {
		return housemadeList;
	}
	public void setHousemadeList(List<HousemadeBrand> housemadeList) {
		this.housemadeList = housemadeList;
	}
	public String getBaseSpiritCategoryString() {
		return this.baseSpiritCategory == null ? null : this.baseSpiritCategory.toString();
	}
	public void setBaseSpiritCategoryString(String baseSpiritCategoryString) {
		this.baseSpiritCategoryString = baseSpiritCategoryString;
	}
	public int getDeleted() {
		return deleted;
	}
	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}
}
