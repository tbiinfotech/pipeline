package com.liquidpresentaion.cocktailservice.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.liquidpresentation.common.Constants;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

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
	
	public User() {}
	public User(String email) {
		this.email = email;
	}
	public User(int pkId, String firstName, String lastName) {
		this.pkId = pkId;
		this.firstName = firstName;
		this.lastName = lastName;
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
}
