package com.liquidpresentaion.cocktailservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

@Entity
@Table(name = "lp_cocktail_group")
@JsonIdentityInfo(generator=JSOGGenerator.class)
@EntityListeners(AuditingEntityListener.class)
public class CocktailGroup {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pk_id", nullable = false)
	private int pkId;
	
	@Column(name = "ctg_cocktail_pkid", nullable = false)
	private Integer cocktailId;
	
	@Column(name = "ctg_group_pkid", nullable = false)
	private Integer groupId;
	
	@Column(name = "ctg_flg")
	private Integer ctgFlg;
	
	public CocktailGroup() {
		
	}
	
	public CocktailGroup(Integer cocktailId, Integer groupId){
		this.cocktailId = cocktailId;
		this.groupId = groupId;
	}
	public CocktailGroup(Integer cocktailId, Integer groupId, Integer ctgFlg){
		this.cocktailId = cocktailId;
		this.groupId = groupId;
		this.ctgFlg = ctgFlg;
	}
	
	public int getPkId() {
		return pkId;
	}

	public void setPkId(int pkId) {
		this.pkId = pkId;
	}

	public Integer getCocktailId() {
		return cocktailId;
	}

	public void setCocktailId(Integer cocktailId) {
		this.cocktailId = cocktailId;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}
	public Integer getCtgFlg() {
		return ctgFlg;
	}
	public void setCtgFlg(Integer ctgFlg) {
		this.ctgFlg = ctgFlg;
	}
	
	
}
