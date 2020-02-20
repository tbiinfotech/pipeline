package com.liquidpresentaion.managementservice.model;

public class User {

	private int pkId;
	private String email;

	public User() {
		
	}

	public User(String email) {
		this.email = email;
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
}
