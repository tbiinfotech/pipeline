package com.liquidpresentation.ingredientservice.controllers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.liquidpresentation.common.utils.PageUtil;
import com.liquidpresentation.ingredientservice.model.Price;
import com.liquidpresentation.ingredientservice.model.PriceCsv;
import com.liquidpresentation.ingredientservice.services.PriceService;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

@RestController
@RequestMapping("/internal/v1/groups/{groupId}/prices")
public class PriceServiceController {

	@Autowired
	private PriceService priceService;

	/*@RequestMapping(method = RequestMethod.POST)
	public void savePrice(@RequestBody Price price) {
		priceService.savePrice(price);
	}*/

	@RequestMapping(method = RequestMethod.GET)
	public Page<Price> getPrices(@PathVariable("groupId") long groupId, @RequestParam(name = "keyword", required = false) String keyword,
											@RequestParam(name = "page", defaultValue = "0") int page,
											@RequestParam(name = "size", defaultValue = "25") int size,
											@RequestParam(name = "property", defaultValue = "prMpc") String property,
											@RequestParam(name = "asc", defaultValue = "true") boolean asc) {
		PageRequest pageRequest;
		if("prBottlePrice".equals(property)) {
			pageRequest = PageUtil.buildPageRequest(page, size);
		}else {
			pageRequest = PageUtil.buildPageRequest(page, size, property, asc);
		}
		Page<Price> pageResult ;
		if (keyword != null) {
			keyword = keyword.trim();
		}
		if(StringUtils.isEmpty(keyword)) {
			pageResult = priceService.findByGroupId(groupId, pageRequest);
		} else {
			pageResult = priceService.findByGroupIdKeyword(groupId, keyword, pageRequest);
		}
		if("prBottlePrice".equals(property)) {
			Comparator<Price> byPrBottlePrice = Comparator.comparing(Price::getPrBottlePrice);
			if(asc) {
				pageResult = new PageImpl<>(pageResult.getContent().stream().sorted(byPrBottlePrice).collect(Collectors.toList()), pageResult.getPageable(),pageResult.getTotalElements());
			}else {
				pageResult = new PageImpl<>(pageResult.getContent().stream().sorted(byPrBottlePrice.reversed()).collect(Collectors.toList()), pageResult.getPageable(),pageResult.getTotalElements());
			}
		}
		return pageResult;
	}
	
		
	@RequestMapping(method = RequestMethod.PUT)
	public void updatePrice(@RequestBody Price price){
		priceService.updatePrice(price);
	}
	
	@RequestMapping(method = RequestMethod.DELETE)
	public void deletePrice(@RequestBody List<Price> prices){
		priceService.deletePrice(prices);
	}
	
	//NOT-USED:internal testing to add prices
	@RequestMapping(method = RequestMethod.POST)
	public void savePrice(@RequestBody Price price){
		priceService.savePrice(price);
	}
	
	@RequestMapping(value = "/upload/prices", method = RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<Map<String,String>> uploadFile(@PathVariable("groupId") long groupId, @RequestParam("csvFile") MultipartFile uploadfile) {

		List<PriceCsv> failureList = new ArrayList<>();
		Integer priceList ; 
		Map<String, String> msgMap = new HashMap<String,String>();
        if (uploadfile.isEmpty()) {
        	msgMap.put("successMsg", "Please select a file!");
            return new ResponseEntity<Map<String,String>>(msgMap, HttpStatus.OK);
        }
        
        try {
        	Map<String,Object> paramMap = priceService.importPrices(groupId, new InputStreamReader(uploadfile.getInputStream()));
        	failureList = (List<PriceCsv>) paramMap.get("failureList");
        	priceList = (Integer) paramMap.get("priceList");
		} catch (Exception e) {
			msgMap.put("exceptionMsg", e.getLocalizedMessage());
			e.printStackTrace();
			return new ResponseEntity<Map<String,String>>(msgMap, HttpStatus.BAD_REQUEST);
		}

        if (failureList.size()<1) {
        	msgMap.put("successMsg", "Successfully uploaded");
        	return new ResponseEntity<Map<String,String>>(msgMap, new HttpHeaders(), HttpStatus.OK);
		} else {
			String errorMsgHeader = "Successfully uploaded("+(priceList)+") - " + uploadfile.getOriginalFilename() + " with ";
			String errorMsgEnd = " validation errors!";
			String number = String.valueOf(failureList.size());
			msgMap.put("errorMsgHeader", errorMsgHeader);
			msgMap.put("batchId", failureList.get(0).getBatchId());
			msgMap.put("errorMsgEnd", errorMsgEnd);
			msgMap.put("number",number);
        	return new ResponseEntity<Map<String,String>>(msgMap, new HttpHeaders(), HttpStatus.OK);
		}
    }
	
	@RequestMapping(value = "/download/prices", method = RequestMethod.GET)
	public void downloadFile(@PathVariable("groupId") long groupId, HttpServletResponse response) {
		try {
			String mimeType = "application/octet-stream";
			response.setContentType(mimeType);
			//response.setContentLength((int) downloadFile.length());
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"IngredientPricing.csv\"");
			response.setHeader(headerKey, headerValue);
			priceService.exportPrices(groupId, response.getWriter());
		} catch (CsvDataTypeMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CsvRequiredFieldEmptyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/download/prices/safari", method = RequestMethod.GET)
	public String downloadFileInSafari(@PathVariable("groupId") long groupId) throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, FileNotFoundException {
		return priceService.exportPricesInSafari(groupId);
	}
	
	@RequestMapping(value = "/{priceId}", method = RequestMethod.GET)
	public Price getPrice(@PathVariable("priceId") long priceId) {
		return priceService.getPrice(priceId);
	}
	
	@RequestMapping(value = "/download/pricecsv",method = RequestMethod.GET)
	public void getDownloadPriceCsv(@RequestParam(value = "batchId") String batchId,HttpServletResponse response) throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, IOException {
		String mimeType = "application/octet-stream";
		response.setContentType(mimeType);
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"Errors.csv\"");
		response.setHeader(headerKey, headerValue);
		priceService.exportPricesCsv(batchId, response.getWriter());
	}
	
	@RequestMapping(value = "/automation/get", method = RequestMethod.GET)
	@ResponseBody
    public ResponseEntity<Map<String,Object>> getAutomationResults() {
		Map<String, Object> results = new HashMap<String,Object>();
		priceService.getAutomationResults(results);
		return new ResponseEntity<Map<String,Object>>(results, HttpStatus.OK);
	}

}
