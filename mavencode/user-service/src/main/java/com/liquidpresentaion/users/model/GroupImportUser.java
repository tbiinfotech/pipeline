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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "lp_group_import_user")
@JsonIdentityInfo(generator=JSOGGenerator.class)
public class GroupImportUser {
	private static final Logger logger = LoggerFactory.getLogger(GroupImportUser.class);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pk_id", nullable = false)
	private int pkId;

	@ManyToOne
	@JoinColumn(name = "distributor_id", nullable = false)
	private Group distributorId;

	@ManyToOne
	@JoinColumn(name = "sales_group_id", nullable = false)
	private Group group;
	
	//@Column(name = "sales_group_id")
	//private Integer group;

	@Column(name = "account_id")
	private String accountId;

	@Column(name = "account_name")
	private String name;

	@Column(name = "account_address")
	private String address;

	public Integer getPkId() {
		logger.info("Test --- pkId : "+ pkId);
		return pkId;
	}

	public void setPkId(Integer pkId) {
		this.pkId = pkId;
	}

	public Group getGroup() {
		logger.info("group : "+ group);
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	/*public Integer getGroup() {
		logger.info("group : "+ group);
		return group;
	}

	public void setGroup(Integer group) {
		this.group = group;
	}*/

	public Group getDistributorId() {
		return distributorId;
	}

 	public void setDistributorId(Group distributorId) {
		this.distributorId = distributorId;
	}

	public String getAccountId() {
		return accountId;
	}
	
	public void setAccountId(String accountId) {
		logger.info("accountId : " + accountId);
		this.accountId = accountId;
	}

	public String getName() {
		return name;
	}

 	public void setName(String name) {
		logger.info("name : " + name);
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	// public void setData(String data) {

	// 	logger.info("address : " + data);
	// 	String[] lines = data.split("\\r?\\n");
	// 	//int count = 1;
	// 	for (String line : lines) {
	// 		logger.info("lines : " + lines);
	// 		//if(count>1){
	// 			String[] detail = line.split(",");
	// 			logger.info("line : " + detail[0]);
	// 			logger.info("line1 : " + detail[1]);
	// 			logger.info("line2 : " + detail[2]);
	// 			//this.distributorId = 1;
	// 			//this.group = 1;
	// 			this.accountId = detail[0];
	// 			this.name = detail[1];
	// 			this.address = detail[2];
	// 		//}
	// 		//count ++;
    //     }
	// }

	@Override
	public int hashCode() {
		int result = 22;
		result = 37 * result + (this.group == null? 0: this.group.hashCode());
		result = 37 * result + (this.distributorId == null? 0: this.distributorId.hashCode());
		result = 37 * result + (this.accountId == null? 0: this.accountId.hashCode());
		result = 37 * result + (this.name == null? 0: this.name.hashCode());
		result = 37 * result + (this.address == null? 0: this.address.hashCode());
		
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		boolean eq = false;
		if (obj instanceof GroupImportUser) {
			GroupImportUser groupImportUser = (GroupImportUser) obj;
			if (
				(this.group.getPkId() == groupImportUser.getGroup().getPkId()) 
					&& this.distributorId.getPkId() == groupImportUser.getDistributorId().getPkId()
					// this.group.equals(groupImportUser.getGroup())
					// && this.distributorId.equals(groupImportUser.getDistributorId())
					&& this.accountId.equals(groupImportUser.getAccountId())
					&& this.name.equals(groupImportUser.getName())
					&& this.address.equals(groupImportUser.getAddress())) {
				eq = true;
			}
		}
		return eq;
	}
}
