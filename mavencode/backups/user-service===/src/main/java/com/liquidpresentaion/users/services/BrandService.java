package com.liquidpresentaion.users.services;

import java.text.Collator;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.liquidpresentaion.users.clients.ManagementFeignClient;
import com.liquidpresentaion.users.context.UserContextHolder;
import com.liquidpresentaion.users.model.Brand;
import com.liquidpresentaion.users.model.Cocktail;
import com.liquidpresentaion.users.model.CocktailGroup;
import com.liquidpresentaion.users.model.Group;
import com.liquidpresentaion.users.model.GroupUser;
import com.liquidpresentaion.users.model.HousemadeBrand;
import com.liquidpresentaion.users.model.Ingredient;
import com.liquidpresentaion.users.repository.BrandRepository;
import com.liquidpresentaion.users.repository.CocktailGroupRepository;
import com.liquidpresentaion.users.repository.CocktailRepository;
import com.liquidpresentaion.users.repository.GroupRepository;
import com.liquidpresentaion.users.repository.GroupUserRepository;
import com.liquidpresentaion.users.repository.HousemadeBrandRepository;
import com.liquidpresentaion.users.repository.IngredientRepository;
import com.liquidpresentation.common.Category;
import com.liquidpresentation.common.GroupType;
import com.liquidpresentation.common.IngredientType;

@Service
public class BrandService {
	private static final int deletedId = 1;
	
	@Value("${cocktail_url}")
	private String cocktailUrl;
	@Autowired
	private BrandRepository brandRepository;
	@Autowired
	private CocktailRepository cocktailRepository;
	@Autowired
	private CocktailGroupRepository cocktailGroupRepository;
	@Autowired
	private IngredientRepository ingredientRepository;
	@Autowired
	private HousemadeBrandRepository housemadeBrandRepository;
	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private GroupUserRepository groupUserRepository;
	@PersistenceContext
	private EntityManager entityManger;
	@Autowired
	private ManagementFeignClient managementFeignClient;
	
	public Page<Brand> findAll(PageRequest pageRequet){
		Page<Brand> p = brandRepository.findByDeletedNot(deletedId,pageRequet);
		return new PageImpl<>(p.stream().map(b -> new Brand(b.getId(), b.getName(), b.getSupplierGroup(), b.getBaseSpiritCategory(), b.getDefaultImage(), b.getCreateDate(), b.getBrCategory(), b.getBaseSpiritCategoryString())).collect(Collectors.toList()), pageRequet, p.getTotalElements());
	}

	public Page<Brand> findByNameContaining(String name, PageRequest pageRequet){
		List<Integer> brandPkId = new ArrayList<>();
		List<Group> pkId = groupRepository.findByNameIgnoreCaseContaining(name);
		List<Brand> brands = brandRepository.findByBrCategoryNativeQuery(name);
		if (brands != null && brands.size() > 0) {
			brands.forEach(b->{
				brandPkId.add(b.getId());
			});
		}
		Page<Brand> p = brandRepository.findByDeletedNotAndNameIgnoreCaseContainingOrSupplierGroupInOrIdIn(deletedId,name,pageRequet,pkId,brandPkId);
		return new PageImpl<>(p.stream().map(b -> new Brand(b.getId(), b.getName(), b.getSupplierGroup(), b.getBaseSpiritCategory(), b.getDefaultImage(), b.getCreateDate(), b.getBrCategory(), b.getBaseSpiritCategoryString())).collect(Collectors.toList()), pageRequet, p.getTotalElements());
	}
	
	public void saveBrand(Brand newBrand){
		brandRepository.save(newBrand);
	}
	
	@Transactional
	public void updateBrand(Brand brand){
		Optional<Brand> opt = this.brandRepository.findById(brand.getId());
		if (opt.isPresent()) {
			Brand persist = opt.get();
			persist.setName(brand.getName());
//			persist.setSupplierGroup(brand.getSupplierGroup());
			Integer supplierGroupId = brand.getSupplierGroup().getPkId();
			persist.setSupplierGroupId(supplierGroupId);
			persist.setDefaultImage(brand.getDefaultImage());
			brandRepository.save(persist);
			
			// update brand name of table:lp_cocktail by brandPkId
			entityManger.createNativeQuery("update lp_cocktail set ct_brand_name = '" + brand.getName().replace("'", "''") + "',ct_supplier_pkid = '" + supplierGroupId + "' where ct_brand_pkid = " + brand.getId()).executeUpdate();
			// update brand name of table:lp_cocktail_brand by brandPkId
			entityManger.createNativeQuery("update lp_cocktail_brand set cb_brand_name = '" + brand.getName().replace("'", "''") + "' where cb_brand_pkid = " + brand.getId()).executeUpdate();
		}
	}
	
	
	public void deleteBrand(Brand brand){
		Optional<Brand> opt = this.brandRepository.findById(brand.getId());
		if (opt.isPresent()) {
			Brand persist = opt.get();
			persist.setDeleted(deletedId);
			List<Map<String,String>> contentEmailMap = cocktailRepository.findByBrandPkIdIs(brand.getId());
			sendEmail (persist.getName(),contentEmailMap);
			brandRepository.save(persist);
		}
	}
	
