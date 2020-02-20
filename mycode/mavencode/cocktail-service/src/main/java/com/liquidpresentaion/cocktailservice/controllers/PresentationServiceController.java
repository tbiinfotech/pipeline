package com.liquidpresentaion.cocktailservice.controllers;

import static com.liquidpresentaion.cocktailservice.constants.API.DUPLICATE_PRESENTATION;
import static com.liquidpresentaion.cocktailservice.constants.API.PATH_VARIABLE_PRESENTATION_ID;
import static com.liquidpresentaion.cocktailservice.constants.API.PATH_VARIABLE_USER_ID;
import static com.liquidpresentaion.cocktailservice.constants.API.PRESENTATION_CALCULATOR;
import static com.liquidpresentaion.cocktailservice.constants.API.PRESENTATION_CONTROLLER;
import static com.liquidpresentaion.cocktailservice.constants.API.PRESENTATION_PATH_USERID;
import static com.liquidpresentaion.cocktailservice.constants.API.PRESENTATION_PATH_VARIABLE;
import static com.liquidpresentaion.cocktailservice.constants.API.PRESENTATION_PDF;
import static com.liquidpresentaion.cocktailservice.constants.API.PRESENTATION_PPTX;
import static com.liquidpresentaion.cocktailservice.constants.API.PRESENTATION_VIEW;
import static com.liquidpresentaion.cocktailservice.constants.API.VIEW_PRESENTATION;
import static com.liquidpresentation.common.Constants.PAGE_PARAM_KEYWORD;
import static com.liquidpresentation.common.Constants.PAGE_PARAM_KEYWORD_DEFAULT;
import static com.liquidpresentation.common.Constants.PAGE_PARAM_PAGE;
import static com.liquidpresentation.common.Constants.PAGE_PARAM_PAGE_DEFAULT;
import static com.liquidpresentation.common.Constants.PAGE_PARAM_SIZE;
import static com.liquidpresentation.common.Constants.PAGE_PARAM_SIZE_DEFAULT;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
import com.liquidpresentaion.cocktailservice.model.Presentation;
import com.liquidpresentaion.cocktailservice.services.PresentationService;
import com.liquidpresentation.common.utils.PageUtil;
import com.lowagie.text.DocumentException;

@RestController
@RequestMapping(PRESENTATION_CONTROLLER)
public class PresentationServiceController {
	
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
	private PresentationService presentationService;
	
	@PostConstruct
	private void initializeAmazon() {
		AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
		this.s3client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(region).build();
	}

	@RequestMapping(method = RequestMethod.GET)
	public Page<Presentation> getPresentations(	@RequestParam(name = PAGE_PARAM_KEYWORD, defaultValue = PAGE_PARAM_KEYWORD_DEFAULT)String keyword,
									@RequestParam(name = "startDate", defaultValue = PAGE_PARAM_KEYWORD_DEFAULT)  String startDate,
									@RequestParam(name = "endDate", defaultValue = PAGE_PARAM_KEYWORD_DEFAULT)  String endDate,
									@RequestParam(name = PAGE_PARAM_PAGE, defaultValue = PAGE_PARAM_PAGE_DEFAULT) int page, 
									@RequestParam(name = PAGE_PARAM_SIZE, defaultValue = PAGE_PARAM_SIZE_DEFAULT) int size,
									@RequestParam(name = "property", defaultValue = "title") String property,
									@RequestParam(name = "asc", defaultValue = "true") boolean asc){
		
		PageRequest pageRequest = PageUtil.buildPageRequest(page, size, property, asc);
		Page<Presentation> resultsPage;

//		if (StringUtil.isAllEmpty(keyword, startDate, endDate)) {
//			 resultsPage = presentationService.findAll(pageRequest);
//			return new PageImpl<>(resultsPage.stream().map(p->new Presentation(p.getId(), p.getTitle(), p.getDate(), p.getUom())).collect(Collectors.toList()), resultsPage.getPageable(), resultsPage.getTotalElements());
//		} else {
//			 resultsPage = presentationService.findByKeyword(keyword, startDate, endDate, pageRequest);
//			 return new PageImpl<>(resultsPage.stream().map(p->new Presentation(p.getId(), p.getTitle(), p.getDate(), p.getUom())).collect(Collectors.toList()), resultsPage.getPageable(), resultsPage.getTotalElements());
//		}
		if (keyword != null) {
			keyword = keyword.trim();
		}
		resultsPage = presentationService.findPresentations(keyword, startDate, endDate, pageRequest);
		return new PageImpl<>(resultsPage.stream().map(p->new Presentation(p.getId(), p.getTitle(), p.getCreateDate(), p.getUom(),p.getStateGroupPkId())).collect(Collectors.toList()), resultsPage.getPageable(), resultsPage.getTotalElements());
	}
	
