package com.liquidpresentation.ingredientservice.services;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.liquidpresentation.common.Category;
import com.liquidpresentation.common.IngredientType;
import com.liquidpresentation.common.Role;
import com.liquidpresentation.common.utils.StringUtil;
import com.liquidpresentation.ingredientservice.context.UserContextHolder;
import com.liquidpresentation.ingredientservice.model.Brand;
import com.liquidpresentation.ingredientservice.model.Cocktail;
import com.liquidpresentation.ingredientservice.model.CocktailBrand;
import com.liquidpresentation.ingredientservice.model.DistributorCategory;
import com.liquidpresentation.ingredientservice.model.Group;
import com.liquidpresentation.ingredientservice.model.GroupUser;
import com.liquidpresentation.ingredientservice.model.HousemadeBrand;
import com.liquidpresentation.ingredientservice.model.Ingredient;
import com.liquidpresentation.ingredientservice.model.Price;
import com.liquidpresentation.ingredientservice.model.User;
import com.liquidpresentation.ingredientservice.repository.BrandRepository;
import com.liquidpresentation.ingredientservice.repository.CocktailBrandRepository;
import com.liquidpresentation.ingredientservice.repository.CocktailRepository;
import com.liquidpresentation.ingredientservice.repository.GroupRepository;
import com.liquidpresentation.ingredientservice.repository.GroupUserRepository;
import com.liquidpresentation.ingredientservice.repository.HousemadeBrandRepository;
import com.liquidpresentation.ingredientservice.repository.IngredientRepository;
import com.liquidpresentation.ingredientservice.repository.PriceRepository;
import com.liquidpresentation.ingredientservice.specification.SearchSpecification;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

@Service
public class IngredientService {
	
	private AmazonS3 s3client;

	@Value("${amazonProperties.endpointUrl}")
	private String endpointUrl;

	@Value("${amazonProperties.bucketName}")
	private String bucketName;

	@Value("${amazonProperties.accessKey}")
	private String accessKey;

	@Value("${amazonProperties.secretKey}")
	private String secretKey;

	@Value("${amazonProperties.region}")
	private String region;

	@Autowired
	private IngredientRepository ingredientRepository;
	@Autowired
	private HousemadeBrandRepository housemadeIngredientRepository;
	@Autowired
	private BrandService brandService;
	@Autowired
	private DistributorCategoryService distributorCategoryService;
	@Autowired
	private BrandRepository brandRepository;
	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private GroupUserRepository groupUserRespository;
	@Autowired
	private PriceRepository priceRepository;
	@Autowired
	private FeignClientService feignClientService;
	@Autowired
	private CocktailRepository cocktailRepository;
	@Autowired
	private CocktailBrandRepository cocktailBrandRepository;
	
	@PostConstruct
	private void initializeAmazon() {
		AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
		this.s3client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(region).build();
	}
	
	private static final Logger logger = LoggerFactory.getLogger(IngredientService.class);
	
    public Ingredient getIngredient(long id) {
    	Ingredient ingredient = ingredientRepository.findByPkId(id);
    	if(IngredientType.housemade.equals(ingredient.getInType())) {
    		List<HousemadeBrand> housemadeList = housemadeIngredientRepository.findByIhHousemadeIngredientPkIdOrderByIndex(id);
    		housemadeList.forEach(hou->{
    			if (hou.getIhBrandPkId() != null) {
    				Optional<Brand> brand = brandRepository.findById(hou.getIhBrandPkId());
    				if (brand.isPresent()) {
    					hou.setBrandName(brand.get().getBrName());
    				}
    			}
			});
    		ingredient.setHousemadeList(housemadeList);
    	}
    	return ingredient;
    }
    
