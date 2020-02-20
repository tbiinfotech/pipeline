package com.liquidpresentaion.managementservice.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.liquidpresentaion.managementservice.model.Presentation;
import com.liquidpresentaion.managementservice.model.ReviewCocktail;
import com.liquidpresentaion.managementservice.model.ReviewTeam;
import com.liquidpresentaion.managementservice.service.ReviewService;
import com.liquidpresentation.common.utils.PageUtil;
import com.liquidpresentation.common.utils.StringUtil;

@RestController
@RequestMapping("internal/v1/reviews")
public class ReviewServiceController {
	
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
	private ReviewService reviewService;
	
	@PostConstruct
	private void initializeAmazon() {
		AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
		this.s3client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(region).build();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/team")
	public Page<ReviewTeam> getTeamReview(	
			@RequestParam(name = "salesGroupId", required = true)Long salesGroupId,
			@RequestParam(name = "keyword", defaultValue = "")String keyword,
			@RequestParam(name = "startDate", required = true) String startDate, 
			@RequestParam(name = "endDate",  required = true) String endDate, 
			@RequestParam(name = "page", defaultValue = "0") int page, 
			@RequestParam(name = "size", defaultValue = "25") int size,
			@RequestParam(name = "property", defaultValue = "fullname") String property,
			@RequestParam(name = "asc", defaultValue = "true") boolean asc) {

		PageRequest pageRequest = PageUtil.buildPageRequest(page, size, property, asc);
		Page<ReviewTeam> pageResult;
		if (keyword != null) {
			keyword = keyword.trim();
		}
		if (StringUtil.isAllEmpty(keyword)) {
			pageResult = reviewService.findTeamReviewNativeQuery(StringUtil.toDate(startDate), StringUtil.toDate(endDate), salesGroupId, pageRequest);
		} else {
			pageResult = reviewService.findTeamReviewNativeQuery(StringUtil.toDate(startDate), StringUtil.toDate(endDate), salesGroupId,keyword, pageRequest);
		}
		List<ReviewTeam> totalList = new ArrayList<>();
		ReviewTeam rt = getTeamReviewTotal(salesGroupId, keyword, startDate, endDate);
		totalList.add(rt);
		totalList.addAll(pageResult.getContent());
		return new PageImpl<>(totalList, pageResult.getPageable(),pageResult.getTotalElements());
	}
	
	private ReviewTeam getTeamReviewTotal(Long salesGroupId, String keyword, String startDate, String endDate) {
		PageRequest pageRequest = PageUtil.buildPageRequest(0, 99999);
		Page<ReviewTeam> pageResult;
		if (StringUtil.isAllEmpty(keyword)) {
			pageResult = reviewService.findTeamReviewNativeQuery(StringUtil.toDate(startDate), StringUtil.toDate(endDate), salesGroupId, pageRequest);
		} else {
			pageResult = reviewService.findTeamReviewNativeQuery(StringUtil.toDate(startDate), StringUtil.toDate(endDate), salesGroupId,keyword, pageRequest);
		}
		List<ReviewTeam> tempList = pageResult.getContent();
		int totalPsCount = tempList.stream().collect(Collectors.summingInt(ReviewTeam::getPsCount));
		int totalPsCountLastYear = tempList.stream().collect(Collectors.summingInt(ReviewTeam::getPsCountLastYear));
		int totalVariance = totalPsCount - totalPsCountLastYear;
		int totalVariancePercent = totalPsCountLastYear == 0 ? totalPsCount*100 : totalVariance*100/totalPsCountLastYear;
		int totalPsCountCurrentMonth = tempList.stream().collect(Collectors.summingInt(ReviewTeam::getPsCountCurrentMonth));
		ReviewTeam rt = new ReviewTeam();
		rt.setPsCount(totalPsCount);
		rt.setPsCountLastYear(totalPsCountLastYear);
		rt.setPsCountCurrentMonth(totalPsCountCurrentMonth);
		rt.setVariance(totalVariance);
		rt.setVariancepercent(totalVariancePercent);
		return rt;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/cocktail")
	public Page<ReviewCocktail> getCocktailReview(
			@RequestParam(name = "salesGroupId", required = true)Long salesGroupId,
			@RequestParam(name = "keyword", defaultValue = "")String keyword,
			@RequestParam(name = "startDate", required = true) String startDate, 
			@RequestParam(name = "endDate",  required = true) String endDate, 
			@RequestParam(name = "page", defaultValue = "0") int page, 
			@RequestParam(name = "size", defaultValue = "25") int size,
			@RequestParam(name = "property", defaultValue = "ct_name") String property,
			@RequestParam(name = "asc", defaultValue = "true") boolean asc) {
		if("cocktailName".equals(property)) {
			property = "ct_name";
		}
//		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(asc ? Direction.ASC : Direction.DESC, property));
//		PageRequest pageRequest = PageRequest.of(page, size);
		PageRequest pageRequest = PageUtil.buildPageRequest(page, size, property, asc);
		Page<ReviewCocktail> pageResult;
		if (keyword != null) {
			keyword = keyword.trim();
		}
		if (StringUtil.isAllEmpty(keyword)) {
			pageResult = reviewService.findCocktailReviewNativeQuery(StringUtil.toDate(startDate), StringUtil.toDate(endDate), salesGroupId, pageRequest);
		} else {
			pageResult = reviewService.findCocktailReviewNativeQuery(StringUtil.toDate(startDate), StringUtil.toDate(endDate), salesGroupId, keyword, pageRequest);
		}
		List<ReviewCocktail> totalList = new ArrayList<>();
		ReviewCocktail rc = getCocktailReviewTotal(salesGroupId, keyword, startDate, endDate,property,asc);
		totalList.add(rc);
		totalList.addAll(pageResult.getContent());
		return new PageImpl<>(totalList, pageResult.getPageable(),pageResult.getTotalElements());
	}

	private ReviewCocktail getCocktailReviewTotal(Long salesGroupId, String keyword, String startDate, String endDate, String property,boolean asc) {
		PageRequest pageRequest = PageUtil.buildPageRequest(0, 99999, property, asc);
		Page<ReviewCocktail> pageResult;
		if (StringUtil.isAllEmpty(keyword)) {
			pageResult = reviewService.findCocktailReviewNativeQuery(StringUtil.toDate(startDate), StringUtil.toDate(endDate), salesGroupId, pageRequest);
		} else {
			pageResult = reviewService.findCocktailReviewNativeQuery(StringUtil.toDate(startDate), StringUtil.toDate(endDate), salesGroupId, keyword, pageRequest);
		}
		
		List<ReviewCocktail> tempList = pageResult.getContent();
		int totalPsCount = tempList.stream().collect(Collectors.summingInt(ReviewCocktail::getPsCount));
		int totalPsCountLastYear = tempList.stream().collect(Collectors.summingInt(ReviewCocktail::getPsCountLastYear));
		int totalPsCountCurrentMonth = tempList.stream().collect(Collectors.summingInt(ReviewCocktail::getPsCountCurrentMonth));
		ReviewCocktail rt = new ReviewCocktail();
		rt.setPsCount(totalPsCount);
		rt.setPsCountLastYear(totalPsCountLastYear);
		rt.setPsCountCurrentMonth(totalPsCountCurrentMonth);
		return rt;
		
	}

	@RequestMapping(method = RequestMethod.GET, value = "/cocktail/{cocktailPkid}")
	public List<Presentation> getPresentationByCocktailPkid(
			@PathVariable(name = "cocktailPkid", required = true) Integer cocktailPkid,
			@RequestParam(name = "startDate", required = true) String startDate, 
			@RequestParam(name = "endDate",  required = true) String endDate) {

		return reviewService.findPresentationNativeQuery(cocktailPkid, StringUtil.toDate(startDate), StringUtil.toDate(endDate));
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/cocktail/{cocktailPkid}/download")
	public void downloadPresentationByCocktailPkid(
			@PathVariable(name = "cocktailPkid", required = true) Integer cocktailPkid,
			@RequestParam(name = "startDate", required = true) String startDate, 
			@RequestParam(name = "endDate",  required = true) String endDate) {

//		reviewService.downloadPresentation(reviewService.findPresentationNativeQuery(cocktailPkid, StringUtil.toDate(startDate), StringUtil.toDate(endDate)));
	}
	
	@RequestMapping(value = "/download/reviews", method = RequestMethod.GET)
	public String downloadFile(@RequestParam(name = "salesGroupId", required = true)Long salesGroupId,
			 @RequestParam(name = "keyword", defaultValue = "")String keyword,
			 @RequestParam(name = "startDate", required = true) String startDate, 
			 @RequestParam(name = "endDate",  required = true) String endDate, 
			 @RequestParam(name = "page", defaultValue = "0") int page, 
			 @RequestParam(name = "size", defaultValue = "25") int size,
			 @RequestParam(name = "property", defaultValue = "fullname") String property,
			 @RequestParam(name = "asc", defaultValue = "true") boolean asc,
			 @RequestParam(name = "isSafari", defaultValue = "false") boolean isSafari,
			 HttpServletResponse response) throws Exception {
		PageRequest pageRequest = PageUtil.buildPageRequest(page, size, property, asc);
		return reviewService.exportReviewTeam(response,StringUtil.toDate(startDate), StringUtil.toDate(endDate), salesGroupId,keyword,isSafari,pageRequest);
	}
	
	@RequestMapping(value = "/download/cocktail", method = RequestMethod.GET)
	public String downloadCocktail(
			@RequestParam(name = "salesGroupId", required = true)Long salesGroupId,
			@RequestParam(name = "keyword", defaultValue = "")String keyword,
			@RequestParam(name = "startDate", required = true) String startDate, 
			@RequestParam(name = "endDate",  required = true) String endDate, 
			@RequestParam(name = "page", defaultValue = "0") int page, 
			@RequestParam(name = "size", defaultValue = "25") int size,
			@RequestParam(name = "property", defaultValue = "fullname") String property,
			@RequestParam(name = "asc", defaultValue = "true") boolean asc,
			@RequestParam(name = "isSafari", defaultValue = "false") boolean isSafari,
			 HttpServletResponse response) throws Exception {
		
		if("cocktailName".equals(property)) {
			property = "ct_name";
		}
		size = 99999;
		PageRequest pageRequest = PageUtil.buildPageRequest(page, size, property, asc);
			
		return reviewService.exportCocktail(response,startDate, endDate, salesGroupId, keyword, isSafari, pageRequest);
	}
	
	protected void uploadFileTos3bucket(String fileName, File file) {
		s3client.putObject(
				new PutObjectRequest(bucketName, fileName, file).withCannedAcl(CannedAccessControlList.PublicRead));
	}
}
