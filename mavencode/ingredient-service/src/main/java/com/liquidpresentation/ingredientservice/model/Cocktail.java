package com.liquidpresentation.ingredientservice.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.liquidpresentation.common.BaseSpiritCategory;
import com.liquidpresentation.common.Constants;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

@Entity
@Table(name = "lp_cocktail")
@JsonIdentityInfo(generator=JSOGGenerator.class)
@EntityListeners(AuditingEntityListener.class)
public class Cocktail {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pk_id", nullable = false)
	private int id;
	@Column(name = "ct_name")
	private String name;
	@Column(name = "ct_brand_pkid")
	private long brandPkId;
	@Column(name = "ct_brand_name")
	private String brandName;
	@Column(name = "ct_supplier_pkid")
	private int supplierPkId;
	@Enumerated(EnumType.STRING)
	@Column(name = "ct_base_spirit_category")
	private BaseSpiritCategory baseSpiritCategory;
	@Column(name = "ct_diff_degree")
	private int degreeOfDiff;
	@Column(name = "ct_image")
	private String image;
	@Column(name = "ct_garnish")
	private String garnish;
	@Column(name = "ct_method")
	private String method;
	@Column(name = "ct_comments")
	private String comments;
	@Column(name = "ct_mixologist_pkid")
	private int mixologistPkId;
	@Column(name = "ct_mixologist_name")
	private String mixologistName;
	@Column(name = "ct_published")
	private boolean published;
	@Column(name = "ct_create_user_pkid")
	@CreatedBy
	private int createPkId;
	@Column(name = "ct_create_timestamp", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMATE)
	@CreatedDate
	private Date createDate;
	@Column(name = "ct_update_user_pkid")
	@LastModifiedBy
	private int updatePkId;
	@Column(name = "ct_update_timestamp", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@LastModifiedDate
	private Date updateDate;
	@Column(name = "ct_ingredientsnumber", nullable = true)
	private Integer ingredientsnumber;
	@Column(name = "ct_archiveFlag")
	private int archiveFlag;
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
	public long getBrandPkId() {
		return brandPkId;
	}
	public void setBrandPkId(long brandPkId) {
		this.brandPkId = brandPkId;
	}
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	public int getSupplierPkId() {
		return supplierPkId;
	}
	public void setSupplierPkId(int supplierPkId) {
		this.supplierPkId = supplierPkId;
	}
	public BaseSpiritCategory getBaseSpiritCategory() {
		return baseSpiritCategory;
	}
	public void setBaseSpiritCategory(BaseSpiritCategory baseSpiritCategory) {
		this.baseSpiritCategory = baseSpiritCategory;
	}
	public int getDegreeOfDiff() {
		return degreeOfDiff;
	}
	public void setDegreeOfDiff(int degreeOfDiff) {
		this.degreeOfDiff = degreeOfDiff;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getGarnish() {
		return garnish;
	}
	public void setGarnish(String garnish) {
		this.garnish = garnish;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public int getMixologistPkId() {
		return mixologistPkId;
	}
	public void setMixologistPkId(int mixologistPkId) {
		this.mixologistPkId = mixologistPkId;
	}
	public String getMixologistName() {
		return mixologistName;
	}
	public void setMixologistName(String mixologistName) {
		this.mixologistName = mixologistName;
	}
	public boolean isPublished() {
		return published;
	}
	public void setPublished(boolean published) {
		this.published = published;
	}
	public int getCreatePkId() {
		return createPkId;
	}
	public void setCreatePkId(int createPkId) {
		this.createPkId = createPkId;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public int getUpdatePkId() {
		return updatePkId;
	}
	public void setUpdatePkId(int updatePkId) {
		this.updatePkId = updatePkId;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public Integer getIngredientsnumber() {
		return ingredientsnumber;
	}
	public void setIngredientsnumber(Integer ingredientsnumber) {
		this.ingredientsnumber = ingredientsnumber;
	}
	public int getArchiveFlag() {
		return archiveFlag;
	}
	public void setArchiveFlag(int archiveFlag) {
		this.archiveFlag = archiveFlag;
	}
}