	@RequestMapping(value = PRESENTATION_PATH_VARIABLE, method = RequestMethod.GET)
	public Presentation getPresentation(@PathVariable(PATH_VARIABLE_PRESENTATION_ID) int presentationId){
		return presentationService.getPresentation(presentationId);
	}
	
	@RequestMapping(value = DUPLICATE_PRESENTATION, method = RequestMethod.POST)
	public void duplicatePresentation(@PathVariable(PATH_VARIABLE_PRESENTATION_ID) int presentationId, @RequestBody Presentation presentation){
		presentationService.duplicatePresentation(presentationId, presentation);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public void savePresentation(@RequestBody Presentation presentation){
		presentationService.savePresentation(presentation);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public void updatePresentation(@RequestBody Presentation presentation){
		presentationService.updatePresentation(presentation);
	}
	
	@RequestMapping(method = RequestMethod.DELETE)
	public void deletePresentation(@RequestBody Presentation presentation){
		presentationService.deletePresentation(presentation);
	}
	
	@RequestMapping(value = PRESENTATION_PATH_USERID, method = RequestMethod.GET)
	public long getNumberOfPresentations(@PathVariable(PATH_VARIABLE_USER_ID) int userId){
		return presentationService.getNumberOfPresentations(userId);
	}
	
	@RequestMapping(value = PRESENTATION_PDF, method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<InputStreamSource> getPresentationPdf(@PathVariable(PATH_VARIABLE_PRESENTATION_ID) int presentationId){
		ByteArrayInputStream bis;
		try {
			Map<String, String> map = new HashMap<>();
			bis = this.presentationService.generatePdf(presentationId, map);
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
		} catch (IOException | DocumentException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	/**
	 * PDF针对Safari下载文件名报错问题
	 * @throws DocumentException 
	 * @throws IOException 
	 * @return PDF'S URL
	 * */
	@RequestMapping(value = "/{presentationId}/pdf/safari", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
	public String getPresentationSafariPdf(HttpServletRequest request, HttpServletResponse resp,@PathVariable(PATH_VARIABLE_PRESENTATION_ID) int presentationId) throws IOException, DocumentException{
		ByteArrayInputStream bis;
        Map<String, String> map = new HashMap<>();
		bis = this.presentationService.generatePdf(presentationId,map);
		String fileName = map.get("title") + ".pdf";
		File convFile = new File(fileName);
		FileOutputStream fos = new FileOutputStream(convFile);
		int index;
		byte[] bytes = new byte[1024];
		while ((index = bis.read(bytes)) != -1) {
			fos.write(bytes, 0, index);
			fos.flush();
        }
		bis.close();
		fos.close();
		String fileUrl = endpointUrl + "/" + fileName;
		uploadFileTos3bucket(fileName, convFile);
		convFile.delete();
		return fileUrl;
	}
	
	@RequestMapping(value = "/pdf", method = RequestMethod.POST)
	public ResponseEntity<byte[]> getPresentationPdf(@RequestBody Presentation presentation){
		ByteArrayInputStream bis;
		try {
			bis = this.presentationService.generatePdf(presentation);
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(IOUtils.toByteArray(bis));
		} catch (IOException | DocumentException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@RequestMapping(value = PRESENTATION_PPTX, method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<InputStreamSource> getPresentationPptx(@PathVariable(PATH_VARIABLE_PRESENTATION_ID) int presentationId){
		ByteArrayInputStream bis;
		try {
			Map<String, String> map = new HashMap<>();
			bis = this.presentationService.generatePptx(presentationId, map);
			String fileName = map.get("title");
			String headerName = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"" + fileName + " - %s.pptx\"", presentationId);
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).header(headerName, headerValue).body(new InputStreamResource(bis));
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	/**
	 * Pptx针对Safari下载文件名报错问题
	 * @throws Exception 
	 * */
	@RequestMapping(value = "/{presentationId}/pptx/safari", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public String getPresentationSafariPptx(HttpServletRequest request, HttpServletResponse resp,@PathVariable(PATH_VARIABLE_PRESENTATION_ID) int presentationId) throws Exception{
		ByteArrayInputStream bis;
        Map<String, String> map = new HashMap<>();
		bis = this.presentationService.generatePptx(presentationId, map);
		String fileName = map.get("title") + ".pptx";
		File convFile = new File(fileName);
		FileOutputStream fos = new FileOutputStream(convFile);
		int index;
		byte[] bytes = new byte[1024];
		while ((index = bis.read(bytes)) != -1) {
			fos.write(bytes, 0, index);
			fos.flush();
        }
		bis.close();
		fos.close();
		String fileUrl = endpointUrl + "/" + fileName;
		uploadFileTos3bucket(fileName, convFile);
		convFile.delete();
		return fileUrl;
	}
	
	@RequestMapping(value = PRESENTATION_VIEW, method = RequestMethod.GET)
	public ResponseEntity<?> viewPresentation(@PathVariable(PATH_VARIABLE_PRESENTATION_ID) int presentationId){
		try {
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(this.presentationService.viewPresentation(presentationId));
		} catch (IOException | DocumentException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@RequestMapping(value = VIEW_PRESENTATION, method = RequestMethod.POST)
	public ResponseEntity<?> viewPresentation(@RequestBody Presentation presentation){
		try {
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(this.presentationService.viewPresentation(presentation));
		} catch (IOException | DocumentException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GetMapping(value = "/customers")
	public List<String> getPresentationsByCreatePkId(
			@RequestParam(name = "createPkId", required = false) Integer createPkId, 
			@RequestParam(name = "salesGroupId", required = false) Integer salesGroupId,
			@RequestParam(name = "startDate", required = true) String startDate,
			@RequestParam(name = "endDate", required = true) String endDate) {
		List<Presentation> list;
		if(createPkId != null) {
			 list = presentationService.findByCreateDateBetweenAndCreatePkIdIsOrderByCustomerAcctName(startDate, endDate, createPkId);
		}else {
			 list = presentationService.findPresentationTeamNativeQuery(startDate, endDate, salesGroupId);
		}
		return list.stream().map(l -> l.getCustomerAcctName() + "(" + l.getCustomerAccountId() + ")").collect(Collectors.toList());
	}
	
	@RequestMapping(value = PRESENTATION_CALCULATOR, method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<InputStreamSource> getProfitCalculator(@PathVariable(PATH_VARIABLE_PRESENTATION_ID) int presentationId){
		ByteArrayInputStream bis;
		try {
			bis = this.presentationService.generateProfitCal(presentationId);
			String headerName = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"ProfitCalculator - %s.xlsx\"", presentationId);
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).header(headerName, headerValue).body(new InputStreamResource(bis));
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@RequestMapping(value = "/{presentationId}/calculator/safari", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public String getProfitCalculatorInSafari(@PathVariable(PATH_VARIABLE_PRESENTATION_ID) int presentationId)
			throws IOException {
		ByteArrayInputStream bis;
		bis = this.presentationService.generateProfitCal(presentationId);
		String fileName = "ProfitCalculator" + ".xlsx";
		File convFile = new File(fileName);
		FileOutputStream fos = new FileOutputStream(convFile);
		int index;
		byte[] bytes = new byte[1024];
		while ((index = bis.read(bytes)) != -1) {
			fos.write(bytes, 0, index);
			fos.flush();
		}
		bis.close();
		fos.close();
		String fileUrl = endpointUrl + "/" + fileName;
		uploadFileTos3bucket(fileName, convFile);
		convFile.delete();
		return fileUrl;
	}
	
	@GetMapping(value = "/customers/download")
	public void dowloadCustomers(@RequestParam(name = "createPkId", required = false) Integer createPkId, 
			@RequestParam(name = "salesGroupId", required = false) Integer salesGroupId,
			@RequestParam(name = "startDate", required = true) String startDate,
			@RequestParam(name = "endDate", required = true) String endDate,
			HttpServletResponse response) throws Exception {
		List<Presentation> list;
		if(createPkId != null) {
			 list = presentationService.findByCreateDateBetweenAndCreatePkIdIsOrderByCustomerAcctName(startDate, endDate, createPkId);
		}else {
			 list = presentationService.findPresentationTeamNativeQuery(startDate, endDate, salesGroupId);
		}
		presentationService.exportCustomers(response, list);
	}
	
	private void uploadFileTos3bucket(String fileName, File file) {
		s3client.putObject(
				new PutObjectRequest(bucketName, fileName, file).withCannedAcl(CannedAccessControlList.PublicRead));
	}
}
