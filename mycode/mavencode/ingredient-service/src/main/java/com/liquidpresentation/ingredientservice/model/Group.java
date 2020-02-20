package com.liquidpresentation.ingredientservice.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.liquidpresentation.common.GroupType;
import com.liquidpresentation.common.UspsState;

@Entity
@Table(name="lp_group")
public class Group {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="pk_id")
	private long pkId;
	
	@Column(name="g_name")
	private String name;

	@Column(name = "g_distributor_pkid")
	private Long distributorPkId;

	@Enumerated(EnumType.STRING)
	@Column(length = 255, name = "g_type")
	private GroupType type;

	@Enumerated(EnumType.STRING)
	@Column(name = "g_state", nullable = true)
	private UspsState state;
	
	@OneToMany(mappedBy = "group", cascade = {CascadeType.ALL}, orphanRemoval = true)
	private List<GroupUser> groupUsers = new ArrayList<>();
	
	
	public long getPkId() {
		return pkId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getDistributorPkId() {
		return distributorPkId;
	}

	public void setDistributorPkId(Long distributorPkId) {
		this.distributorPkId = distributorPkId;
	}

	public GroupType getType() {
		return type;
	}

	public void setType(GroupType type) {
		this.type = type;
	}

	public void setPkId(long pkId) {
		this.pkId = pkId;
	}

	public UspsState getState() {
		return state;
	}

	public void setState(UspsState state) {
		this.state = state;
	}

	public List<GroupUser> getGroupUsers() {
		return groupUsers;
	}

	public void setGroupUsers(List<GroupUser> groupUsers) {
		this.groupUsers = groupUsers;
	}
}
