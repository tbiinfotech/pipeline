package com.liquidpresentaion.cocktailservice.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.liquidpresentation.common.GroupType;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

@Entity
@Table(name = "lp_group")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class Group {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pk_id", nullable = false)
	private int pkId;
	@Column(name = "g_name")
	private String name;
	@Enumerated(EnumType.STRING)
	@Column(name = "g_type")
	private GroupType type;
	@Column(name = "g_parent_pkid")
	private Integer parentPkid;
	@Column(name = "g_cocktails_limit")
	private Integer cocktailsLimit;
	@Column(name = "g_create_date")
	private Date createDate;
	@Column(name = "g_parent_pkid",insertable = false, updatable = false)
	private int parentPkId;
	
	public int getPkId() {
		return pkId;
	}

	public void setPkId(int pkId) {
		this.pkId = pkId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getParentPkid() {
		return parentPkid;
	}

	public void setParentPkid(Integer parentPkid) {
		this.parentPkid = parentPkid;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public GroupType getType() {
		return type;
	}

	public void setType(GroupType type) {
		this.type = type;
	}

	public int getParentPkId() {
		return parentPkId;
	}

	public void setParentPkId(int parentPkId) {
		this.parentPkId = parentPkId;
	}
	
	public int getCocktailsLimit() {
		return cocktailsLimit;
	}
	
	public void setCocktailsLimit(int cocktailsLimit) {
		this.cocktailsLimit = cocktailsLimit;
	}
	public Long getLongPkid(){
		return Integer.valueOf(this.pkId).longValue();
	}
	
}