    @Transactional
    public long saveIngredient(Ingredient newIngredient){
    	long userId = UserContextHolder.getContext().getUserId();
    	newIngredient.setInCreateUserPkid(userId);
    	newIngredient.setInUpdateUserPkid(userId);
    	if (IngredientType.custom.equals(newIngredient.getInType()) || IngredientType.housemade.equals(newIngredient.getInType())) {
    		long distributorId = UserContextHolder.getContext().getDistributorId();
    		newIngredient.setInDistribtorPkid(distributorId);
    	}
    	if(newIngredient.getInBrandPkid() == null || newIngredient.getInBrandPkid()<=0) {
    		if (IngredientType.custom.equals(newIngredient.getInType())) {
    			newIngredient.setInBrandPkid(addBrandId(newIngredient));
    		} else {
    			newIngredient.setInBrandPkid(getOrAddBrandId(newIngredient));
    		}
    	}
    	if(IngredientType.custom.equals(newIngredient.getInType())) {
    		newIngredient.setInNewCust(true);
    	}
    	long pkid = ingredientRepository.save(newIngredient).getPkId();
    	
    	if(IngredientType.housemade.equals(newIngredient.getInType())) {
    		for(HousemadeBrand housemade : newIngredient.getHousemadeList()) {
    			housemade.setIhHousemadeIngredientPkId(pkid);
    			housemadeIngredientRepository.save(housemade);
    		}
    	}
    	return pkid;
	}
	
	public void updateIngredient(Ingredient ingredient){
		Optional<Ingredient> opt = this.ingredientRepository.findById(ingredient.getPkId());
		if (opt.isPresent()) {
			Ingredient persist = opt.get();
			persist.setInUpc(ingredient.getInUpc());
			persist.setInMpc(ingredient.getInMpc());
			persist.setInName(ingredient.getInName());
			persist.setInBrandPkid(ingredient.getInBrandPkid());
			persist.setInSupplierGroupPkid(ingredient.getInSupplierGroupPkid());
			persist.setInSize(ingredient.getInSize());
			persist.setInUom(ingredient.getInUom());
			persist.setInCasePack(ingredient.getInCasePack());
			persist.setInCategory(ingredient.getInCategory());
			persist.setInType(ingredient.getInType());
			persist.setInBaseSpriteCategory(ingredient.getInBaseSpriteCategory());

			Timestamp ts = new Timestamp(System.currentTimeMillis());
			long userId = UserContextHolder.getContext().getUserId();
			persist.setInUpdateUserPkid(userId);
			persist.setInUpdateTimestamp(ts);
			persist.setInNewCust(false);
			ingredientRepository.save(persist);
		}
	}
	
	public void updateIngredientDistSupplierId(Long pkId, Long inSupplierGroupPkid, String distSupplierId, String distSupplierName){
		Optional<Ingredient> opt = this.ingredientRepository.findById(pkId);
		if (opt.isPresent()) {
			Ingredient persist = opt.get();
			persist.setInSupplierGroupPkid(inSupplierGroupPkid);
			persist.setDistSupplierId(distSupplierId);
			persist.setDistSupplierName(distSupplierName);

			Timestamp ts = new Timestamp(System.currentTimeMillis());
			long userId = UserContextHolder.getContext().getUserId();
			persist.setInUpdateUserPkid(userId);
			persist.setInUpdateTimestamp(ts);
			persist.setInNewCust(false);
			ingredientRepository.save(persist);
		}
	}
	
	public void updateIngredientByHousemade(Ingredient ingredient){
		Optional<Ingredient> opt = this.ingredientRepository.findById(ingredient.getPkId());
		if (opt.isPresent()) {
			Ingredient persist = opt.get();
			persist.setInName(ingredient.getInName());
			persist.setDcCategory(ingredient.getDcCategory());
			persist.setInMethod(ingredient.getInMethod());
			persist.setInCategory(ingredient.getInCategory());
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			long userId = UserContextHolder.getContext().getUserId();
			persist.setInUpdateUserPkid(userId);
			persist.setInUpdateTimestamp(ts);
			persist.setInNewCust(false);
			ingredientRepository.save(persist);
		}
	}
	
