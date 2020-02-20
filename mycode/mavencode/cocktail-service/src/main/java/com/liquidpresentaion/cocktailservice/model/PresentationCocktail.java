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
@Table(name = "lp_presentation_cocktail")
@JsonIdentityInfo(generator=JSOGGenerator.class)
@EntityListeners(AuditingEntityListener.class)
public class PresentationCocktail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pk_id", nullable = false)
	private Integer pkId;
	
	@Column(name = "pc_presentation_pkid")
	private Integer presenttationId;
	
	@Column(name = "pc_cocktail_pkid")
	private Integer cocktailId;

	public Integer getPkId() {
		return pkId;
	}

	public void setPkId(Integer pkId) {
		this.pkId = pkId;
	}

	public Integer getPresenttationId() {
		return presenttationId;
	}

	public void setPresenttationId(Integer presenttationId) {
		this.presenttationId = presenttationId;
	}

	public Integer getCocktailId() {
		return cocktailId;
	}

	public void setCocktailId(Integer cocktailId) {
		this.cocktailId = cocktailId;
	}
	
}
