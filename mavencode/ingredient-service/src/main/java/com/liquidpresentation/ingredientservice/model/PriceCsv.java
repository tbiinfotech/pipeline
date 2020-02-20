package com.liquidpresentation.ingredientservice.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.opencsv.bean.CsvBindByPosition;

@Entity
@Table(name="lp_ingredientpricing_csv")
public class PriceCsv {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="pk_id")
	private Long pkId;

	@Column(name="ic_batch_id")
	@CsvBindByPosition(position = 0)
	private String batchId;

	@Column(name="ic_csv_record")
	private String csvRecord;

	@Column(name="ic_failure_message")
	@CsvBindByPosition(position = 1)
	private String failureMessage;

	@Column(name="ic_warning_message")
	@CsvBindByPosition(position = 2)
	private String warningMessage;

	@Column(name="ic_create_timestamp", insertable=false, updatable=false)
	private Timestamp prCreateTimestamp;

	@Column(name="ic_create_user_pkid", updatable=false)
	private Long prCreateUserPkid;
	
	@Column(name="ic_update_timestamp", insertable=false)
	private Timestamp prUpdateTimestamp;

	@Column(name="ic_update_user_pkid")
	private Long prUpdateUserPkid;

	@Transient
	private Long prDistributorPkid;
	@Transient
	private String distSupplierId;
	
	public Long getPkId() {
		return pkId;
	}

	public void setPkId(Long pkId) {
		this.pkId = pkId;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public String getCsvRecord() {
		return csvRecord;
	}

	public void setCsvRecord(String csvRecord) {
		this.csvRecord = csvRecord;
	}

	public String getFailureMessage() {
		return failureMessage;
	}

	public void setFailureMessage(String failureMessage) {
		this.failureMessage = failureMessage;
	}

	public String getWarningMessage() {
		return warningMessage;
	}

	public void setWarningMessage(String warningMessage) {
		this.warningMessage = warningMessage;
	}

	public Timestamp getPrCreateTimestamp() {
		return prCreateTimestamp;
	}

	public void setPrCreateTimestamp(Timestamp prCreateTimestamp) {
		this.prCreateTimestamp = prCreateTimestamp;
	}

	public Long getPrCreateUserPkid() {
		return prCreateUserPkid;
	}

	public void setPrCreateUserPkid(Long prCreateUserPkid) {
		this.prCreateUserPkid = prCreateUserPkid;
	}

	public Timestamp getPrUpdateTimestamp() {
		return prUpdateTimestamp;
	}

	public void setPrUpdateTimestamp(Timestamp prUpdateTimestamp) {
		this.prUpdateTimestamp = prUpdateTimestamp;
	}

	public Long getPrUpdateUserPkid() {
		return prUpdateUserPkid;
	}

	public void setPrUpdateUserPkid(Long prUpdateUserPkid) {
		this.prUpdateUserPkid = prUpdateUserPkid;
	}

	public Long getPrDistributorPkid() {
		return prDistributorPkid;
	}

	public void setPrDistributorPkid(Long prDistributorPkid) {
		this.prDistributorPkid = prDistributorPkid;
	}

	public String getDistSupplierId() {
		return distSupplierId;
	}

	public void setDistSupplierId(String distSupplierId) {
		this.distSupplierId = distSupplierId;
	}
}