	public Page<Ingredient> findAll(String keyword, PageRequest pageRequest,boolean showNew){
		if(StringUtils.isNotEmpty(keyword)) {
			List<Brand> brNames = brandRepository.findByBrNameIgnoreCaseContaining(keyword); //根据keyword查询 set到brandPkId
			List<Group> suppliers = groupRepository.findByNameIgnoreCaseContaining(keyword);
			String key = keyword.replaceAll("[a-zA-Z]", "");
			String uom = keyword.replaceAll("[-0-9-.]", "");
			List<Ingredient> Ingredients = ingredientRepository.findIngredinetLikeInSize(key.trim(),uom.trim());
			List<Ingredient> ingredientsList = ingredientRepository.findAllByInCategoryNotNull();
			List<Long> brandPkId = new ArrayList<>();
			List<Long> supplierPkIds = new ArrayList<>();
			List<Long> inSizeList = new ArrayList<>();
			List<Integer> userIdList = new ArrayList<>();
			if (brNames.size() > 0) {
				brNames.forEach(b->{
					brandPkId.add(b.getPkId());
				});
			}
			if (suppliers.size() > 0) {
				suppliers.forEach(s->{
					supplierPkIds.add(s.getPkId());
					s.getGroupUsers().forEach(u->{
						if(!groupUserRespository.existsByUserAndRole(u.getUser(), Role.ADMINISTRATOR)) {
							userIdList.add(u.getUser().getPkId());
						}
					});
					
				});
			}
			if (Ingredients.size() > 0) {
				Ingredients.forEach(i->{
					inSizeList.add(i.getPkId());
				});
			}
			if (ingredientsList.size() > 0) {
				ingredientsList.forEach(ing->{
					if (ing.getCreateUser() != null) {
						String str = ing.getCreateUser().getFirstName()+" "+ing.getCreateUser().getLastName();
						if (str.indexOf(keyword)>-1) {
							userIdList.add(ing.getCreateUser().getPkId());
						}
					}
				});
				
			}
		
			return ingredientRepository.findAll(SearchSpecification.ingredinetSearch(keyword, brandPkId, supplierPkIds,inSizeList,userIdList,showNew), pageRequest);
			
		} else {
			if (showNew) {
				return ingredientRepository.findByInNewCust(showNew, pageRequest);
			}
			return ingredientRepository.findAll(pageRequest);
		}
	}
	
	public Page<Ingredient> findNew(String keyword, PageRequest pageRequest){
		if(StringUtils.isNotEmpty(keyword)) {
			return ingredientRepository.findByInNewCustAndInNameIgnoreCaseContaining(true, keyword, pageRequest);
		} else {
			return ingredientRepository.findByInNewCust(true, pageRequest);
		}
	}
	
