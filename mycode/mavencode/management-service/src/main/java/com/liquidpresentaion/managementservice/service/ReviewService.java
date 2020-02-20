package com.liquidpresentaion.managementservice.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.liquidpresentaion.managementservice.context.UserContextHolder;
import com.liquidpresentaion.managementservice.model.Presentation;
import com.liquidpresentaion.managementservice.model.ReviewCocktail;
import com.liquidpresentaion.managementservice.model.ReviewTeam;
import com.liquidpresentaion.managementservice.repository.ReviewCocktailRepository;
import com.liquidpresentaion.managementservice.repository.ReviewTeamRepository;
import com.liquidpresentation.common.ExcelData;
import com.liquidpresentation.common.utils.ExportExcelUtils;
import com.liquidpresentation.common.utils.PageUtil;
import com.liquidpresentation.common.utils.StringUtil;

@Service
public class ReviewService {
	
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
	private ReviewTeamRepository reviewTeamRepository;
	@Autowired 
	private ReviewCocktailRepository reviewCocktailRepository;
	@PersistenceContext
	private EntityManager em;
	
	@PostConstruct
	private void initializeAmazon() {
		AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
		this.s3client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(region).build();
	}

	public Page<ReviewTeam> findTeamReviewNativeQuery(Date startDate, Date endDate, Long salesGroupId, PageRequest pageRequest) {
		return this.reviewTeamRepository.findTeamReviewNativeQuery(startDate, endDate, salesGroupId, pageRequest);
	}

	public Page<ReviewTeam> findTeamReviewNativeQuery(Date startDate, Date endDate, Long salesGroupId, String keyword, PageRequest pageRequest) {
		return this.reviewTeamRepository.findTeamReviewNativeQuery(startDate, endDate, salesGroupId, keyword, pageRequest);
	}

	public Page<ReviewCocktail> findCocktailReviewNativeQuery(Date startDate, Date endDate, Long salesGroupId, PageRequest pageable) {
//		List<ReviewCocktail> cocktailReviewList = new ArrayList<>();
//		for (Integer groupId : feignClientService.getSubGroupIdList(salesGroupId.intValue())) {
//			cocktailReviewList.addAll(this.reviewCocktailRepository.findCocktailReviewNativeQuery(startDate, endDate, groupId));
//		}
		if (UserContextHolder.getContext().isAdmin()) {
			 List<ReviewCocktail> cocktailReviewList = this.reviewCocktailRepository.findCocktailReviewQueryByCocktailGroup(startDate, endDate, salesGroupId);
			Comparator<ReviewCocktail> orderBy = null;
			String orderString = null;
			if (pageable.getSort().getOrderFor("ct_name") != null) {
				orderString = "ct_name";
				orderBy = Comparator.comparing(ReviewCocktail::getCocktailName, Collator.getInstance(Locale.ENGLISH));
			} else if (pageable.getSort().getOrderFor("brand_Name") != null) {
				orderString = "brand_Name";
				orderBy = Comparator.comparing(ReviewCocktail::getBrandName, Collator.getInstance(Locale.ENGLISH));
			} else if (pageable.getSort().getOrderFor("supplier_Group_Name") != null) {
				orderString = "supplier_Group_Name";
				orderBy = Comparator.comparing(ReviewCocktail::getSupplierGroupName, Collator.getInstance(Locale.ENGLISH));
			} else if (pageable.getSort().getOrderFor("ps_Count") != null) {
				orderString = "ps_Count";
				orderBy = Comparator.comparing(ReviewCocktail::getPsCount);
			} else if (pageable.getSort().getOrderFor("ps_Count_last_year") != null) {
				orderString = "ps_Count_last_year";
				orderBy = Comparator.comparing(ReviewCocktail::getPsCountLastYear);
			} else if (pageable.getSort().getOrderFor("variance") != null) {
				orderString = "variance";
				orderBy = Comparator.comparing(ReviewCocktail::getVariance);
			} else if (pageable.getSort().getOrderFor("variancepercent") != null) {
				orderString = "variancepercent";
				orderBy = Comparator.comparing(ReviewCocktail::getVariancePercentage);
			} else if (pageable.getSort().getOrderFor("ps_Count_current_month") != null) {
				orderString = "ps_Count_current_month";
				orderBy = Comparator.comparing(ReviewCocktail::getPsCountCurrentMonth);
			}
			List<ReviewCocktail> resultListTemp = new ArrayList<>();
			if (!pageable.getSort().getOrderFor(orderString).getDirection().equals(Direction.ASC)) {
				resultListTemp.addAll(cocktailReviewList.stream().sorted(orderBy.reversed()).collect(Collectors.toList()));
			} else {
				resultListTemp.addAll(cocktailReviewList.stream().sorted(orderBy).collect(Collectors.toList()));
			}
			List<ReviewCocktail> resutlPageList = PageUtil.buildPage(resultListTemp,pageable.getPageNumber(),pageable.getPageSize());
			return new PageImpl<>(resutlPageList, pageable, cocktailReviewList.size()) ;
		}
		return this.reviewCocktailRepository.findCocktailReviewNativeQuery(startDate, endDate, salesGroupId, pageable);
	}
	
