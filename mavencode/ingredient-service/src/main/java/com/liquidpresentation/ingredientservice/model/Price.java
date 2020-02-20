package com.liquidpresentation.ingredientservice.model;

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

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.liquidpresentation.common.BaseSpiritCategory;
import com.liquidpresentation.common.Category;
import com.opencsv.bean.CsvBindByPosition;


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
	
	@Column(name="pr_category",insertable = false, updatable = false)
	private String prCategoryString;
	
	@Enumerated(EnumType.STRING)
	@Column(name="pr_base_spirit_category")
	private BaseSpiritCategory prBaseCategory;

	@Column(name="pr_create_timestamp", insertable=false, updatable=false)
	private Timestamp prCreateTimestamp;

	@Column(name="pr_create_user_pkid", updatable=false)
	private Long prCreateUserPkid;

	@Column(name="pr_ingredient_pkid")
	@CsvBindByPosition(position = 0)
	private Long prIngredientPkid;

	@Column(name="pr_mpc")
	@CsvBindByPosition(position = 1)
	private String prMpc;

	@Transient
	@CsvBindByPosition(position = 2)
	private String prSupplierName;
	
	@Column(name="pr_supplier_group_pkid")
	private Long prSupplierGroupPkid;
	
	@Transient
	@CsvBindByPosition(position = 3)
	private String distSupplierId;
	
	@Column(name="pr_distributor_pkid")
	private Long prDistributorPkid;

	@Transient
	@CsvBindByPosition(position = 4)
	private String csvPrDistributorPkid;
	
	@Column(name="pr_distribtor_item_code")
	@CsvBindByPosition(position = 5)
	private String prDistribtorItemCode;

	@Column(name="pr_sales_group_pkid")
	private Long prSalesGroupPkid;

	@Transient
	@CsvBindByPosition(position = 6)
	private String csvPrSalesGroupPkid;

	@Column(name="pr_state")
	@CsvBindByPosition(position = 7)
	private String prState;

	@Column(name="pr_description")
	@CsvBindByPosition(position = 8)
	private String prDescription;

	@Transient
	@CsvBindByPosition(position = 9)
	private String dcCategory;

	@ManyToOne
	@JoinColumn(name="pr_supplier_group_pkid", insertable=false, updatable=false)
	private Group prSupplierGroup;

	@Column(name="pr_size")
	private BigDecimal prSize;

	@Transient
	@CsvBindByPosition(position = 10)
	private String csvPrSize;
	
	@Column(name="pr_uom")
	@CsvBindByPosition(position = 11)
	private String prUom;

	@Transient
	private String csvPrUom;

	@Column(name="pr_case_pack")
	private Integer prCasePack;

	@Transient
	@CsvBindByPosition(position = 12)
	private String csvPrCasePack;

	@Column(name="pr_case_price")
	private BigDecimal prCasePrice;

	@Transient
	@CsvBindByPosition(position = 13)
	private String csvPrCasePrice;

	@Transient
	private BigDecimal prBottlePrice;

	@Transient
	@CsvBindByPosition(position = 14)
	private String csvPrBottlePrice;

	@Transient
	@CsvBindByPosition(position = 15)
	private String prDistributorBrandId;
	
	@Transient
	@CsvBindByPosition(position = 16)
	private String prDistributorBrandDescription;
	
	@Column(name="pr_upc")
	private String prUpc;

	@Column(name="pr_update_timestamp", insertable=false)
	private Timestamp prUpdateTimestamp;

	@Column(name="pr_update_user_pkid")
	private Long prUpdateUserPkid;
	
	@ManyToOne(targetEntity = Ingredient.class)
	@JoinColumn(name = "pr_ingredient_pkid", insertable=false, updatable=false)
	@NotFound(action = NotFoundAction.IGNORE)
	private Ingredient ingredient;
	
	@Transient
	private Long brandPkid;

	@Transient
	private Long oldSalesGroupPkid;

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
		return this.prDescription == null? null : this.prDescription.trim();
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
		if(this.prSupplierGroup != null) {
			return this.prSupplierGroup.getName();
		}
		return this.prSupplierName;
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
		return prDistributorBrandDescription == null? null:prDistributorBrandDescription.trim();
	}

	public void setPrDistributorBrandDescription(String prDistributorBrandDescription) {
		this.prDistributorBrandDescription = prDistributorBrandDescription;
	}
	
	
	/*brandPkid传值一直为0
	 * 修改好了
	*/
	public Long getBrandPkid() {
		return ingredient != null ? ingredient.getInBrandPkid() : null;
	}

	public void setBrandPkid(Long brandPkid) {
		this.brandPkid = brandPkid;
	}
	
	public String getDistSupplierId() {
		return distSupplierId;
	}

	public void setDistSupplierId(String distSupplierId) {
		this.distSupplierId = distSupplierId;
	}

	private String quoteContent(String content){
		return "\"" + content +  "\"";
	}
	
	public Long getOldSalesGroupPkid() {
		return oldSalesGroupPkid;
	}

	public void setOldSalesGroupPkid(Long oldSalesGroupPkid) {
		this.oldSalesGroupPkid = oldSalesGroupPkid;
	}

	@JsonIgnore
	public PriceCsv getPriceCsv(String batchId, String failureMessage){
		PriceCsv csv = new PriceCsv();
		StringBuffer sb = new StringBuffer();
		sb.append(this.prIngredientPkid==null?"":this.prIngredientPkid.toString()).append(",");
		sb.append(this.prMpc).append(",");
		sb.append(this.prSupplierName==null?"":this.quoteContent(this.prSupplierName.trim())).append(",");
		sb.append(this.distSupplierId).append(",\"");
		sb.append(this.csvPrDistributorPkid).append("\",");
		sb.append(this.prDistribtorItemCode).append(",\"");
		sb.append(this.csvPrSalesGroupPkid).append("\",");
		sb.append(this.prState).append(",");
		sb.append(this.prDescription==null?"":this.quoteContent(this.prDescription.trim())).append(",");
		sb.append(this.prCategory == null ? "" : this.prCategory).append(",");
		sb.append(this.csvPrSize).append(",");
		sb.append(this.csvPrUom).append(",\"");
		sb.append(this.csvPrCasePack).append("\",\"");
		sb.append(this.csvPrCasePrice).append("\",\"");
		sb.append(this.csvPrBottlePrice).append("\",");
		sb.append(this.prDistributorBrandId);
		
		csv.setBatchId(batchId);
		csv.setFailureMessage(failureMessage);
		csv.setCsvRecord(sb.toString());
		csv.setPrDistributorPkid(this.getPrDistributorPkid());
		csv.setDistSupplierId(this.distSupplierId);
		return csv;
	}

	@JsonIgnore
	public PriceCsv getPriceWarningCsv(String batchId, String warningMessage){
		PriceCsv csv = new PriceCsv();
		StringBuffer sb = new StringBuffer();
		sb.append(this.prIngredientPkid==null?"":this.prIngredientPkid.toString()).append(",");
		sb.append(this.prMpc).append(",");
		sb.append(this.prSupplierName==null?"":this.quoteContent(this.prSupplierName.trim())).append(",");
		sb.append(this.distSupplierId).append(",\"");
		sb.append(this.csvPrDistributorPkid).append("\",");
		sb.append(this.prDistribtorItemCode).append(",\"");
		sb.append(this.csvPrSalesGroupPkid).append("\",");
		sb.append(this.prState).append(",");
		sb.append(this.prDescription==null?"":this.quoteContent(this.prDescription.trim())).append(",");
		sb.append(this.prCategory == null ? "" : this.prCategory).append(",");
		sb.append(this.csvPrSize).append(",");
		sb.append(this.csvPrUom).append(",\"");
		sb.append(this.csvPrCasePack).append("\",\"");
		sb.append(this.csvPrCasePrice).append("\",\"");
		sb.append(this.csvPrBottlePrice).append("\",");
		sb.append(this.prDistributorBrandId);
		
		csv.setBatchId(batchId);
		csv.setWarningMessage(warningMessage);
		csv.setCsvRecord(sb.toString());
		return csv;
	}

	public Ingredient getIngredient() {
		return ingredient;
	}

	public void setIngredient(Ingredient ingredient) {
		this.ingredient = ingredient;
	}

	public String getPrCategoryString() {
		return prCategoryString;
	}

	public void setPrCategoryString(String prCategoryString) {
		this.prCategoryString = prCategoryString;
	}
	
	public boolean mappedSupplier(){
		return this.prSupplierGroupPkid != null && this.prSupplierGroupPkid > 0;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.pkId).append(",");
		sb.append(this.prIngredientPkid==null?"prIngredientPkid not found":this.prIngredientPkid.toString()).append(",");
		sb.append(this.prMpc).append(",");
		sb.append(this.prSupplierName==null?"":this.quoteContent(this.prSupplierName.trim())).append(",");
		sb.append(this.distSupplierId).append(",");
		sb.append(this.prDistributorPkid).append(",");
		sb.append(this.prDistribtorItemCode).append(",");
		sb.append(this.oldSalesGroupPkid).append(",");
		sb.append(this.prState).append(",");
		sb.append(this.prDescription==null?"":this.quoteContent(this.prDescription.trim())).append(",");
		sb.append(this.prCategory).append(",");
		sb.append(this.prSize).append(",");
		sb.append(this.prUom).append(",");
		sb.append(this.prCasePack).append(",");
		sb.append(this.prCasePrice).append(",");
		sb.append(this.prBottlePrice).append(",");
		sb.append(this.prDistributorBrandId);
		
		return sb.toString();
	}

	public String getCsvPrSize() {
		return csvPrSize;
	}

	public void setCsvPrSize(String csvPrSize) {
		this.csvPrSize = csvPrSize;
	}

	public String getCsvPrUom() {
		return csvPrUom;
	}

	public void setCsvPrUom(String csvPrUom) {
		this.csvPrUom = csvPrUom;
	}

	public String getCsvPrDistributorPkid() {
		return csvPrDistributorPkid;
	}

	public void setCsvPrDistributorPkid(String csvPrDistributorPkid) {
		this.csvPrDistributorPkid = csvPrDistributorPkid;
	}

	public String getCsvPrSalesGroupPkid() {
		return csvPrSalesGroupPkid;
	}

	public void setCsvPrSalesGroupPkid(String csvPrSalesGroupPkid) {
		this.csvPrSalesGroupPkid = csvPrSalesGroupPkid;
	}

	public String getCsvPrCasePack() {
		return csvPrCasePack;
	}

	public void setCsvPrCasePack(String csvPrCasePack) {
		this.csvPrCasePack = csvPrCasePack;
	}

	public String getCsvPrCasePrice() {
		return csvPrCasePrice;
	}

	public void setCsvPrCasePrice(String csvPrCasePrice) {
		this.csvPrCasePrice = csvPrCasePrice;
	}

	public String getCsvPrBottlePrice() {
		return csvPrBottlePrice;
	}

	public void setCsvPrBottlePrice(String csvPrBottlePrice) {
		this.csvPrBottlePrice = csvPrBottlePrice;
	}
}