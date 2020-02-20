package com.liquidpresentation.ingredientservice.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.liquidpresentation.common.utils.StringUtil;
import com.liquidpresentation.ingredientservice.validator.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.liquidpresentation.common.utils.UserContextHolder;
import com.liquidpresentation.ingredientservice.data.DataBuilderManager;
import com.liquidpresentation.ingredientservice.data.MatchBrandIdDescriptionBuilder;
import com.liquidpresentation.ingredientservice.data.MatchDistributorItemCodeBuilder;
import com.liquidpresentation.ingredientservice.data.MatchDistributorSupplierBuilder;
import com.liquidpresentation.ingredientservice.data.MatchInvalidPriceValueBuilder;
import com.liquidpresentation.ingredientservice.data.MatchMpcBuilder;
import com.liquidpresentation.ingredientservice.model.Group;
import com.liquidpresentation.ingredientservice.model.Price;
import com.liquidpresentation.ingredientservice.model.PriceCsv;
import com.liquidpresentation.ingredientservice.repository.GroupRepository;
import com.liquidpresentation.ingredientservice.repository.PriceCsvRepository;
import com.liquidpresentation.ingredientservice.repository.PriceRepository;
import com.liquidpresentation.ingredientservice.specification.PriceSearchSpecification;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

@Service
public class PriceService {
	
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
	private PriceRepository priceRepository;
	@Autowired
	private PriceCsvRepository priceCsvRepository;
	@Autowired
	private IngredientService ingredientService;
	@Autowired
	private BrandService brandService;
	@Autowired
	private GroupService groupService;
	@Autowired
	private DistributorCategoryService distributorCategoryService;
	@Autowired
	private DistributorSupplierService distributorSupplierService;
	@Autowired
	private ValidatorManager priceValidatorManager;
	@Autowired
	private DataBuilderManager dataBuilderManager;
	@Autowired
	private GroupRepository groupRepository;
	
	@PostConstruct
	private void initializeAmazon() {
		AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
		this.s3client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(region).build();
	}
	
    public Price getPrice(long id) {
    	return priceRepository.findByPkId(id);
    }
    
    public void savePrice(Price newPrice){    	
    	newPrice.setPrDescription(newPrice.getPrDescription().trim());
    	long userId = UserContextHolder.getContext().getUserId();
    	newPrice.setPrCreateUserPkid(userId);
    	newPrice.setPrUpdateUserPkid(userId);
    	priceRepository.save(newPrice);
	}
	
	public void updatePrice(Price price){
		Optional<Price> opt = this.priceRepository.findById(price.getPkId());
		if (opt.isPresent()) {
			Price persist = opt.get();
			persist.setPrDescription(price.getPrDescription());
			persist.setPrSize(price.getPrSize());
			persist.setPrUom(price.getPrUom());
			persist.setPrCasePack(price.getPrCasePack());
			persist.setPrCasePrice(price.getPrCasePrice());
			
			Timestamp ts = new Timestamp(new java.util.Date().getTime());
			long userId = UserContextHolder.getContext().getUserId();
			persist.setPrUpdateUserPkid(userId);
			persist.setPrUpdateTimestamp(ts);
			priceRepository.save(persist);
		}
	}
	
	public Page<Price> findByGroupId(long groupId, PageRequest pageRequest){
		List<Group> groups = groupRepository.findTopGroupNoDistributor(groupId);
		List<Long> topGroupIds = groups.stream().map(Group::getPkId).collect(Collectors.toList());
		return priceRepository.findByPrSalesGroupPkidIn(topGroupIds, pageRequest);
	}
	
	public Page<Price> findByGroupIdKeyword(long groupId, String keyword, PageRequest pageRequest){
		List<Group> groups = groupRepository.findTopGroupNoDistributor(groupId);
		List<Long> topGroupIds = groups.stream().map(Group::getPkId).collect(Collectors.toList());
		List<Long> supplierIds = new ArrayList<>();
		Set<Long> priceIds = new HashSet<>();
		List<Group> suppliers = groupRepository.findByNameIgnoreCaseContaining(keyword);
		List<Price> prices = priceRepository.findByPrSizeAndPrCasrPackNativeQuery(topGroupIds, keyword);
		if (prices != null && prices.size() > 0) {
			priceIds = prices.stream().map(Price::getPkId).collect(Collectors.toSet());
		}
		if (suppliers != null && suppliers.size() > 0) {
			suppliers.forEach(s->{
				supplierIds.add(s.getPkId());
			});
		}
		return priceRepository.findAll(PriceSearchSpecification.PriceSearch(keyword, supplierIds, topGroupIds,priceIds), pageRequest);
	}
	
