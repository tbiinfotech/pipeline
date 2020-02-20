package com.liquidpresentaion.managementservice.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.liquidpresentation.common.BaseSpiritCategory;
import com.liquidpresentation.common.Category;

public class Cocktail {
	
	private int id;
	private String name;
	private int brandPkId;
	private String brandName;
	private int supplierPkId;
	private String supplierName;
	private BaseSpiritCategory baseSpiritCategory;
	private int degreeOfDiff;
	private String image;
	private String garnish;
	private String method;
	private String comments;
	private int mixologistPkId;
	private String mixologistName;
	private boolean published;
	private int createPkId;
	private Date createDate;
	private int updatePkId;
	private Date updateDate;

	private Set<CocktailCategory> categorySet = new HashSet<>();
	
	private Set<CocktailFlavorProfile> flavorProfileSet = new HashSet<>();
	
	private Set<CocktailOutletType> outletTypeSet = new HashSet<>();
	private Set<CocktailSeasonalThemed> seasonalThemedSet = new HashSet<>();
	
	private Set<CocktailGlassStyle> glassStypleSet = new HashSet<>();

	private Set<CocktailBrand> brandSet = new HashSet<>();

	private List<Group> groups = new ArrayList<>();

	private List<Presentation> presentations = new ArrayList<>();

	private Group supplierGroup;
	private String baseSpirit;
	private String juice;
	private String sweetener;
	private String solids;
	public String getBaseSpirit() {
		brandSet.forEach(bs-> {
			if(Category.BASESPIRIT.equals(bs.getCategory())) 
			{
				baseSpirit = StringUtils.isEmpty(baseSpirit) ? bs.getBrandName() : (baseSpirit + ", " + bs.getBrandName());
			}
		});
		return baseSpirit;
	}

	public void setBaseSpirit(String baseSpirit) {
		this.baseSpirit = baseSpirit;
	}

	public String getJuice() {
		brandSet.forEach(bs-> {
			if(Category.JUICE.equals(bs.getCategory())) 
			{
				juice = StringUtils.isEmpty(juice) ? bs.getBrandName() : (juice + ", " + bs.getBrandName());
			}
		});
		return juice;
	}

	public void setJuice(String juice) {
		this.juice = juice;
	}

	public String getSweetener() {
		brandSet.forEach(bs-> {
			if(Category.SWEETENER.equals(bs.getCategory())) 
			{
				sweetener = StringUtils.isEmpty(sweetener) ? bs.getBrandName() : (sweetener + ", " + bs.getBrandName());
			}
		});
		return sweetener;
	}

	public void setSweetener(String sweetener) {
		this.sweetener = sweetener;
	}

	public String getSolids() {
		brandSet.forEach(bs-> {
			if(Category.SOLIDS.equals(bs.getCategory())) 
			{
				solids = StringUtils.isEmpty(solids) ? bs.getBrandName() : (solids + ", " + bs.getBrandName());
			}
		});
		return solids;
	}

	public void setSolids(String solids) {
		this.solids = solids;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getBrandPkId() {
		return brandPkId;
	}

	public void setBrandPkId(int brandPkId) {
		this.brandPkId = brandPkId;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public int getSupplierPkId() {
		return supplierPkId;
	}

	public void setSupplierPkId(int supplierPkId) {
		this.supplierPkId = supplierPkId;
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

	public int getDegreeOfDiff() {
		return degreeOfDiff;
	}

	public void setDegreeOfDiff(int degreeOfDiff) {
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

	public int getMixologistPkId() {
		return mixologistPkId;
	}

	public void setMixologistPkId(int mixologistPkId) {
		this.mixologistPkId = mixologistPkId;
	}

	public String getMixologistName() {
		return mixologistName;
	}

	public void setMixologistName(String mixologistName) {
		this.mixologistName = mixologistName;
	}

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public int getCreatePkId() {
		return createPkId;
	}

	public void setCreatePkId(int createPkId) {
		this.createPkId = createPkId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public int getUpdatePkId() {
		return updatePkId;
	}

	public void setUpdatePkId(int updatePkId) {
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
	
	public List<Presentation> getPresentations() {
		return presentations;
	}

	public void setPresentations(List<Presentation> presentations) {
		this.presentations = presentations;
	}

	public Group getSupplierGroup() {
		return supplierGroup;
	}

	public void setSupplierGroup(Group supplierGroup) {
		this.supplierGroup = supplierGroup;
	}
}
