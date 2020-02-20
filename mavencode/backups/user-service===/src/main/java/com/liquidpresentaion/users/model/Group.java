package com.liquidpresentaion.users.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.liquidpresentation.common.GroupType;
import com.liquidpresentation.common.UspsState;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

@Entity
@Table(name = "lp_group")
@JsonIdentityInfo(generator=JSOGGenerator.class)
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
	@ManyToOne(fetch=FetchType.LAZY, optional = true)
	@JoinColumn(name = "g_distributor_pkid")
	@JsonIgnore
	private Distributor distributor;
	@Column(name = "g_create_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
	private Date createDate;
	@OneToMany(mappedBy = "group", cascade = {CascadeType.ALL}, orphanRemoval = true)
	private List<GroupUser> groupUsers = new ArrayList<>();
	
	@ManyToMany(targetEntity = Group.class)
	@JoinTable(name = "lp_group_sales_supplier", joinColumns = {@JoinColumn(name = "gss_supplier_group_pkid")}, inverseJoinColumns = {@JoinColumn(name = "gss_sales_group_pkid")})
	private List<Group> salesGroups = new ArrayList<>();
	@ManyToMany(targetEntity = Group.class)
	@JoinTable(name = "lp_group_sales_supplier", joinColumns = {@JoinColumn(name = "gss_sales_group_pkid")}, inverseJoinColumns = {@JoinColumn(name = "gss_supplier_group_pkid")})
	private List<Group> supplierGroups = new ArrayList<>();
	
	@Transient
	@JsonInclude
	private List<Group> subGroups = new LinkedList<>();
	
	@Transient
	private long cocktailsCount;
	@Transient
	private long publishedCount;
	@Transient
	private Integer addTopFlig;

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

	public Distributor getDistributor() {
		return distributor;
	}

	public void setDistributor(Distributor distributor) {
		this.distributor = distributor;
	}

	public Date getCreateDate() {
		return createDate;
	}
	
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public void addSubGroup(Group subGroup){
		this.subGroups.add(subGroup);
	}

	public List<Group> getSubGroups() {
		return subGroups;
	}

	public List<GroupUser> getGroupUsers() {
		return groupUsers;
	}

	public void setGroupUsers(List<GroupUser> groupUsers) {
		this.groupUsers = groupUsers;
	}

	public void updateGroupUsers(Collection<GroupUser> groupUsers){
		this.groupUsers.retainAll(groupUsers);
		this.groupUsers.addAll(groupUsers);
	}
	
	public void addGroupUser(GroupUser groupUser){
		this.groupUsers.add(groupUser);
	}
	
	public void removeGroupUser(GroupUser groupUser){
		for (Iterator<GroupUser> iterator = groupUsers.iterator(); iterator.hasNext();) {
			if (iterator.next().equals(groupUser)) {
				iterator.remove();
			}
		}
	}

	public List<Group> getSalesGroups() {
		return salesGroups;
	}

	public void setSalesGroups(List<Group> salesGroups) {
		this.salesGroups = salesGroups;
	}

	public List<Group> getSupplierGroups() {
		return supplierGroups;
	}

	public void setSupplierGroups(List<Group> supplierGroups) {
		this.supplierGroups = supplierGroups;
	}
	
	public void addSalesGroup(Group salesGroup){
		this.salesGroups.add(salesGroup);
	}
	
	public void removeSalesGroup(Group salesGroup){
		for (Iterator<Group> iterator = salesGroups.iterator(); iterator.hasNext();) {
			if (iterator.next().getPkId() == salesGroup.getPkId()) {
				iterator.remove();
			}
		}
	}

	public long getCocktailsCount() {
		return cocktailsCount;
	}

	public void setCocktailsCount(long cocktailsCount) {
		this.cocktailsCount = cocktailsCount;
	}

	public long getPublishedCount() {
		return publishedCount;
	}

	public void setPublishedCount(long publishedCount) {
		this.publishedCount = publishedCount;
	}
	
	public boolean hasSubGroups(){
		return !this.subGroups.isEmpty();
	}
	
	public boolean parentOf(Group group){
		boolean isParent = false;
		for (Group subGroup : subGroups) {
			if (subGroup.getPkId() == group.getPkId()) {
				isParent = true;
			} else if(subGroup.hasSubGroups()){
				isParent = subGroup.parentOf(group);
			}
		}
		return isParent;
	}
	
	
	public Integer getAddTopFlig() {
		return addTopFlig;
	}

	public void setAddTopFlig(Integer addTopFlig) {
		this.addTopFlig = addTopFlig;
	}
}