	public void sendEmail (String brandName, List<Map<String,String>> contentEmailMap) {
		
		String subject = MessageFormat.format("\"{0}\" Brand has been discontinued", brandName);
		String content = "  \"{0}\" Brand has been discontinued from system. You have \"{1}\" cocktails with \"{2}\" in Liquid Presentation.<br/>" + 
				"  Please click this link (<a href=\"{3}/#/Administration/Cocktails\">Review Cocktail</a>) to login and review all impacted cocktails highlighted in red.<br/>" + 
				"  Once Logged in, search the \"Deleted Brands\" column and click any brand name in the column to change cocktail name and change brand.<br/>";
		System.out.println(content);
		String token = UserContextHolder.getContext().getAuthToken();
		new Thread(() -> 
			contentEmailMap.forEach(en -> {
				managementFeignClient.sendEmailNotification(subject,MessageFormat.format(content, brandName, en.get("num"), brandName,cocktailUrl) , en.get("email"), token);
			})
		).start();
	}
	
	public Brand getBrand(int brandId){
		Brand b = brandRepository.findById(brandId).get();
		b.setSalesGroups(b.getSalesGroups().stream().map(g -> new Group(g.getPkId(), g.getName())).collect(Collectors.toList()));
		return b;
	}

	@Transactional
	public void addAccessGroup(int brandId, Group salesGroup) {
		Brand brand = brandRepository.findById(brandId).get();
		for (Iterator<Group> iterator = brand.getSalesGroups().iterator(); iterator.hasNext();) {
			if (salesGroup.getPkId() == iterator.next().getPkId()) {
				return;
			}
		}
		brand.getSalesGroups().add(salesGroup);
		Integer supplierGroupId = brand.getSupplierGroup().getPkId();
		List<GroupUser> groupUser = groupUserRepository.findByGroupPkIdsIdNaiveQuery(supplierGroupId);
		List<Integer> userIds = new ArrayList<>();
		groupUser.forEach(g->{
			userIds.add(g.getUser().getPkId());
		});
		List<Integer> cocktailsIdList = new ArrayList<>();
		List<Cocktail> cocktails = cocktailRepository.findByCreatePkIdIn(userIds);
		cocktails.forEach(c->{
			if(c.getBrandPkId() == brandId) {
				cocktailsIdList.add(c.getId());
			}
		});
		List<CocktailGroup> accessGroups = new ArrayList<>();
		if (cocktailsIdList.size() > 0) {
			for (Integer cocktailPkid : cocktailsIdList) {
					if(!cocktailGroupRepository.existsByCocktailIdAndGroupId(cocktailPkid, salesGroup.getPkId())) {
						accessGroups.add(new CocktailGroup(cocktailPkid, salesGroup.getPkId(), 2));
					}
			}
		}
		brandRepository.save(brand);
		cocktailGroupRepository.saveAll(accessGroups);
	}

	@Transactional
	public void removeAccessGroup(int brandId, Group salesGroup) {
		Brand brand = brandRepository.findById(brandId).get();
		for (Iterator<Group> iterator = brand.getSalesGroups().iterator(); iterator.hasNext();) {
			if (salesGroup.getPkId() == iterator.next().getPkId()) {
				iterator.remove();
			}
		}
		
		Integer supplierGroupId = brand.getSupplierGroup().getPkId();
		List<GroupUser> groupUser = groupUserRepository.findByGroupPkIdsIdNaiveQuery(supplierGroupId);
		List<Integer> userIds = new ArrayList<>();
		groupUser.forEach(g->{
			userIds.add(g.getUser().getPkId());
		});
		List<Integer> cocktailsIdList = new ArrayList<>();
		List<Cocktail> cocktails = cocktailRepository.findByCreatePkIdIn(userIds);
		cocktails.forEach(c->{
			if(c.getBrandPkId() == brandId) {
				cocktailsIdList.add(c.getId());
			}
		});
		cocktailGroupRepository.deleteByGroupIdAndCtgFlgAndCocktailIdIn(salesGroup.getPkId(),2,cocktailsIdList);
	}

