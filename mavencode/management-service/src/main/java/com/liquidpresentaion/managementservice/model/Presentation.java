package com.liquidpresentaion.managementservice.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.liquidpresentation.common.Constants;


@Entity
@Table(name = "lp_presentation")
public class Presentation {

	@Id
	@Column(name = "pk_id", nullable = false)
	private int id;
	@Column(name = "ps_customer_account_id")
	private String customerAccountId;
	@Column(name = "ps_customer_account_name")
	private String customerAcctName;

	@Transient
	private String title;
	@Transient
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMATE)
	private Date date;
	@Transient
	private String uom;
	@Transient
	private String accountLogo;
	@Transient
	private String brandedElement;
	@Transient
	private String contactInfo;
	@Transient
	private String subject;
	@Transient
	private String to;
	@Transient
	private String message;
	@Transient
	private Integer stateGroupPkId;
	@Transient
	private List<Cocktail> cocktails = new ArrayList<>();
	
	public Presentation() {
		
	}
	
	public Presentation(int id, String customerAccountId, String customerAcctName) {
		this.id = id;
		this.customerAccountId = customerAccountId;
		this.customerAcctName = customerAcctName;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCustomerAccountId() {
		return customerAccountId;
	}
	public void setCustomerAccountId(String customerAccountId) {
		this.customerAccountId = customerAccountId;
	}
	public String getCustomerAcctName() {
		return customerAcctName;
	}
	public void setCustomerAcctName(String customerAcctName) {
		this.customerAcctName = customerAcctName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public String getAccountLogo() {
		return accountLogo;
	}

	public void setAccountLogo(String accountLogo) {
		this.accountLogo = accountLogo;
	}

	public String getBrandedElement() {
		return brandedElement;
	}

	public void setBrandedElement(String brandedElement) {
		this.brandedElement = brandedElement;
	}

	public String getContactInfo() {
		return contactInfo;
	}

	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}

	public Integer getStateGroupPkId() {
		return stateGroupPkId;
	}

	public void setStateGroupPkId(Integer stateGroupPkId) {
		this.stateGroupPkId = stateGroupPkId;
	}

	public List<Cocktail> getCocktails() {
		return cocktails;
	}

	public void setCocktails(List<Cocktail> cocktails) {
		this.cocktails = cocktails;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
