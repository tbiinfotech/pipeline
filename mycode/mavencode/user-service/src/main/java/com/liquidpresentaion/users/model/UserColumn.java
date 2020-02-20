package com.liquidpresentaion.users.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

import javax.persistence.Id;

@Entity
@Table(name = "lp_user_column")
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class UserColumn {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pk_id", nullable = false)
	private int pkId;

	@Column(name = "uc_user_pkid")
	private int userPkid;

	@Column(name = "uc_column_name")
	private String columnName;

	@Column(name = "uc_column_index")
	private Integer columnIndex;

	public UserColumn(int userPkid, String columnName,  int columnIndex) {
		this.userPkid = userPkid;
		this.columnName = columnName;
		this.columnIndex = columnIndex;
	}

	public UserColumn() {
	}

	public int getPkId() {
		return pkId;
	}

	public void setPkId(int pkId) {
		this.pkId = pkId;
	}

	public int getUserPkid() {
		return userPkid;
	}

	public void setUserPkid(int userPkid) {
		this.userPkid = userPkid;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public Integer getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(Integer columnIndex) {
		this.columnIndex = columnIndex;
	}
}
