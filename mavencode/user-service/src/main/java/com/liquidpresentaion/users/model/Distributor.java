package com.liquidpresentaion.users.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "lp_distributor")
public class Distributor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pk_id", nullable = false)
	private int id;
	@Column(name = "ds_name")
	private String name;
	@Column(name = "ds_create_timestamp", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
	private Date createDate;
	
	@OneToMany(mappedBy = "distributor", cascade = {CascadeType.ALL}, orphanRemoval = true)
	private Set<DistributorCategory> categorySet = new HashSet<>();

	@OneToMany(fetch=FetchType.LAZY, mappedBy = "distributor", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
	private List<Group> topGroups = new ArrayList<>();
	
	public Distributor() {
		
	}
	public Distributor(int id, String name, List<Group> topGroups) {
		this.id = id;
		this.name = name;
		this.topGroups = topGroups;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Set<DistributorCategory> getCategorySet() {
		return categorySet;
	}
	public void setCategorySet(Set<DistributorCategory> categorySet) {
		this.categorySet = categorySet;
	}
	public List<Group> getTopGroups() {
		return topGroups;
	}
	public void setTopGroups(List<Group> topGroups) {
		this.topGroups = topGroups;
	}
}
