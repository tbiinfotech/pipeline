package com.liquidpresentaion.authenticationservice.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.liquidpresentation.common.GroupType;
import com.liquidpresentation.common.UspsState;

@Entity
@Table(name = "lp_group")
public class Group {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pk_id", nullable = false)
	private int pkId;
	@Column(name = "g_name")
	private String name;
	@Enumerated(EnumType.STRING)
	@Column(length = 255, name = "g_type")
	private GroupType type;
	@Enumerated(EnumType.STRING)
	@Column(name = "g_state", nullable = true)
	private UspsState state;
	@Column(name = "g_title_image")
	private String titleImage;
	@Column(name = "g_state_numberofusers")
	private int stateNumberOfUsers;
	@Column(name = "g_include_default_cocktails")
	private boolean includeDefaultCocktails;
	@Column(name = "g_parent_pkid")
	private int parentPkId;
	@Column(name = "g_cocktails_limit")
	private int cocktailsLimit;
	@Column(name = "g_create_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
	private Date createDate;

	public Group() {
		
	}

	public Group(int pkId) {
		this.pkId = pkId;
	}

	public Group(int pkId, String name) {
		this.pkId = pkId;
		this.name = name;
	}

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

	public void setType(GroupType type) {
		this.type = type;
	}

	public GroupType getType() {
		return type;
	}

	public UspsState getState() {
		return state;
	}

	public void setState(UspsState state) {
		this.state = state;
	}

	public String getTitleImage() {
		return titleImage;
	}

	public void setTitleImage(String titleImage) {
		this.titleImage = titleImage;
	}

	public int getStateNumberOfUsers() {
		return stateNumberOfUsers;
	}

	public void setStateNumberOfUsers(int stateNumberOfUsers) {
		this.stateNumberOfUsers = stateNumberOfUsers;
	}

	public boolean isIncludeDefaultCocktails() {
		return includeDefaultCocktails;
	}

	public void setIncludeDefaultCocktails(boolean includeDefaultCocktails) {
		this.includeDefaultCocktails = includeDefaultCocktails;
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

	public Date getCreateDate() {
		return createDate;
	}
	
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}
