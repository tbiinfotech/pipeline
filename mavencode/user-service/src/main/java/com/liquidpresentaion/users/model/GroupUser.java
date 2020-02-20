package com.liquidpresentaion.users.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.liquidpresentation.common.Role;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

@Entity
@Table(name = "lp_group_user")
@JsonIdentityInfo(generator=JSOGGenerator.class)
public class GroupUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pk_id", nullable = false)
	private int pkId;

	@ManyToOne
	@JoinColumn(name = "gu_group_pkid", nullable = false)
	private Group group;
	
	@ManyToOne
	@JoinColumn(name = "gu_user_pkid", nullable = false)
	private User user;
	
	
	@Enumerated(EnumType.STRING)
	@Column(name = "gu_role")
	private Role role;


	public int getPkId() {
		return pkId;
	}


	public void setPkId(int pkId) {
		this.pkId = pkId;
	}


	public Group getGroup() {
		return group;
	}


	public void setGroup(Group group) {
		this.group = group;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public Role getRole() {
		return role;
	}


	public void setRole(Role role) {
		this.role = role;
	}


	@Override
	public int hashCode() {
		int result = 22;
		result = 37 * result + (this.group == null? 0: this.group.hashCode());
		result = 37 * result + (this.user == null? 0: this.user.hashCode());
		result = 37 * result + (this.role == null? 0: this.role.hashCode());
		
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		boolean eq = false;
		if (obj instanceof GroupUser) {
			GroupUser groupUser = (GroupUser) obj;
			if ((this.group.getPkId() == groupUser.getGroup().getPkId() || Role.ADMINISTRATOR.equals(groupUser.getRole())) 
					&& this.user.getPkId() == groupUser.getUser().getPkId()
					&& this.role.equals(groupUser.getRole())) {
				eq = true;
			}
		}
		return eq;
	}
}