	public void deletePrice(List<Price> prices) {
		priceRepository.deleteAll(prices);
	}
	
	public Map<String,Object> importPrices(long groupId, Reader reader) throws Exception {
		Map<String,Object> resultMap = new HashMap<>();
		String batchId = String.valueOf(System.currentTimeMillis());
		List<Price> priceList = new CsvToBeanBuilder<Price>(reader).withSkipLines(1).withType(Price.class).build().parse();
        if (priceList.size() > 1500) {
            throw new Exception("Too much data, please don't exceed 1500 records");
        }
		List<PriceCsv> failureList = priceValidatorManager.build().withValidator(new IngredientIdValidator(ingredientService))
															.withValidator(new MpcValidator())
															.withValidator(new SupplierIdNameValidator())
															.withValidator(new DistributorIdValidator(groupId, groupService))
															.withValidator(new DistributorItemCodeValidator())
															.withValidator(new SalesGroupIdValidator(groupService))
															.withValidator(new StateCodeValidator(groupService.getStateCode(groupId), groupService))
															//.withValidator(new DescriptionValidator())
															.withValidator(new DistributorCategoryMappingValidator(distributorCategoryService))
                                                            .withValidator(new SizeUomValidator())
															.withValidator(new CasePackValidtor())
															.withValidator(new CasePriceValidator())
															.withValidator(new BottlePriceValidtor())
															.validate(priceList).stream().map(x -> x.getPriceCsv(batchId)).collect(Collectors.toList());
//															.validate(priceList).stream().map(ValidationResult::getPrice).collect(Collectors.toList());
		int succeed = priceList.size();
		dataBuilderManager.withPriceList(priceList)
							.firstDataBuilder(new MatchInvalidPriceValueBuilder(this))
							.nextDataBuilder(new MatchDistributorSupplierBuilder(distributorSupplierService)) //validation builder for supplier mapping error
							.nextDataBuilder(new MatchDistributorItemCodeBuilder(this, ingredientService, brandService)) //check price and ingredient
							.nextDataBuilder(new MatchBrandIdDescriptionBuilder(this, ingredientService, brandService))//match brand
							.nextDataBuilder(new MatchMpcBuilder(this, ingredientService, brandService)) // match MPC
							.build().collect();
		
		List<PriceCsv> supplierMappingErrorPriceList = dataBuilderManager.getBadPricList().stream().map(x -> x.getPriceWarningCsv(batchId, "Supplier Mapping Error")).collect(Collectors.toList());
		
		//validate supplier mapping errors for validation failure records
		for (PriceCsv priceCsv : failureList) {
			if (priceCsv.getPrDistributorPkid() == null || StringUtils.isEmpty(priceCsv.getDistSupplierId()) ||
					!distributorSupplierService.existsByDistributorPkidAndDistSupplierId(priceCsv.getPrDistributorPkid(), priceCsv.getDistSupplierId())) {
				priceCsv.setWarningMessage("Supplier Mapping Error");
			} else {
				priceCsv.setWarningMessage("");
			}
		}
		
		failureList.addAll(supplierMappingErrorPriceList);
		priceCsvRepository.saveAll(failureList);
		resultMap.put("failureList", failureList);
		resultMap.put("priceList", succeed);
		return resultMap;
	}
	
