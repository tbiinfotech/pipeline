package com.liquidpresentaion.managementservice.model;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;

@SqlResultSetMapping(name = "TeamReviewResult",
classes = {
        @ConstructorResult(targetClass = ReviewTeam.class,
                columns = {
                        @ColumnResult(name = "pk_id"),
                        @ColumnResult(name = "fullname"),
                        @ColumnResult(name = "ps_Count")})
})
@Entity
public class ReviewTeam {

	@Id
	@Column(name = "pk_id")
	private long pkId;
	@Column(name = "fullname")
	private String fullname;
	@Column(name = "ps_Count")
	private int psCount;
	@Column(name = "ps_Count_last_year")
	private int psCountLastYear;
	@Column(name = "ps_Count_current_month")
	private int psCountCurrentMonth;
	@Column(name = "variance")
	private int variance;
	@Column(name = "variancepercent")
	private long variancepercent;

	public long getPkId() {
		return pkId;
	}

	public void setPkId(long pkId) {
		this.pkId = pkId;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public int getPsCount() {
		return psCount;
	}

	public void setPsCount(int psCount) {
		this.psCount = psCount;
	}

	public int getPsCountLastYear() {
		return psCountLastYear;
	}

	public void setPsCountLastYear(int psCountLastYear) {
		this.psCountLastYear = psCountLastYear;
	}

	public int getPsCountCurrentMonth() {
		return psCountCurrentMonth;
	}

	public void setPsCountCurrentMonth(int psCountCurrentMonth) {
		this.psCountCurrentMonth = psCountCurrentMonth;
	}

	public int getVariance() {
		return variance;
	}

	public void setVariance(int variance) {
		this.variance = variance;
	}

	public long getVariancepercent() {
		return variancepercent;
	}

	public void setVariancepercent(long variancepercent) {
		this.variancepercent = variancepercent;
	}
}
