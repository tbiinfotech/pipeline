package com.liquidpresentaion.users.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.liquidpresentation.common.BaseSpiritCategory;
import com.liquidpresentation.common.Category;
import com.liquidpresentation.common.Constants;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

@Entity
@Table(name = "lp_cocktail")
@JsonIdentityInfo(generator=JSOGGenerator.class)
public class Cocktail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pk_id", nullable = false)
	private int id;

	@Column(name = "ct_supplier_pkid")
	private Integer supplierPkId;
	
	@Column(name = "ct_published")
	private boolean published;

	@Column(name = "ct_brand_pkid")
	private Integer brandPkId;
	
	@Column(name = "ct_create_user_pkid")
	@CreatedBy
	private Integer createPkId;
	
	@Column(name = "ct_mixologist_pkid")
	private Integer mixologistPkId;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getSupplierPkId() {
		return supplierPkId;
	}

	public void setSupplierPkId(Integer supplierPkId) {
		this.supplierPkId = supplierPkId;
	}

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public Integer getBrandPkId() {
		return brandPkId;
	}

	public void setBrandPkId(Integer brandPkId) {
		this.brandPkId = brandPkId;
	}

	public Integer getCreatePkId() {
		return createPkId;
	}

	public void setCreatePkId(Integer createPkId) {
		this.createPkId = createPkId;
	}
	
	public Integer getMixologistPkId() {
		return mixologistPkId;
	}

	public void setMixologistPkId(Integer mixologistPkId) {
		this.mixologistPkId = mixologistPkId;
	}
	
	//******************
	@Column(name = "ct_name")
	private String name;
	@Column(name = "ct_brand_name")
	private String brandName;
	@Transient
	private String supplierName;
	@Enumerated(EnumType.STRING)
	@Column(name = "ct_base_spirit_category")
	private BaseSpiritCategory baseSpiritCategory;
	@Column(name = "ct_diff_degree")
	private Integer degreeOfDiff;
	@Column(name = "ct_image")
	private String image;
	@Column(name = "ct_garnish")
	private String garnish;
	@Column(name = "ct_method")
	private String method;
	@Column(name = "ct_comments")
	private String comments;
	@Column(name = "ct_mixologist_name")
	private String mixologistName;
	@Column(name = "ct_create_timestamp", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMATE)
	@CreatedDate
	private Date createDate;
	@Column(name = "ct_update_user_pkid")
	@LastModifiedBy
	private Integer updatePkId;
	@Column(name = "ct_update_timestamp", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@LastModifiedDate
	private Date updateDate;
	@Column(name = "ct_ingredientsnumber", nullable = true)
	private Integer ingredientsnumber;
	@Column(name = "ct_archiveFlag")
	private Integer archiveFlag;

//	@OneToMany(mappedBy = "cocktail", cascade = {CascadeType.ALL}, orphanRemoval = true)
	@Transient
	private Set<CocktailCategory> categorySet = new HashSet<>();
	
//	@OneToMany(mappedBy = "cocktail", cascade = {CascadeType.ALL}, orphanRemoval = true)
	@Transient
	private Set<CocktailFlavorProfile> flavorProfileSet = new HashSet<>();
	
//	@OneToMany(mappedBy = "cocktail", cascade = {CascadeType.ALL}, orphanRemoval = true)
	@Transient
	private Set<CocktailOutletType> outletTypeSet = new HashSet<>();
	
//	@OneToMany(mappedBy = "cocktail", cascade = {CascadeType.ALL}, orphanRemoval = true)
	@Transient
	private Set<CocktailSeasonalThemed> seasonalThemedSet = new HashSet<>();
	
//	@OneToMany(mappedBy = "cocktail", cascade = {CascadeType.ALL}, orphanRemoval = true)
	@Transient
	private Set<CocktailGlassStyle> glassStypleSet = new HashSet<>();

//	@OneToMany(fetch=FetchType.LAZY, mappedBy = "cocktail", cascade = {CascadeType.ALL}, orphanRemoval = true)
	@Transient
	private Set<CocktailBrand> brandSet = new HashSet<>();

	@ManyToMany(targetEntity = Group.class, cascade = {CascadeType.REFRESH})
	@JoinTable(name = "lp_cocktail_group", joinColumns = {@JoinColumn(name = "ctg_cocktail_pkid")}, 
													inverseJoinColumns = {@JoinColumn(name = "ctg_group_pkid")})
	private List<Group> groups = new ArrayList<>();

	@ManyToOne(targetEntity = Group.class)
	@JoinColumn(name = "ct_supplier_pkid", insertable=false, updatable=false)
	@NotFound(action = NotFoundAction.IGNORE)
	private Group supplierGroup;
	
	@ManyToOne(targetEntity = Brand.class)
	@JoinColumn(name = "ct_brand_pkid", insertable=false, updatable=false)
	@NotFound(action = NotFoundAction.IGNORE)
	private Brand brand;
	
	
	
	
	@Transient
	private String baseSpirit;
	@Transient
	private String baseSpiritCategoryString;
	@Transient
	private String juice;
	@Transient
	private String sweetener;
	@Transient
	private String solids;
	@Transient
	private String defaultImage;
	@Transient
	private String deletedBrand;
	@Transient
	private Integer isCocktailDeletedBrand;
	
	public String getDefaultImage() {
		if (this.brand != null) {
			return this.brand.getDefaultImage();
		}
		return defaultImage;
	}

	public void setDefaultImage(String defaultImage) {
		this.defaultImage = defaultImage;
	}

	public String getBaseSpirit() {
		/*brandSet.forEach(bs-> {
			if(Category.BASESPIRIT.equals(bs.getCategory())) 
			{
				baseSpirit = StringUtils.isEmpty(baseSpirit) ? bs.getBrandName() : (baseSpirit + ", " + bs.getBrandName());
			}
		});*/
		String baseSpiritTemp = "";
		for (CocktailBrand bs : brandSet ) {
		if(Category.BASESPIRIT.equals(bs.getCategory())) 
			{
			baseSpiritTemp = StringUtils.isEmpty(baseSpiritTemp) ? bs.getBrandName() : (baseSpiritTemp + ", " + bs.getBrandName());
			}
		}
		return baseSpiritTemp;
	}

	public void setBaseSpirit(String baseSpirit) {
		this.baseSpirit = baseSpirit;
	}

	public String getJuice() {
		/*brandSet.forEach(bs-> {
			if(Category.JUICE.equals(bs.getCategory())) 
			{
				juice = StringUtils.isEmpty(juice) ? bs.getBrandName() : (juice + ", " + bs.getBrandName());
			}
		});*/
		String juiceTemp = "";
		for (CocktailBrand bs : brandSet ) {
			if(Category.JUICE.equals(bs.getCategory())) 
			{
				juiceTemp = StringUtils.isEmpty(juiceTemp) ? bs.getBrandName() : (juiceTemp + ", " + bs.getBrandName());
			}
		}
		return juiceTemp;
	}

	public void setJuice(String juice) {
		this.juice = juice;
	}

	public String getSweetener() {
		/*brandSet.forEach(bs-> {
			if(Category.SWEETENER.equals(bs.getCategory())) 
			{
				sweetener = StringUtils.isEmpty(sweetener) ? bs.getBrandName() : (sweetener + ", " + bs.getBrandName());
			}
		});*/
		String sweetenerTemp = "";
		for (CocktailBrand bs : brandSet ) {
		if(Category.SWEETENER.equals(bs.getCategory())) 
			{
				sweetenerTemp = StringUtils.isEmpty(sweetenerTemp) ? bs.getBrandName() : (sweetenerTemp + ", " + bs.getBrandName());
			}
		}
		return sweetenerTemp;
	}

	public void setSweetener(String sweetener) {
		this.sweetener = sweetener;
	}

	public String getSolids() {
		/*brandSet.forEach(bs-> {
			if(Category.SOLIDS.equals(bs.getCategory())) 
			{
				solids = StringUtils.isEmpty(solids) ? bs.getBrandName() : (solids + ", " + bs.getBrandName());
			}
		});*/
		String solidsTemp = "";
		for (CocktailBrand bs : brandSet ) {
		if(Category.SOLIDS.equals(bs.getCategory())) 
			{
			solidsTemp = StringUtils.isEmpty(solidsTemp) ? bs.getBrandName() : (solidsTemp + ", " + bs.getBrandName());
			}
		}
		return solidsTemp;
	}

	public void setSolids(String solids) {
		this.solids = solids;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getSupplierName() {
		if(supplierGroup != null) {
			return supplierGroup.getName();
		}
		return null;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public BaseSpiritCategory getBaseSpiritCategory() {
		return baseSpiritCategory;
	}

	public void setBaseSpiritCategory(BaseSpiritCategory baseSpiritCategory) {
		this.baseSpiritCategory = baseSpiritCategory;
	}

	public Integer getDegreeOfDiff() {
		return degreeOfDiff;
	}

	public void setDegreeOfDiff(Integer degreeOfDiff) {
		this.degreeOfDiff = degreeOfDiff;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getGarnish() {
		return garnish;
	}

	public void setGarnish(String garnish) {
		this.garnish = garnish;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getMixologistName() {
		return mixologistName;
	}

	public void setMixologistName(String mixologistName) {
		this.mixologistName = mixologistName;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Integer getUpdatePkId() {
		return updatePkId;
	}

	public void setUpdatePkId(Integer updatePkId) {
		this.updatePkId = updatePkId;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Set<CocktailCategory> getCategorySet() {
		return categorySet;
	}

	public void setCategorySet(Set<CocktailCategory> categorySet) {
		this.categorySet = categorySet;
	}

	public Set<CocktailFlavorProfile> getFlavorProfileSet() {
		return flavorProfileSet;
	}

	public void setFlavorProfileSet(Set<CocktailFlavorProfile> flavorProfileSet) {
		this.flavorProfileSet = flavorProfileSet;
	}

	public Set<CocktailOutletType> getOutletTypeSet() {
		return outletTypeSet;
	}

	public void setOutletTypeSet(Set<CocktailOutletType> outletTypeSet) {
		this.outletTypeSet = outletTypeSet;
	}

	public Set<CocktailSeasonalThemed> getSeasonalThemedSet() {
		return seasonalThemedSet;
	}

	public void setSeasonalThemedSet(Set<CocktailSeasonalThemed> seasonalThemedSet) {
		this.seasonalThemedSet = seasonalThemedSet;
	}

	public Set<CocktailGlassStyle> getGlassStypleSet() {
		return glassStypleSet;
	}

	public void setGlassStypleSet(Set<CocktailGlassStyle> glassStypleSet) {
		this.glassStypleSet = glassStypleSet;
	}

	public Set<CocktailBrand> getBrandSet() {
		return brandSet;
	}

	public void setBrandSet(Set<CocktailBrand> brandSet) {
		this.brandSet = brandSet;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public void addAccessGroup(Group group){
		for (Iterator<Group> iterator = this.groups.iterator(); iterator.hasNext();) {
			if (group.getPkId() == iterator.next().getPkId()) {
				return;
			}
		}
		this.groups.add(group);
	}
	
	public void removeAccessGroup(Group group){
		for (Iterator<Group> iterator = this.groups.iterator(); iterator.hasNext();) {
			if (group.getPkId() == iterator.next().getPkId()) {
				iterator.remove();
			}
		}
	}
	
	public Group getSupplierGroup() {
		return supplierGroup;
	}

	public void setSupplierGroup(Group supplierGroup) {
		this.supplierGroup = supplierGroup;
	}

	public Integer getIngredientsnumber() {
		return ingredientsnumber;
	}

	public void setIngredientsnumber(Integer ingredientsnumber) {
		this.ingredientsnumber = ingredientsnumber;
	}

	public String getBaseSpiritCategoryString() {
		return this.baseSpiritCategory == null ? null : this.baseSpiritCategory.toString();
	}

	public void setBaseSpiritCategoryString(String baseSpiritCategoryString) {
		this.baseSpiritCategoryString = baseSpiritCategoryString;
	}
	
	public String getCocktailCategory() {
		Set<CocktailCategory> set = this.getCategorySet();
		List<String> cocktailCategory = set.stream().map(c->c.getCategoryString()).collect(Collectors.toList());
		return StringUtils.join(cocktailCategory, ",");
	}
	
	public String getSeasonalThemed() {
		Set<CocktailSeasonalThemed> set = this.getSeasonalThemedSet();
		List<String> cocktailSeasonalThemed = set.stream().map(s->s.getSeasonalThemedString()).collect(Collectors.toList());
		return StringUtils.join(cocktailSeasonalThemed, ",");
	}
	
	public String getFlavorProfile() {
		Set<CocktailFlavorProfile> set = this.getFlavorProfileSet();
		List<String> cocktailFlavorProfile = set.stream().map(c->c.getFlavorProfileString()).collect(Collectors.toList());
		return StringUtils.join(cocktailFlavorProfile, ",");
	}
	
	public String getGlassStyple() {
		Set<CocktailGlassStyle> set = this.getGlassStypleSet();
		List<String> glassStyle = set.stream().map(c->c.getGlassStyleString()).collect(Collectors.toList());
		return StringUtils.join(glassStyle, ",");
	}
	
	public String getOutletType() {
		Set<CocktailOutletType> set = this.getOutletTypeSet();
		List<String> outletType = set.stream().map(c->c.getOutletTypeString()).collect(Collectors.toList());
		return StringUtils.join(outletType, ",");
	}

	public Integer getArchiveFlag() {
		return archiveFlag;
	}

	public void setArchiveFlag(Integer archiveFlag) {
		this.archiveFlag = archiveFlag;
	}

	public String getDeletedBrand() {
		return deletedBrand;
	}

	public void setDeletedBrand(String deletedBrand) {
		this.deletedBrand = deletedBrand;
	}

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	public Integer getIsCocktailDeletedBrand() {
		if (this.brand != null) {
			return this.brand.getDeleted();
		}
		return isCocktailDeletedBrand;
	}

	public void setIsCocktailDeletedBrand(Integer isCocktailDeletedBrand) {
		this.isCocktailDeletedBrand = isCocktailDeletedBrand;
	}

}
