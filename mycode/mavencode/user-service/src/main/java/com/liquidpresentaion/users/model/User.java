package com.liquidpresentaion.users.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.liquidpresentation.common.Constants;
import com.liquidpresentation.common.GroupType;
import com.liquidpresentation.common.Role;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
@Table(name = "lp_user")
@JsonIdentityInfo(generator=JSOGGenerator.class)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pk_id", nullable = false)
	private int pkId;
	
	@Column(name = "u_first_name")
	private String firstName;

	@Column(name = "u_last_name")
	private String lastName;
	
	@Column(name = "u_Email", unique = true)
	private String email;

	@Column(name = "u_password")
	private String password;
	
	@Column(name = "u_phone")
	private String phone;
	
	@Column(name = "u_recovery")
	private String recovery;
	
	@Column(name = "u_create_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMATE)
	private Date createDate;
	
	@OneToMany(mappedBy = "user", cascade = {CascadeType.ALL}, orphanRemoval = true)
	private List<GroupUser> userGroups = new ArrayList<GroupUser>();
	
	@Transient
	@JsonIgnore
	private String stateName;
	
	@Transient
	@JsonIgnore
	private String distributorName;
	
	public User() {}
	public User(String email) {
		this.email = email;
	}
	public User(int pkId, String firstName, String lastName) {
		this.pkId = pkId;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public User(int pkId, String firstName, String lastName, Date createDate, String email, String phone) {
		this.pkId = pkId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.createDate = createDate;
		this.email = email;
		this.phone = phone;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public int getPkId() {
		return pkId;
	}
	public void setPkId(int pkId) {
		this.pkId = pkId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getRecovery() {
		return recovery;
	}
	public void setRecovery(String recovery) {
		this.recovery = recovery;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public void updateUserGroups(Collection<GroupUser> userGroups) {
		this.userGroups.clear();
		this.userGroups.addAll(userGroups);
	}
	public List<GroupUser> getUserGroups() {
		return userGroups;
	}
	public void setUserGroups(List<GroupUser> userGroups) {
		this.userGroups = userGroups;
		for (GroupUser groupUser : userGroups) {
			groupUser.setUser(this);
		}
	}
	public String getFullName() {
		return this.firstName + " " + this.lastName;
	}
	public String getStateName() {
		return stateName;
	}
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
	public String getDistributorName() {
		return distributorName;
	}
	public void setDistributorName(String distributorName) {
		this.distributorName = distributorName;
	}
	public boolean isAdministrator(){
		if (this.userGroups.isEmpty()) {
			return false;
		} else {
			for (GroupUser groupUser : userGroups) {
				if (Role.ADMINISTRATOR.equals(groupUser.getRole())) {
					return true;
				}
			}
			return false;
		}
	}
	public boolean isDistributorMixologist(){
		if (this.userGroups.isEmpty()) {
			return false;
		} else {
			for (GroupUser groupUser : userGroups) {
				if (Role.MIXOLOGIST.equals(groupUser.getRole()) && GroupType.sales.equals(groupUser.getGroup().getType())) {
					return true;
				}
			}
			return false;
		}
	}
}