	public void exportPrices(long groupId, PrintWriter writer) throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
		List<Price> priceList = priceRepository.findAllByPrSalesGroupPkid(groupId);
		StatefulBeanToCsvBuilder<Price> builder = new StatefulBeanToCsvBuilder<Price>(writer).withQuotechar(CSVWriter.NO_QUOTE_CHARACTER);
		StatefulBeanToCsv<Price> beanWriter = builder.build();
		//default position mappingStrategy has no header, append csv header first
		writer.println("IngredientId,MPC,Supplier,SupplierId,DistributorId,Item code,SalesGroupID,State,Item Description,Category,Size,UOM,Case Pack,Case Price,Bottle Price");
		beanWriter.write(priceList);
	}
	
	public String exportPricesInSafari(long groupId) throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, FileNotFoundException {
		String fileName = "IngredientPricing.csv";
		File file = new File(fileName);
		PrintWriter writer = new PrintWriter(file);
		List<Price> priceList = priceRepository.findAllByPrSalesGroupPkid(groupId);
		StatefulBeanToCsvBuilder<Price> builder = new StatefulBeanToCsvBuilder<Price>(writer).withQuotechar(CSVWriter.NO_QUOTE_CHARACTER);
		StatefulBeanToCsv<Price> beanWriter = builder.build();
		//default position mappingStrategy has no header, append csv header first
		writer.println("IngredientId,MPC,Supplier,SupplierId,DistributorId,Item code,SalesGroupID,State,Item Description,Category,Size,UOM,Case Pack,Case Price,Bottle Price");
		beanWriter.write(priceList);
		writer.close();
		String fileUrl = endpointUrl + "/" + fileName;
		uploadFileTos3bucket(fileName, file);
		file.delete();
		return fileUrl;
	}

	public Optional<Price> findByPrDistributorPkidAndPrDistribtorItemCodeAndPrSalesGroupPkidOrPrState(long prDistributorPkid, String prDistribtorItemCode, Long prSalesGroupPkid, String prState) {
		if (prSalesGroupPkid == null) {
			prSalesGroupPkid = 0L;
		}
		return priceRepository.findByPrDistributorPkidAndPrDistribtorItemCodeAndPrSalesGroupPkidOrPrState(prDistributorPkid, prDistribtorItemCode, prSalesGroupPkid, prState);
	}

	public void deleteByPrDistributorPkidAndPrDistribtorItemCodeAndPrSalesGroupPkidOrPrState(Long prDistributorPkid,
			String prDistribtorItemCode, Long prSalesGroupPkid, String prState) {
		this.priceRepository.deleteByPrDistributorPkidAndPrDistribtorItemCodeAndPrSalesGroupPkidOrPrState(prDistributorPkid, prDistribtorItemCode, prSalesGroupPkid, prState);
		
	}
	
	public void exportPricesCsv (String batchId,PrintWriter writer) throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
		List<PriceCsv> priceCsvList = priceCsvRepository.findByBatchIdOrderByPkIdAsc(batchId);
//		StatefulBeanToCsvBuilder<PriceCsv> builder = new StatefulBeanToCsvBuilder<PriceCsv>(writer).withQuotechar(CSVWriter.NO_QUOTE_CHARACTER);
//		StatefulBeanToCsv<PriceCsv> beanWriter = builder.build();
		LinkedHashMap<String, List<PriceCsv>> result = priceCsvList.stream()
                .collect(Collectors.groupingBy(PriceCsv::getCsvRecord, LinkedHashMap::new,Collectors.toList()));
//		writer.println("Item code,Errors");
		writer.println("IngredientId,MPC,Supplier,SupplierId,DistributorId,Item code,SalesGroupID,State,Item Description,Category,Size,UOM,Case Pack,Case Price,Bottle Price,PrDistributorBrandId,Errors,Supplier Mapping Error");
//		beanWriter.write(priceCsvList);
		for(Map.Entry<String, List<PriceCsv>> en : result.entrySet()) {
			List<String> msgList = en.getValue().stream().map(e-> e.getFailureMessage()).collect(Collectors.toList());
			List<String> warnList = en.getValue().stream().map(e-> e.getWarningMessage()).collect(Collectors.toList());
			writer.println(en.getKey() + "," + StringUtils.join(msgList, ";") + "," + (warnList.isEmpty()?"":warnList.get(0)));
		}
		
		
		/*result.forEach(p->{
			writer.println(p.getCsvRecord() +","+ p.getFailureMessage());
		});*/
	}

	/**
	 * automation tests endpoint
	 * @param results
	 */
	public void getAutomationResults(Map<String, Object> results) {
		results.put("prices", this.priceRepository.findAllByPrDistribtorItemCodeStartingWithOrderByPkIdDesc("IC-Test"));
		results.put("ingredients", this.ingredientService.findAllByInDistribtorItemCodeStartingWithOrderByPkIdDesc("IC-Test"));
		results.put("brands", this.brandService.findAllByMpcStartingWithOrderByPkIdDesc("MPC-Test"));
	}
	
	protected void uploadFileTos3bucket(String fileName, File file) {
		s3client.putObject(
				new PutObjectRequest(bucketName, fileName, file).withCannedAcl(CannedAccessControlList.PublicRead));
	}
}
