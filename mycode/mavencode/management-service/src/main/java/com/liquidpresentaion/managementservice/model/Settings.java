package com.liquidpresentaion.managementservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "lp_settings")
public class Settings {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pk_id", nullable = false)
	private int pkId;
	
	@Column(name = "smtp_user")
	private String smtpUser;

	@Column(name = "smtp_server")
	private String smtpServer;
	
	@Column(name = "smtp_password")
	private String smtpPassword;

	@Column(name = "contact_us")
	private String contactUs;
	
	@Column(name = "smtp_port")
	private int smtpPort;

	@Column(name = "smtp_use_TLS")
	private boolean smtpUseTls;

	public int getPkId() {
		return pkId;
	}

	public void setPkId(int pkId) {
		this.pkId = pkId;
	}

	public String getSmtpUser() {
		return smtpUser;
	}

	public void setSmtpUser(String smtpUser) {
		this.smtpUser = smtpUser;
	}

	public String getSmtpServer() {
		return smtpServer;
	}

	public void setSmtpServer(String smtpServer) {
		this.smtpServer = smtpServer;
	}

	public String getSmtpPassword() {
		return smtpPassword;
	}

	public void setSmtpPassword(String smtpPassword) {
		this.smtpPassword = smtpPassword;
	}

	public String getContactUs() {
		return contactUs;
	}

	public void setContactUs(String contactUs) {
		this.contactUs = contactUs;
	}

	public int getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(int smtpPort) {
		this.smtpPort = smtpPort;
	}

	public boolean isSmtpUseTls() {
		return smtpUseTls;
	}

	public void setSmtpUseTls(boolean smtpUseTls) {
		this.smtpUseTls = smtpUseTls;
	}
}
