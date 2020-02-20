package com.liquidpresentaion.cocktailservice.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;

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
import javax.persistence.Transient;

import com.liquidpresentation.common.BaseSpiritCategory;
import com.liquidpresentation.common.Category;


/**
 * The persistent class for the lp_brandpricing database table.
 * 
 */
@Entity
@Table(name="lp_ingredientpricing")
public class Price  {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="pk_id")
	private Long pkId;

	@Enumerated(EnumType.STRING)
	@Column(name="pr_category")
	private Category prCategory;
	
	@Enumerated(EnumType.STRING)
	@Column(name="pr_base_spirit_category")
	private BaseSpiritCategory prBaseCategory;

	@Column(name="pr_create_timestamp", insertable=false, updatable=false)
	private Timestamp prCreateTimestamp;

	@Column(name="pr_create_user_pkid", updatable=false)
	private Long prCreateUserPkid;

	@Column(name="pr_ingredient_pkid")
	private Long prIngredientPkid;

	@Column(name="pr_mpc")
	private String prMpc;

	@Transient
	private String prSupplierName;
	
	@Column(name="pr_supplier_group_pkid")
	private Long prSupplierGroupPkid;
	
	@Column(name="pr_distributor_pkid")
	private Long prDistributorPkid;
	
	@Column(name="pr_distribtor_item_code")
	private String prDistribtorItemCode;

	@Column(name="pr_sales_group_pkid")
	private Long prSalesGroupPkid;
	
	@Column(name="pr_state")
	private String prState;

	@Column(name="pr_description")
	private String prDescription;

	@Transient
	private String dcCategory;

	@ManyToOne
	@JoinColumn(name="pr_supplier_group_pkid", insertable=false, updatable=false)
	private Group prSupplierGroup;

	@Column(name="pr_size")
	private BigDecimal prSize;
	
	@Column(name="pr_uom")
	private String prUom;

	@Column(name="pr_case_pack")
	private Integer prCasePack;

	@Column(name="pr_case_price")
	private BigDecimal prCasePrice;

	@Transient
	private BigDecimal prBottlePrice;
	
	@Transient
	private String prDistributorBrandId;
	
	@Transient
	private String prDistributorBrandDescription;
	
	@Column(name="pr_upc")
	private String prUpc;

	@Column(name="pr_update_timestamp", insertable=false)
	private Timestamp prUpdateTimestamp;

	@Column(name="pr_update_user_pkid")
	private Long prUpdateUserPkid;
	
	@Column(name="brandPkid")
	private Long brandPkid;

	public Price() {
	}

	public Long getPkId() {
		return this.pkId;
	}

	public void setPkId(Long pkId) {
		this.pkId = pkId;
	}

	public Integer getPrCasePack() {
		return this.prCasePack;
	}

	public void setPrCasePack(Integer prCasePack) {
		this.prCasePack = prCasePack;
	}

	public BigDecimal getPrCasePrice() {
		return this.prCasePrice;
	}

	public void setPrCasePrice(BigDecimal prCasePrice) {
		this.prCasePrice = prCasePrice;
	}

	public Category getPrCategory() {
		return this.prCategory;
	}

	public void setPrCategory(Category prCategory) {
		this.prCategory = prCategory;
	}

	public Timestamp getPrCreateTimestamp() {
		return this.prCreateTimestamp;
	}

	public void setPrCreateTimestamp(Timestamp prCreateTimestamp) {
		this.prCreateTimestamp = prCreateTimestamp;
	}

	public Long getPrCreateUserPkid() {
		return this.prCreateUserPkid;
	}

	public void setPrCreateUserPkid(Long prCreateUserPkid) {
		this.prCreateUserPkid = prCreateUserPkid;
	}

	public String getPrDescription() {
		return this.prDescription;
	}

	public void setPrDescription(String prDescription) {
		this.prDescription = prDescription;
	}

	public String getPrDistribtorItemCode() {
		return this.prDistribtorItemCode;
	}

	public void setPrDistribtorItemCode(String prDistribtorItemCode) {
		this.prDistribtorItemCode = prDistribtorItemCode;
	}

	public Long getPrDistributorPkid() {
		return this.prDistributorPkid;
	}

	public void setPrDistributorPkid(Long prDistributorPkid) {
		this.prDistributorPkid = prDistributorPkid;
	}

	public Long getPrIngredientPkid() {
		return this.prIngredientPkid;
	}

	public void setPrIngredientPkid(Long prIngredientPkid) {
		this.prIngredientPkid = prIngredientPkid;
	}

	public String getPrMpc() {
		return this.prMpc;
	}

	public void setPrMpc(String prMpc) {
		this.prMpc = prMpc;
	}

	public String getPrState() {
		return this.prState;
	}

	public void setPrState(String prState) {
		this.prState = prState;
	}

	public Long getPrSupplierGroupPkid() {
		return this.prSupplierGroupPkid;
	}

	public void setPrSupplierGroupPkid(Long prSupplierGroupPkid) {
		this.prSupplierGroupPkid = prSupplierGroupPkid;
	}

	public String getPrUom() {
		return this.prUom;
	}

	public void setPrUom(String prUom) {
		this.prUom = prUom;
	}

	public String getPrUpc() {
		return this.prUpc;
	}

	public void setPrUpc(String prUpc) {
		this.prUpc = prUpc;
	}

	public Timestamp getPrUpdateTimestamp() {
		return this.prUpdateTimestamp;
	}

	public void setPrUpdateTimestamp(Timestamp prUpdateTimestamp) {
		this.prUpdateTimestamp = prUpdateTimestamp;
	}

	public Long getPrUpdateUserPkid() {
		return this.prUpdateUserPkid;
	}

	public void setPrUpdateUserPkid(Long prUpdateUserPkid) {
		this.prUpdateUserPkid = prUpdateUserPkid;
	}

	public BigDecimal getPrSize() {
		return prSize;
	}

	public void setPrSize(BigDecimal prSize) {
		this.prSize = prSize;
	}

	public Long getPrSalesGroupPkid() {
		return prSalesGroupPkid;
	}

	public void setPrSalesGroupPkid(Long prSalesGroupPkid) {
		this.prSalesGroupPkid = prSalesGroupPkid;
	}
	
	public BaseSpiritCategory getPrBaseCategory() {
		return prBaseCategory;
	}

	public void setPrBaseCategory(BaseSpiritCategory prBaseCategory) {
		this.prBaseCategory = prBaseCategory;
	}

	public BigDecimal getPrBottlePrice() {
		if(this.prCasePrice!=null && this.prCasePrice.compareTo(BigDecimal.ZERO) > 0) {
			return this.prCasePrice.divide(new BigDecimal(this.prCasePack), 2, RoundingMode.HALF_DOWN);
		}
		return new BigDecimal(0);
	}

	public void setPrBottlePrice(BigDecimal prBottlePrice) {
		this.prBottlePrice = prBottlePrice;
	}

	public Group getPrSupplierGroup() {
		return prSupplierGroup;
	}

	public void setPrSupplierGroup(Group prSupplierGroup) {
		this.prSupplierGroup = prSupplierGroup;
	}

	public String getPrSupplierName() {
		return this.prSupplierGroup.getName();
	}

	public void setPrSupplierName(String prSupplierName) {
		this.prSupplierName = prSupplierName;
	}

	public String getCsvSupplierName() {
		return this.prSupplierName;
	}

	public String getDcCategory() {
		return dcCategory;
	}

	public void setDcCategory(String dcCategory) {
		this.dcCategory = dcCategory;
	}

	public String getPrDistributorBrandId() {
		return prDistributorBrandId;
	}

	public void setPrDistributorBrandId(String prDistributorBrandId) {
		this.prDistributorBrandId = prDistributorBrandId;
	}

	public String getPrDistributorBrandDescription() {
		return prDistributorBrandDescription;
	}

	public void setPrDistributorBrandDescription(String prDistributorBrandDescription) {
		this.prDistributorBrandDescription = prDistributorBrandDescription;
	}
	
	public void setBrandPkid(Long brandPkid) {
		this.brandPkid = brandPkid;
	}

	public Long getBrandPkid() {
		return brandPkid;
	}
}