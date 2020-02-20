package com.liquidpresentaion.cocktailservice.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.liquidpresentation.common.Constants;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

@Entity
@Table(name = "lp_presentation")
@JsonIdentityInfo(generator=JSOGGenerator.class)
@EntityListeners(AuditingEntityListener.class)
public class Presentation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pk_id", nullable = false)
	private Integer id;
	@Column(name = "ps_customer_account_id")
	private String customerAccountId;
	@Column(name = "ps_customer_account_name")
	private String customerAcctName;
	@Column(name = "ps_title")
	private String title;
	@Column(name = "ps_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMATE)
	private Date date;
	@Column(name = "ps_contact_info")
	private String contactInfo;
	@Column(name = "ps_account_logo")
	private String accountLogo;
	@Column(name = "ps_branded_element")
	private String brandedElement;
	@Column(name = "ps_state_group_id")
	private Integer stateGroupPkId;
	@Column(name = "ps_uom")
	private String uom;
	@Column(name = "ps_archived")
	private Boolean archived;
	@Column(name = "ps_create_user_pkid")
	@CreatedBy
	private Integer createPkId;
	@Column(name = "ps_create_timestamp", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMATE)
	@CreatedDate
	private Date createDate;
	@Column(name = "ps_update_user_pkid")
	@LastModifiedBy
	private Integer updatePkId;
	@Column(name = "ps_update_timestamp", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@LastModifiedDate
	private Date updateDate;
	@Transient
	private String dateStrings;
	
	@ManyToMany(targetEntity = Cocktail.class)
	@JoinTable(name = "lp_presentation_cocktail", joinColumns = {@JoinColumn(name = "pc_presentation_pkid")}, 
													inverseJoinColumns = {@JoinColumn(name = "pc_cocktail_pkid")})
	private List<Cocktail> cocktails = new ArrayList<>();
	
	public Presentation() {
		
	}
	
	public Presentation(Integer id, String title, Date createDate, String uom,Integer stateGroupPkId) {
		this.id = id;
		this.title = title;
		this.createDate = createDate;
		this.uom = uom;
		this.stateGroupPkId = stateGroupPkId;
	}
	
	public Integer getId() {
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
	public String getContactInfo() {
		return contactInfo;
	}
	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
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
	public Integer getStateGroupPkId() {
		return stateGroupPkId;
	}
	public void setStateGroupPkId(int stateGroupPkId) {
		this.stateGroupPkId = stateGroupPkId;
	}
	public String getUom() {
		return uom;
	}
	public void setUom(String uom) {
		this.uom = uom;
	}
	public Boolean isArchived() {
		return archived;
	}
	public void setArchived(Boolean archived) {
		this.archived = archived;
	}
	public Integer getCreatePkId() {
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
	public Integer getUpdatePkId() {
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
	public List<Cocktail> getCocktails() {
		return cocktails;
	}
	public void setCocktails(List<Cocktail> cocktails) {
		this.cocktails = cocktails;
	}
	public void addCocktail(Cocktail cocktail){
		this.cocktails.add(cocktail);
	}

	public String getDateStrings() {
		return this.dateStrings;
	}

	public void setDateStrings(String dateStrings) {
		this.dateStrings = dateStrings;
	}
}