	public Optional<Ingredient> findByMpc(String mpc){
		List<Ingredient> ingredients = ingredientRepository.findByInMpc(mpc);
		if (ingredients.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(ingredients.get(0));
	}
	
	@Transactional
	public void deleteIngredient(long pkId) {
		Ingredient dbIngredient = ingredientRepository.findByPkId(pkId);
		if(IngredientType.custom.equals(dbIngredient.getInType()) && !ingredientRepository.existsByInBrandPkid(dbIngredient.getInBrandPkid())) {
			brandService.deleteBrand(dbIngredient.getInBrandPkid());
		}
		if(IngredientType.housemade.equals(dbIngredient.getInType())) {
			housemadeIngredientRepository.deleteByIhHousemadeIngredientPkId(pkId);
		}
		ingredientRepository.delete(dbIngredient);
	}
	
	private long getOrAddBrandId(Ingredient ingredient){
		String mpc = ingredient.getInMpc();
		String name = ingredient.getInName();
		Optional<Brand> opt = brandService.findBrandByMpcOrName(mpc, name);
		if(opt.isPresent()) {
			return opt.get().getPkId();
		}else {
			Brand newBrand = new Brand();
			newBrand.setBrMpc(ingredient.getInMpc());
			String brName = StringUtil.isAllEmpty(ingredient.getBrandName()) ? ingredient.getInName() : ingredient.getBrandName();
			brName.trim();
			newBrand.setBrName(brName);
			newBrand.setBrCreateUserPkid(ingredient.getInCreateUserPkid());
			newBrand.setBrUpdateUserPkid(ingredient.getInUpdateUserPkid());
			if (ingredient.getInSupplierGroupPkid() != null) {
				newBrand.setBrSupplierGroupPkid(ingredient.getInSupplierGroupPkid());
			}
			newBrand.setBrBaseSpiritCategory(ingredient.getInBaseSpriteCategory());
			if (ingredient.getInType().equals(IngredientType.housemade)) {
				newBrand.setBrCategory(Category.HOUSEMADE);
			} else {
				newBrand.setBrCategory(ingredient.getInCategory());
			}
			return brandService.saveBrand(newBrand);
		}				
	}
	
	private long addBrandId(Ingredient ingredient){
		Brand newBrand = new Brand();
		newBrand.setBrMpc(ingredient.getInMpc());
		String brName = StringUtil.isAllEmpty(ingredient.getBrandName()) ? ingredient.getInName() : ingredient.getBrandName();
		brName.trim();
		newBrand.setBrName(brName);
		newBrand.setBrCreateUserPkid(ingredient.getInCreateUserPkid());
		newBrand.setBrUpdateUserPkid(ingredient.getInUpdateUserPkid());
		if (ingredient.getInSupplierGroupPkid() != null) {
			newBrand.setBrSupplierGroupPkid(ingredient.getInSupplierGroupPkid());
		}
		newBrand.setBrBaseSpiritCategory(ingredient.getInBaseSpriteCategory());
//		if (ingredient.getInType().equals(IngredientType.housemade)) {
//			newBrand.setBrCategory(Category.HOUSEMADE);
//		} else {
			newBrand.setBrCategory(ingredient.getInCategory());
//		}
		return brandService.saveBrand(newBrand);
	}
	
	
	public void importIngredients(Reader reader) {
		List<Ingredient> ingredientList = new CsvToBeanBuilder<Ingredient>(reader).withSkipLines(1).withType(Ingredient.class).build().parse();
		for(Ingredient ingredient : ingredientList) {
			DistributorCategory dcCategory = distributorCategoryService.getDistributorCategory(ingredient.getInDistribtorPkid(), ingredient.getDcCategory());
			ingredient.setInBaseSpriteCategory(dcCategory.getDcBaseSpiritCategory());
			ingredient.setInCategory(dcCategory.getDcIngredientCategory());
			if(ingredient.getPkId()>0) {
				//existing record
				Ingredient exist = ingredientRepository.findByPkId(ingredient.getPkId());
				ingredient.setInBrandPkid(exist.getInBrandPkid());
				ingredient.setInType(exist.getInType());
				ingredient.setInSupplierGroupPkid(exist.getInSupplierGroupPkid());
			}else {
				ingredient.setInType(IngredientType.custom);
			}
			this.saveIngredient(ingredient);
		}
	}
	
	public void exportIngredients(PrintWriter writer) throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
		List<Ingredient> ingredientList = ingredientRepository.findAll();
		StatefulBeanToCsvBuilder<Ingredient> builder = new StatefulBeanToCsvBuilder<Ingredient>(writer).withQuotechar(CSVWriter.NO_QUOTE_CHARACTER);
		StatefulBeanToCsv<Ingredient> beanWriter = builder.build();
		//default position mappingStrategy has no header, append csv header first
		writer.println("IngredientId,DistributorId,Item Code,MPC,UPC,Item Description,Base Spirit Category,Size,UOM,Case Pack,SupplierId,Supplier,Name,BrandId,Brand Description");
		beanWriter.write(ingredientList);
	}
	
	public String exportIngredientsInSafari() throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, IOException {
		String fileName = "Ingredients.csv";
		File file = new File(fileName);
		PrintWriter writer = new PrintWriter(file);
		List<Ingredient> ingredientList = ingredientRepository.findAll();
		StatefulBeanToCsvBuilder<Ingredient> builder = new StatefulBeanToCsvBuilder<Ingredient>(writer).withQuotechar(CSVWriter.NO_QUOTE_CHARACTER);
		StatefulBeanToCsv<Ingredient> beanWriter = builder.build();
		//default position mappingStrategy has no header, append csv header first
		writer.println("IngredientId,DistributorId,Item Code,MPC,UPC,Item Description,Base Spirit Category,Size,UOM,Case Pack,SupplierId,Supplier,Name,BrandId,Brand Description");
		beanWriter.write(ingredientList);
		writer.close();
		String fileUrl = endpointUrl + "/" + fileName;
		uploadFileTos3bucket(fileName, file);
		file.delete();
		return fileUrl;
	}
	
