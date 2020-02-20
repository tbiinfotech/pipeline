package com.liquidpresentaion.managementservice.model;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;

@SqlResultSetMapping(name = "CocktailReviewResult",
classes = {
        @ConstructorResult(targetClass = ReviewTeam.class,
                columns = {
                        @ColumnResult(name = "pk_id"),
                        @ColumnResult(name = "ct_name"),
                        @ColumnResult(name = "ct_brand_pkid"),
                        @ColumnResult(name = "ct_supplier_pkid"),
                        @ColumnResult(name = "brand_Name"),
                        @ColumnResult(name = "supplier_Group_Name"),
                        @ColumnResult(name = "ps_Count"),
                        @ColumnResult(name = "ps_Count_last_year"),
                        @ColumnResult(name = "ps_Count_current_month")})
})
@Entity
public class ReviewCocktail {

	@Id
	@Column(name = "pk_id")
	private long pkId;
	@Column(name = "ct_name")
	private String cocktailName;
	@Column(name = "ct_brand_pkid")
	private Integer brandPkid;
	@Column(name = "ct_supplier_pkid")
	private Integer supplierGroupPkid;
	@Column(name = "brand_Name")
	private String brandName;
	@Column(name = "supplier_Group_Name")
	private String supplierGroupName;
	@Column(name = "ps_Count")
	private int psCount;
	@Column(name = "ps_Count_last_year")
	private int psCountLastYear;
	@Column(name = "ps_Count_current_month")
	private int psCountCurrentMonth;

	public long getPkId() {
		return pkId;
	}

	public void setPkId(long pkId) {
		this.pkId = pkId;
	}

	public String getCocktailName() {
		return cocktailName;
	}

	public void setCocktailName(String cocktailName) {
		this.cocktailName = cocktailName;
	}

	public Integer getBrandPkid() {
		return brandPkid;
	}

	public void setBrandPkid(Integer brandPkid) {
		this.brandPkid = brandPkid;
	}

	public Integer getSupplierGroupPkid() {
		return supplierGroupPkid;
	}

	public void setSupplierGroupPkid(Integer supplierGroupPkid) {
		this.supplierGroupPkid = supplierGroupPkid;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getSupplierGroupName() {
		return supplierGroupName;
	}

	public void setSupplierGroupName(String supplierGroupName) {
		this.supplierGroupName = supplierGroupName;
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

	public int getVariance(){
		return this.psCount - this.psCountLastYear;
	}
	
	public String getVariancePercentage(){
		if (this.psCountLastYear == 0) {
			return this.psCount == 0 ? "0%" : this.psCount + "00%";
		} else {
			return Math.round(this.getVariance() / this.psCountLastYear * 100 ) + "%";
		}
	}
}