	public List<Brand> findByBrCategoryAndNameContainingIgnoreCase(Category brCategory, String name, boolean isIngredinet) {
		List<Brand> resultList = null;
		Long distributorId = UserContextHolder.getContext().getDistributorId();
		Set<String> roles = UserContextHolder.getContext().getAuthorities();
		Long userId = UserContextHolder.getContext().getUserId();
		List<String> adminstrators = new ArrayList<>();
		roles.forEach(r->{
			if (r.contains("ADMINISTRATOR")) {
				adminstrators.add(r);
			}
		});
		if(isIngredinet){
			resultList = brandRepository.findByNameContainingIgnoreCaseOrderByName(name);
		}else if(brCategory != null) {
			if(brCategory == Category.HOUSEMADE) {
				if (adminstrators.size() < 1 ) {
					List<Brand> brands = new ArrayList<>();
					List<Integer> supplierGroups = new ArrayList<>();
					List<Integer> salesGroups = new ArrayList<>();
					List<Long> userIds = new ArrayList<>();
					List<GroupUser> groupUsers = groupUserRepository.findByUserIdNaiveQuery(userId);
					groupUsers.forEach(gu->{
						if (gu.getGroup().getType().equals(GroupType.supplier)) {
							supplierGroups.add(gu.getGroup().getPkId());
						} else {
							salesGroups.add(gu.getGroup().getPkId());
						}
					});
					if (supplierGroups != null && supplierGroups.size() > 0) {
						List<GroupUser> userIDs = groupUserRepository.findByGroupPkIdsIdNaiveQuery(supplierGroups);
						userIDs.forEach(user->{
							userIds.add((long)user.getUser().getPkId());
						});
						List<Ingredient> ingredient = ingredientRepository.findByInTypeAndInBrandPkidNotNullAndInCreateUserPkidIn(IngredientType.housemade, userIds);
						ingredient.forEach(i->{
							if (i.getBrand() != null) {
								brands.add(i.getBrand());
							}
						});
					} else if (salesGroups != null && salesGroups.size() > 0) {
						List<Ingredient> ingredient = ingredientRepository.findByInTypeAndInBrandPkidNotNullAndInDistribtorPkid(IngredientType.housemade, distributorId);
						ingredient.forEach(i->{
							if (i.getBrand() != null) {
								brands.add(i.getBrand());
							}
						});
					}
					resultList =  brands;
				} else {
					resultList = brandRepository.findByhousemadeNativeQuery();
				}
			} else {
				resultList = brandRepository.findByDeletedNotAndBrCategoryAndNameContainingIgnoreCaseOrderByName(deletedId,brCategory, name);
			}
		}else {
			List<IngredientType> ingredientTypeList = new ArrayList<>();
			List<Brand> brandResultList = new ArrayList<>();
			ingredientTypeList.add(IngredientType.custom);
			ingredientTypeList.add(IngredientType.housemade);
			if (adminstrators.size() < 1 ) {
				Set<Brand> brands = new HashSet<>();
				List<Integer> salesGroupPkIds = UserContextHolder.getContext().getGroupPkidList();
				List<Integer> supplierGroups = UserContextHolder.getContext().getSupplierGroupIds();
				List<Long> supplierGroupIds = new ArrayList<>();
				if (!CollectionUtils.isEmpty(supplierGroups)) {
					supplierGroupIds = supplierGroups.stream().map(id->(long)id).collect(Collectors.toList());
				}
				
				if (supplierGroups != null && supplierGroups.size() > 0) {
					List<Ingredient> ingredient = ingredientRepository.findByInTypeNotInAndInBrandPkidNotNullAndInSupplierGroupPkidIn(ingredientTypeList, supplierGroupIds);
					brands.addAll(ingredient.stream().map(in->in.getBrand()).collect(Collectors.toList()));
				}
				if (salesGroupPkIds != null && salesGroupPkIds.size() > 0) {
					List<Brand> brandList = brandRepository.findBrandNameInCocktailPage(salesGroupPkIds, distributorId);
					brands.addAll(brandList);
				}
				Map<Integer,Brand> brandMap = new HashMap<Integer, Brand>();
				brands.forEach(b-> brandMap.put(b.getId(), new Brand(b.getId(),b.getName(),b.getSupplierGroup(),b.getBaseSpiritCategory(),b.getDefaultImage(),b.getCreateDate(),b.getBrCategory(),b.getBaseSpiritCategoryString(),b.getDeleted())));
				brandResultList.addAll(brandMap.values());
			} else {
				// 优化SQL
				brandResultList = brandRepository.findBrandOrderByNameByAdminNativeQuery();
			}
			if (StringUtils.isNotBlank(name)) {
				return brandResultList.stream().filter(new Predicate<Brand>() {
					@Override
					public boolean test(Brand t) {
						String[] names = name.split(" ");
						for (String s : names) {
							if (t.getName().toLowerCase().contains(s.toLowerCase())) {
								return true;
							}
						}
						return false;
					}
					
				}).collect(Collectors.toList());
			}
			return brandResultList;
			
		}
		Comparator<Brand> brandOrder = Comparator.comparing(Brand::getName,Collator.getInstance(Locale.ENGLISH));
		List<Brand> brandOrderList = resultList.stream().sorted(brandOrder).collect(Collectors.toList());
		List<Brand> listTemp = new ArrayList<>();
		Iterator<Brand> iter = brandOrderList.iterator();
		while(iter.hasNext()){
			Brand br = (Brand)iter.next();
			if(br.getName().toLowerCase().startsWith(name.toLowerCase())) {
				listTemp.add(br);
				iter.remove();
			}
		}
		Brand brandTemp = null;
		for (int i= 0;i<listTemp.size();i++) {
			if (name.toLowerCase().equals(listTemp.get(i).getName().toLowerCase())) {
				brandTemp = listTemp.get(i);
				break;
			}
		}
		if (brandTemp != null) {
			listTemp.remove(brandTemp);
			listTemp.add(0,brandTemp);
		}
		List<Brand> resultListTemp = new ArrayList<>();
		resultListTemp.addAll(listTemp);
		resultListTemp.addAll(brandOrderList);
		return resultListTemp.stream().map(b ->  {
			if(Category.HOUSEMADE == b.getBrCategory()) 
			{
				List<Ingredient> ingredients = ingredientRepository.findByInBrandPkidAndInType(b.getId(), IngredientType.housemade);
				Ingredient ingredient = null;
				if(!CollectionUtils.isEmpty(ingredients)) {
					ingredient = ingredients.stream().findFirst().get();
					List<HousemadeBrand> housemadeList = housemadeBrandRepository.findByIhHousemadeIngredientPkId(ingredient.getPkId());
					housemadeList.forEach(hou->{
						if (hou.getIhBrandPkId() != null) {
							Optional<Brand> brand = brandRepository.findById(hou.getIhBrandPkId().intValue());
							if (brand.isPresent()) {
								hou.setBrandName(brand.get().getName());
								if (brand.get().getDeleted() == 1) {
									hou.setDeletedBrand(brand.get().getDeleted());
								}
							}
						}
					});
					ingredient.setHousemadeList(housemadeList);
				}
				return new Brand(b.getId(), b.getName(), b.getSupplierGroup(), b.getBaseSpiritCategory(), 
						b.getDefaultImage(), b.getCreateDate(), b.getBrCategory(), ingredient, b.getBaseSpiritCategoryString());
			} else {
					return new Brand(b.getId(), b.getName(), b.getSupplierGroup(), b.getBaseSpiritCategory(), 
							b.getDefaultImage(), b.getCreateDate(), b.getBrCategory(), b.getBaseSpiritCategoryString());
				}
			}).filter(new Predicate<Brand>() {
				@Override
				public boolean test(Brand t) {
					if (StringUtils.isNotBlank(name)) {
						if ((t.getName().toUpperCase().contains(name.toUpperCase()))) {
							return true;
						} else {
							return false;
						}
					}
					return true;
				}
			}).collect(Collectors.toList());
//				.filter(new Predicate<Brand>() {
//
//				@Override
//				public boolean test(Brand brand) {
//					if (UserContextHolder.getContext().isAdmin()) {
//						return true;
//					}
//					List<Integer> supplierGroupsIds = UserContextHolder.getContext().getSupplierGroupIds();//groupRepository.findSupplierGroupsByUserPkid(UserContextHolder.getContext().getUserId().intValue());
//					return supplierGroupsIds.contains(brand.getSupplierGroup().getPkId());
//				}
//			})
	}
}