	public boolean existsByInDistribtorPkidAndInDistribtorItemCode(long inDistribtorPkid, String inDistribtorItemCode) {
		return  ingredientRepository.existsByInDistribtorPkidAndInDistribtorItemCode(inDistribtorPkid, inDistribtorItemCode);
	}

	public boolean existsByInName(String prDistributorBrandDescription) {
		return ingredientRepository.existsByInName(prDistributorBrandDescription);
	}

	public boolean existsByPkId(Long pkId) {
		return ingredientRepository.existsById(pkId);
	}
	
	public boolean validateIngredientNameAndCategory(String inName,Long pkId,Boolean isBrand,Category category) {
		Boolean ibrand = brandRepository.existsByBrName(inName);
		if (category.equals(Category.HOUSEMADE)) {
			Boolean housemadeBrand = brandRepository.existsByBrName(inName);
			Boolean housemadeIn = ingredientRepository.existsByInName(inName);
			if (pkId != null && !pkId.equals("")) {
				if (housemadeIn) {
					Ingredient ingredient = ingredientRepository.findByPkId(pkId);
					if (ingredient != null && inName.equals(ingredient.getInName())) {
						return true;
					} else {
						return false;
					}
				}
			} else {
				if (housemadeBrand && housemadeIn) {
					return false;
				} else {
					return true;
				}
			}
		} else if (isBrand) {
			if (ibrand) {
				if (pkId != null && !pkId.equals("")) {
					Brand brand = brandRepository.findByPkId(pkId);
					if (brand != null && inName.equals(brand.getBrName())) {
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			} else {
				return true;
			}
		}
		Boolean i = ingredientRepository.existsByInNameAndInCategory(inName,category);
		Boolean b = brandRepository.existsByBrNameAndBrCategory(inName,category);
		if (pkId != null && !pkId.equals("")) {
			Ingredient ingredient = ingredientRepository.findByPkIdAndInCategory(Long.valueOf(pkId), category);
			if (i) {
				if (ingredient.getInName().equals(inName)) {
					return true;
				} else {
					return false;
				}
			} else {
				if (b) {
					return false;
				} 
				return true;
			}
		} else {
			if ( !i && !b) {
				return true;
			} else {
				return false;
			}
		}
	}

	public List<Ingredient> findByDistributorIdAndDistributorBrandId(Long prDistributorPkid, String prDistributorBrandId) {
		return ingredientRepository.findByInDistribtorPkidAndDistributorBrandId(prDistributorPkid, prDistributorBrandId);
	}

	public List<Ingredient> findAllByInDistribtorItemCodeStartingWithOrderByPkIdDesc(String itemCode) {
		return ingredientRepository.findAllByInDistribtorItemCodeStartingWithOrderByPkIdDesc(itemCode);
	}

	public Optional<Ingredient> findByInDistribtorPkidAndInDistribtorItemCodeOrPkId(Long prDistributorPkid,
			String prDistribtorItemCode, Long prIngredientPkid) {
		List<Ingredient> ingredients = ingredientRepository.findByInDistribtorPkidAndInDistribtorItemCode(prDistributorPkid, prDistribtorItemCode);
		if (!ingredients.isEmpty()) {
			return Optional.of(ingredients.get(0));
		} else if(prIngredientPkid != null){
			return ingredientRepository.findById(prIngredientPkid);
		}
		return Optional.empty();
	}

	public List<Ingredient> findAllIngredients(String keyword) {
			Integer userId = (int) UserContextHolder.getContext().getUserId();
			User user = new User();
			user.setPkId(userId);
			List<GroupUser> groupusers = groupUserRespository.findByUser(user);
			List<Long> groupIds = new ArrayList<>();
			groupIds= groupusers.stream().map(g-> g.getGroup().getPkId()).collect(Collectors.toList());
			List<Group> topGroups = groupRepository.findTopGroups(groupIds);
			List<Long> distributorPkIds = topGroups.stream().map(g-> g.getDistributorPkId()).filter(Objects::nonNull).collect(Collectors.toList());
			List<Long> topGroupIds = topGroups.stream().map(g-> g.getPkId()).collect(Collectors.toList());
			if (CollectionUtils.isEmpty(distributorPkIds)) {
				return new ArrayList<Ingredient>();
			}
			List<Ingredient> ingredients = ingredientRepository.findAlls(distributorPkIds,topGroupIds);
			if (CollectionUtils.isEmpty(ingredients)) {
				return ingredients;
			}
			if (StringUtil.isNotEmpty(keyword)) {
				ingredients = ingredients.stream().filter(new Predicate<Ingredient>() {
					String regex = StringUtil.isNotEmpty(keyword) ?  getRegex(keyword) : "";
					@Override
					public boolean test(Ingredient ingredient) {
						boolean match = false;
						match = StringUtil.isAllEmpty(keyword) ? true :
								Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(ingredient.getInName() == null ? "": ingredient.getInName()).find()
								|| Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(ingredient.getInSize() == null ? "": ingredient.getInSize().toString()).find()
								|| Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(ingredient.getInUom() == null ? "": ingredient.getInUom()).find()
								|| Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(ingredient.getBrandName() == null ? "": ingredient.getBrandName()).find();
						return match;
					}
				}).collect(Collectors.toList());
			}
			if (!CollectionUtils.isEmpty(ingredients)) {
				List<Map<Long, Object>> isupgradeIdMaps = ingredientRepository.getIsUpgradeds();
				for (Map<Long, Object> maps : isupgradeIdMaps) {
					for (Ingredient ingredient : ingredients) {
						if (ingredient.getPkId() == ((BigInteger) maps.get("pkid")).longValue()) {
							ingredient.setIsUpgrade((Boolean)maps.get("isupgrade"));
							continue;
						}
					}
				}
				
			}
			return ingredients;
	}
	
	private static String getRegex(String keyword) {
		keyword = keyword.trim();
		keyword = keyword.replace("(", "\\(").replace(")", "\\)");
		String regex;
		regex = "(" + keyword + ")";
		return regex;
	}
	
	public Map<String, String> getRevertOldBrand(Long ingredientId) {
		Map<String, String> resultMap = new HashMap<>();
		Optional<Ingredient> ingredientOptional = ingredientRepository.findById(ingredientId);
		if (ingredientOptional.isPresent()) {
			Ingredient ingredient = ingredientOptional.get();
			resultMap.put("upgraded_brand_name",ingredient.getBrandName());
			Brand brand = brandRepository.findByPkId(ingredient.getInOldBrandId());
			if (brand != null) {
				resultMap.put("old_brand_name",brand.getBrName());
			}
			resultMap.put("distributor_name",ingredient.getDistributor().getName());
			List<Price> prices = priceRepository.findByPrIngredientPkid(ingredientId);
			if (!CollectionUtils.isEmpty(prices)) {
				Long salesGroupId = prices.get(0).getPrSalesGroupPkid();
				Optional<Group> groupOptional = groupRepository.findById(salesGroupId);
				if (groupOptional.isPresent()) {
					resultMap.put("sales_group_name",groupOptional.get().getName());
				}
			}
		}
		return resultMap;
	}

	/**
	 * 1. create a new brand based on the upgraded ingredient;
	 * 2. update the ingredient: newBrandID and oldBrandID
	 * 3. update newBrandID and oldBrandID in other ingredients with the same GTIN when their GTIN is valid;
	 * 4. update newBrandID and oldBrandID in other ingredients with the same item description (aka ingredientName);
	 * 5. send email to administrator and copy to other distributor mixologists of the same group (combined sales group plus distributor ID).
	 * 
	 * @param ingredientId: ID of the ingredient which is to be upgraded
	 */
	public Map<String, Object> upgradeIngredient(long ingredientId) {
		Map<String, Object> results = new HashMap<>();

		//get upgrade ingredient
		Optional<Ingredient> upgradeOptional = this.ingredientRepository.findById(ingredientId);
		if (upgradeOptional.isPresent()) {
			if (upgradeOptional.get().getInOldBrandId() == null) {
				performUpgrade(upgradeOptional.get(), results);
				
				//send email
				try {
					Map<String, List<String>> mailingInfo = this.feignClientService.sendIngredientUpgradeEmail(upgradeOptional.get().getInName());
					results.put("mailingInfo", mailingInfo);
				} catch (Exception e) {
					logger.error("send email is failed!",e);
				}
			} else {
				results.put("Message", "Error: Ingredient is already upgraded! Old brand ID = " + upgradeOptional.get().getInOldBrandId());
			}
		} else {
			results.put("Message", "Error: Ingredient is not found!");
		}
		return results;
	}
	
	@Transactional
	private void performUpgrade(Ingredient upgrade, Map<String, Object> results){
		Brand brand = new Brand();
		brand.setBrName(upgrade.getInName());
		brand.setDistributorBrandId(upgrade.getDistributorBrandId());
		brand.setBrSupplierGroupPkid(upgrade.getInSupplierGroupPkid());
		brand.setBrReviewed(false);
		brand.setBrMpc(upgrade.getInMpc());
		brand.setBrBaseSpiritCategory(upgrade.getInBaseSpriteCategory());
		brand.setBrCategory(upgrade.getInCategory());
		

		//create new brand
		long newBrandPkid = brandService.saveBrand(brand);
		brand.setPkId(newBrandPkid);
		
		//update upgrade ingredient
		long oldBrandPkid = upgrade.getInBrandPkid();
		/*upgrade.setInBrandPkid(newBrandPkid);
		upgrade.setInOldBrandId(oldBrandPkid);
		logger.info("Start to upgrade ingredient pkId = [" + upgrade.getPkId() + "] to " + newBrandPkid + " from " + oldBrandPkid);
		this.ingredientRepository.save(upgrade);
		logger.info("upgrade selected ingredient pkId = [" + upgrade.getPkId() + "]");*/
		
		//retrieve other ingredients
		String gtin = upgrade.getInMpc();
		String name = upgrade.getInName();
		List<Ingredient> others = this.ingredientRepository.findByInMpcOrInName(gtin, name);
		Pattern pattern = Pattern.compile("[1-9]+");
		for (Iterator<Ingredient> iterator = others.iterator(); iterator.hasNext();) {
			Ingredient other = iterator.next();
			Matcher matcher = pattern.matcher(other.getInMpc());
			if (StringUtil.isAllEmpty(other.getInMpc()) || !matcher.find()) {
				iterator.remove();
			} else {
				other.setInOldBrandId(other.getInBrandPkid());
				other.setInBrandPkid(newBrandPkid);
				this.ingredientRepository.save(other);
				logger.info("upgrade related ingredient pkId = [" + upgrade.getPkId() + "] to " + newBrandPkid + " from " + other.getInOldBrandId());
			}
		}
		
		logger.info("End upgrade ingredient pkId = [" + upgrade.getPkId() + "] to " + newBrandPkid + " from " + oldBrandPkid);

		results.put("newBrand", brand);
		results.put("upgradeIngredient", upgrade);
		results.put("updateIngredients", others);
	}

	/**
	 * 1. Get newBrandID and oldBrandID;
	 * 2. Update the selected ingredient: inBrandPkid=oldBrandID, inOldBrandId=null;
	 * 3. Update all ingredients with same GTIN or Ingredient Name: inBrandPkid=oldBrandID, inOldBrandId=null, when their GTIN is valid;
	 * 4. Update all housemade ingredients which use newBrandID;
	 * 5. Update all cocktails whose ctBrandPkid=newBrandID: ctBrandPkid=oldBrandID;
	 * 6. Update all cocktails whose add more section use newBrandID;
	 * 
	 * @param ingredientId
	 */
	public Map<String, Object> revertIngredient(long ingredientId) {
		Map<String, Object> results = new HashMap<>();

		//get upgrade ingredient
		Optional<Ingredient> upgradeOptional = this.ingredientRepository.findById(ingredientId);
		if (upgradeOptional.isPresent()) {
			if (upgradeOptional.get().getInOldBrandId() != null) {
				performRevert(upgradeOptional.get(), results);
			} else {
				results.put("Message", "Error: Ingredient is NOT upgraded!");
			}
		} else {
			results.put("Message", "Error: Ingredient is not found!");
		}
		return results;
	}
	
	@Transactional
	private void performRevert(Ingredient revert, Map<String, Object> results){
		long newBrandPkid = revert.getInBrandPkid();
		long oldBrandPkid = revert.getInOldBrandId();

		logger.info("Start to revert ingredient pkId = [" + revert.getPkId() + "] from " + newBrandPkid + " to " + oldBrandPkid);
		
		//revert ingredient
		/*revert.setInBrandPkid(oldBrandPkid);
		revert.setInOldBrandId(null);
		this.ingredientRepository.save(revert);
		logger.info("Revert selected ingredient pkId = [" + revert.getPkId() + "]");*/
		
		//revert other ingredients
		String gtin = revert.getInMpc();
		String name = revert.getInName();
		List<Ingredient> others = this.ingredientRepository.findByInMpcOrInName(gtin, name);
		Pattern pattern = Pattern.compile("[1-9]+");
		for (Iterator<Ingredient> iterator = others.iterator(); iterator.hasNext();) {
			Ingredient other = iterator.next();
			Matcher matcher = pattern.matcher(other.getInMpc());
			if (StringUtil.isAllEmpty(other.getInMpc()) || !matcher.find()) {
				iterator.remove();
			} else {
				other.setInBrandPkid(other.getInOldBrandId());
				other.setInOldBrandId(null);
				this.ingredientRepository.save(other);
				logger.info("Revert related ingredient pkId = [" + other.getPkId() + "]");
			}
		}
		
		//revert housemade ingredients which use the newBrandPkid
		List<HousemadeBrand> housemadeBrands = this.housemadeIngredientRepository.findByIhBrandPkId(newBrandPkid);
		if (!housemadeBrands.isEmpty()) {
			for (HousemadeBrand housemadeBrand : housemadeBrands) {
				housemadeBrand.setIhBrandPkId(oldBrandPkid);
				this.housemadeIngredientRepository.save(housemadeBrand);
				logger.info("Revert housemadeBrand ingredient pkId = [" + housemadeBrand.getPkid() + "]");
			}
		}
		
		//revert cocktail which use the newBrandPkid
		List<Cocktail> cocktails = this.cocktailRepository.findByBrandPkId(newBrandPkid);
		if (!cocktails.isEmpty()) {
			for (Cocktail cocktail : cocktails) {
				cocktail.setBrandPkId(oldBrandPkid);
				this.cocktailRepository.save(cocktail);
				logger.info("Revert cocktail pkId = [" + cocktail.getBrandPkId() + "]");
			}
		}
		
		//revert cocktail which use the newBrandPkid in add more section
		List<CocktailBrand> cocktailBrands = this.cocktailBrandRepository.findByBrandPkId(newBrandPkid);
		if (!cocktailBrands.isEmpty()) {
			for (CocktailBrand cocktailBrand : cocktailBrands) {
				cocktailBrand.setBrandPkId(oldBrandPkid);
				this.cocktailBrandRepository.save(cocktailBrand);
				logger.info("Revert cocktailBrand pkId = [" + cocktailBrand.getBrandPkId() + "]");
			}
		}
		
		//Remove upgraded brand
		brandRepository.deleteById(newBrandPkid);
		
		logger.info("End revert ingredient pkId = [" + revert.getPkId() + "] from " + newBrandPkid + " to " + oldBrandPkid);
		
		
		results.put("RevertIngredient", revert);
		results.put("updateIngredients", others);
		results.put("housemadeBrands", housemadeBrands);
		results.put("cocktails", cocktails);
		results.put("cocktailBrands", cocktailBrands);
	}
	
	public List<Brand> getBrands() {
		return brandRepository.findByBrands();
	}
	
	protected void uploadFileTos3bucket(String fileName, File file) {
		s3client.putObject(
				new PutObjectRequest(bucketName, fileName, file).withCannedAcl(CannedAccessControlList.PublicRead));
	}
}