	public Page<ReviewCocktail> findCocktailReviewNativeQuery(Date startDate, Date endDate, Long salesGroupId, String keyword, PageRequest pageable) {
//		List<ReviewCocktail> cocktailReviewList = new ArrayList<>();
//		for (Integer groupId : feignClientService.getSubGroupIdList(salesGroupId.intValue())) {
//			cocktailReviewList.addAll(this.reviewCocktailRepository.findCocktailReviewNativeQuery(startDate, endDate, keyword, groupId));
//		}
//		return new PageImpl<>(cocktailReviewList, pageable, cocktailReviewList.size());
		if (UserContextHolder.getContext().isAdmin()) {
			return this.reviewCocktailRepository.findCocktailReviewQueryByCocktailGroup(startDate, endDate, keyword,salesGroupId, pageable);
		}
		return this.reviewCocktailRepository.findCocktailReviewNativeQuery(startDate, endDate, keyword, salesGroupId, pageable);
	}
	
	public String exportReviewTeam(HttpServletResponse response,Date startDate, Date endDate, Long salesGroupId, String keyword,boolean isSafari, PageRequest pageRequest) throws Exception {
		Page<ReviewTeam> reviewTeamList;
		if(StringUtil.isAllEmpty(keyword)) {
			reviewTeamList = reviewTeamRepository.findTeamReviewNativeQuery(startDate, endDate, salesGroupId, pageRequest);
		}else {
			reviewTeamList = reviewTeamRepository.findTeamReviewNativeQuery(startDate, endDate, salesGroupId, keyword, pageRequest);
		}
		
		ExcelData data = new ExcelData();
        List<String> titles = new ArrayList<String>();
        titles.add("User");
        titles.add("In Frame");
        titles.add("In Frame LY");
        titles.add("Variance");
        titles.add("Variance%");
        titles.add("Current Month");
        data.setTitles(titles);
        List<List<Object>> rows = new ArrayList<List<Object>>();
        List<Object> rowOne = new ArrayList<Object>();
        int psCountSum = 0;
        int psCountLastYear = 0;
        int variance = 0;
        int variancepercent = 0;
        int psCountCurrentMonth = 0;
//        reviewTeamList.forEach(review->{
//        	psCountSum = psCountSum + Integer.parseInt(review.getFullname());
//        });
        
        for(ReviewTeam reviewTeam : reviewTeamList) {
        	psCountSum = psCountSum + reviewTeam.getPsCount();
        	psCountLastYear = psCountLastYear + reviewTeam.getPsCountLastYear();
        	variance = variance + reviewTeam.getVariance();
        	variancepercent = variancepercent + (int)reviewTeam.getVariancepercent();
        	psCountCurrentMonth = psCountCurrentMonth + reviewTeam.getPsCountCurrentMonth();
        }
        rowOne.add("Grand total");
        rowOne.add(psCountSum);
        rowOne.add(psCountLastYear);
        rowOne.add(variance);
        rowOne.add(variancepercent + "%");
        rowOne.add(psCountCurrentMonth);
        
        rows.add(rowOne);
        for(ReviewTeam reviewTeam : reviewTeamList) {
        	 List<Object> row = new ArrayList<Object>();
			 row.add(reviewTeam.getFullname());
			 row.add(reviewTeam.getPsCount());
			 row.add(reviewTeam.getPsCountLastYear());
			 row.add(reviewTeam.getVariance());
			 row.add(reviewTeam.getVariancepercent() + "%");
			 row.add(reviewTeam.getPsCountCurrentMonth());
			 rows.add(row);
		}
        data.setRows(rows);
        String fileName = "Teams_Review__Some Business Unit.xlsx";
        if (isSafari) {
        	File file = new File(fileName);
        	OutputStream outputStream = new FileOutputStream(file, true);
        	ExportExcelUtils.exportExcel(data, outputStream);
    		String fileUrl = endpointUrl + "/" + fileName;
    		uploadFileTos3bucket(fileName, file);
    		file.delete();
    		return fileUrl;
        } else {
        	ExportExcelUtils.exportExcel(response,fileName,data);
        }
        return null;
	}
	
	public String exportCocktail(HttpServletResponse response,String startDate, String endDate, Long salesGroupId, String keyword,boolean isSafari, PageRequest pageRequest) throws Exception {
		Page<ReviewCocktail> reviewCocktailList;
		if (UserContextHolder.getContext().isAdmin()) {
			if(StringUtil.isAllEmpty(keyword)) {
//				Page<ReviewCocktail> cocktailReviewList = reviewCocktailRepository.findCocktailReviewQueryByCocktailGroup(StringUtil.toDate(startDate), StringUtil.toDate(endDate), salesGroupId,pageRequest);
				reviewCocktailList = findCocktailReviewNativeQuery(StringUtil.toDate(startDate), StringUtil.toDate(endDate), salesGroupId, pageRequest);
			}else {
				Page<ReviewCocktail> cocktailReviewList = reviewCocktailRepository.findCocktailReviewQueryByCocktailGroup(StringUtil.toDate(startDate), StringUtil.toDate(endDate), keyword, salesGroupId,pageRequest);
				reviewCocktailList = new PageImpl<>(cocktailReviewList.getContent(), pageRequest, cocktailReviewList.getContent().size());
			}
		} else {
			if(StringUtil.isAllEmpty(keyword)) {
				Page<ReviewCocktail> cocktailReviewList = reviewCocktailRepository.findCocktailReviewNativeQuery(StringUtil.toDate(startDate), StringUtil.toDate(endDate), salesGroupId,pageRequest);
				reviewCocktailList = new PageImpl<>(cocktailReviewList.getContent(), pageRequest, cocktailReviewList.getContent().size());
			}else {
				Page<ReviewCocktail> cocktailReviewList = reviewCocktailRepository.findCocktailReviewNativeQuery(StringUtil.toDate(startDate), StringUtil.toDate(endDate), keyword, salesGroupId,pageRequest);
				reviewCocktailList = new PageImpl<>(cocktailReviewList.getContent(), pageRequest, cocktailReviewList.getContent().size());
			}
		}
		ExcelData data = new ExcelData();
        List<String> titles = new ArrayList<String>();
        titles.add("Cocktail Name");
        titles.add("Brand");
        titles.add("Supplier");
        titles.add("In Frame");
        titles.add("In Frame LY");
        titles.add("Variance");
        titles.add("Variance%");
        titles.add("Current Month");
        data.setTitles(titles);
        List<List<Object>> rows = new ArrayList<List<Object>>();
        List<Object> rowOne = new ArrayList<Object>();
        int psCountSum = 0;
        int psCountLastYear = 0;
        int variance = 0;
        int variancepercent = 0;
        int psCountCurrentMonth = 0;
        
        for(ReviewCocktail reviewCocktail : reviewCocktailList) {
        	psCountSum = psCountSum + reviewCocktail.getPsCount();
        	psCountLastYear = psCountLastYear + reviewCocktail.getPsCountLastYear();
        	variance = variance + reviewCocktail.getVariance();
        	variancepercent = variancepercent + Integer.valueOf((reviewCocktail.getVariancePercentage().substring(0,reviewCocktail.getVariancePercentage().indexOf("%"))));
        	psCountCurrentMonth = psCountCurrentMonth + reviewCocktail.getPsCountCurrentMonth();
        }
        rowOne.add("Grand total");
        rowOne.add("");
        rowOne.add("");
        rowOne.add(psCountSum);
        rowOne.add(psCountLastYear);
        rowOne.add(variance);
        rowOne.add(variancepercent + "%");
        rowOne.add(psCountCurrentMonth);
        
        rows.add(rowOne);
        for(ReviewCocktail reviewCocktail : reviewCocktailList) {
        	 List<Object> row = new ArrayList<Object>();
			 row.add(reviewCocktail.getCocktailName());
			 row.add(reviewCocktail.getBrandName());
			 row.add(reviewCocktail.getSupplierGroupName());
			 row.add(reviewCocktail.getPsCount());
			 row.add(reviewCocktail.getPsCountLastYear());
			 row.add(reviewCocktail.getVariance());
			 row.add(reviewCocktail.getVariancePercentage());
			 row.add(reviewCocktail.getPsCountCurrentMonth());
			 rows.add(row);
		}
        data.setRows(rows);
        String fileName = "Cocktail_Statistics__Some Business Unit.xlsx";
        if (isSafari) {
        	File file = new File(fileName);
        	OutputStream outputStream = new FileOutputStream(file, true);
        	ExportExcelUtils.exportExcel(data, outputStream);
    		String fileUrl = endpointUrl + "/" + fileName;
    		uploadFileTos3bucket(fileName, file);
    		file.delete();
    		return fileUrl;
        } else {
        	ExportExcelUtils.exportExcel(response,fileName,data);
        }
        return null;
	}

	public List<Presentation> findPresentationNativeQuery(Integer cocktailPkid, Date startDate, Date endDate) {
		return this.reviewCocktailRepository.findPresentationNativeQuery(cocktailPkid, startDate, endDate).stream().map(x -> new Presentation(x.getId(), x.getCustomerAccountId(), x.getCustomerAcctName())).collect(Collectors.toList());
	}
	
	protected void uploadFileTos3bucket(String fileName, File file) {
		s3client.putObject(
				new PutObjectRequest(bucketName, fileName, file).withCannedAcl(CannedAccessControlList.PublicRead));
	}
}
