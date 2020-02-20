package com.liquidpresentaion.cocktailservice.controllers;

import static com.liquidpresentaion.cocktailservice.constants.API.ACCESS_RIGHT;
import static com.liquidpresentaion.cocktailservice.constants.API.COCKTAIL_CONTROLLER;
import static com.liquidpresentaion.cocktailservice.constants.API.COCKTAIL_PATH_VARIABLE;
import static com.liquidpresentaion.cocktailservice.constants.API.PATH_VARIABLE_COCKTAIL_ID;
import static com.liquidpresentation.common.Constants.PAGE_PARAM_KEYWORD;
import static com.liquidpresentation.common.Constants.PAGE_PARAM_KEYWORD_DEFAULT;
import static com.liquidpresentation.common.Constants.PAGE_PARAM_PAGE;
import static com.liquidpresentation.common.Constants.PAGE_PARAM_PAGE_DEFAULT;
import static com.liquidpresentation.common.Constants.PAGE_PARAM_PROPERTY;
import static com.liquidpresentation.common.Constants.PAGE_PARAM_PROPERTY_DEFAULT;
import static com.liquidpresentation.common.Constants.PAGE_PARAM_SIZE;
import static com.liquidpresentation.common.Constants.PAGE_PARAM_SIZE_DEFAULT;
import static com.liquidpresentation.common.Constants.PAGE_PARAM_SORT;
import static com.liquidpresentation.common.Constants.PAGE_PARAM_SORT_DEFAULT;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.liquidpresentaion.cocktailservice.constants.Category;
import com.liquidpresentaion.cocktailservice.constants.FlavorProfile;
import com.liquidpresentaion.cocktailservice.constants.GlassStyle;
import com.liquidpresentaion.cocktailservice.constants.OutletType;
import com.liquidpresentaion.cocktailservice.constants.SeasonalThemed;
import com.liquidpresentaion.cocktailservice.context.UserContextHolder;
import com.liquidpresentaion.cocktailservice.model.Cocktail;
import com.liquidpresentaion.cocktailservice.model.Group;
import com.liquidpresentaion.cocktailservice.repository.CocktailRepository;
import com.liquidpresentaion.cocktailservice.repository.GroupRepository;
import com.liquidpresentaion.cocktailservice.services.CocktailService;
import com.liquidpresentation.common.BaseSpiritCategory;
import com.liquidpresentation.common.utils.PageUtil;

import scala.util.parsing.json.JSONArray;

@RestController
@RequestMapping(COCKTAIL_CONTROLLER)
public class CocktailServiceController {

	@Autowired
	private CocktailService cocktailService;
	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private CocktailRepository cocktailRepository;
	
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getCocktails(	@RequestParam(name = PAGE_PARAM_KEYWORD, defaultValue = PAGE_PARAM_KEYWORD_DEFAULT)String keyword,
			@RequestParam(name = "fromPage", required = false)String fromPage,
			@RequestParam(name = PAGE_PARAM_PAGE, defaultValue = PAGE_PARAM_PAGE_DEFAULT) int page, 
			@RequestParam(name = PAGE_PARAM_SIZE, defaultValue = PAGE_PARAM_SIZE_DEFAULT) int size,
			@RequestParam(name = PAGE_PARAM_PROPERTY, defaultValue = PAGE_PARAM_PROPERTY_DEFAULT) String property,
			@RequestParam(name = PAGE_PARAM_SORT, defaultValue = PAGE_PARAM_SORT_DEFAULT) boolean asc,
			@RequestParam(name = "filter", required = false) String filter){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Cocktail> cocktailList = new ArrayList<>();
		Set<String> baseSpiritCategorySet = new HashSet<>();
		Set<String> flavorProfileSet = new HashSet<>();
		Set<String> glassStylesSet = new HashSet<>();
		Set<String> outletTypeSet = new HashSet<>();
		Set<String> seasonalThemedSet = new HashSet<>();
		Set<Integer> degreeOfDiffSet = new HashSet<>();
		Set<String> supplierSet = new HashSet<>();
		Set<String> categorySet = new HashSet<>();
		for (BaseSpiritCategory b : BaseSpiritCategory.values()) {
			baseSpiritCategorySet.add(b.toString());
		}
		for (FlavorProfile f : FlavorProfile.values()) {
			flavorProfileSet.add(f.toString());
		}
		for (GlassStyle g : GlassStyle.values()) {
			glassStylesSet.add(g.toString());
		}
		for (OutletType o : OutletType.values()) {
			outletTypeSet.add(o.toString());
		}
		for (SeasonalThemed s : SeasonalThemed.values()) {
			seasonalThemedSet.add(s.toString());
		}
		for (Category c : Category.values()) {
			categorySet.add(c.toString());
		}
		for (int i = 0; i<6; i++) {
			degreeOfDiffSet.add(i);
		}
		PageRequest pageRequest = PageUtil.buildPageRequest(page, size, property, asc);
		
		
		Page<Cocktail> pageResult; 
		if (keyword != null) {
			keyword = keyword.trim();
		}
		if (PAGE_PARAM_KEYWORD_DEFAULT.equals(keyword) && StringUtils.isBlank(filter)) {
			pageResult =  cocktailService.findAll(pageRequest,fromPage);
			
			Optional<Group> groups = null;
			if (UserContextHolder.getContext().isMixologist()) {
				if (CollectionUtils.isEmpty(UserContextHolder.getContext().getSupplierGroupIds())) {
					resultMap.put("addFlig", 0);
				} else {
					for(int id : UserContextHolder.getContext().getSupplierGroupIds()) {
						groups =  groupRepository.findById(id);
					}
					Group group =	groups.get();
					
					if (pageResult.getContent().size() >= group.getCocktailsLimit()) {
						resultMap.put("addFlig", 1);
					}else {
						resultMap.put("addFlig", 0);
					}
				}
			}
		} else {
			pageResult =  cocktailService.findByKeyword(fromPage, keyword, filter, pageRequest);
		}
		
		if (StringUtils.isEmpty(fromPage)) {
			cocktailList = pageResult.getContent();
			
			for (Cocktail cocktail : cocktailList) {
				baseSpiritCategorySet.add(cocktail.getBaseSpiritCategory().toString());
				degreeOfDiffSet.add(cocktail.getDegreeOfDiff());
				if (StringUtils.isNotBlank(cocktail.getSupplierName())) {
					supplierSet.add(cocktail.getSupplierName());
				}
			}
			resultMap.put("baseSpiritCategorySet",baseSpiritCategorySet);
			resultMap.put("degreeOfDiffSet", degreeOfDiffSet);
			resultMap.put("categorySet", categorySet);
			resultMap.put("FlavorProfileSet", flavorProfileSet);
			resultMap.put("glassStylesSet", glassStylesSet);
			resultMap.put("outletTypeSet", outletTypeSet);
			resultMap.put("seasonalThemedSet", seasonalThemedSet);
			resultMap.put("supplierSet", supplierSet);
			resultMap.put("pageResult", pageResult);
			return new ResponseEntity<Map<String, Object>>(resultMap,HttpStatus.OK);
		} else {
			cocktailList = pageResult.getContent();
			resultMap.put("pageResult", new PageImpl<>(cocktailList, pageResult.getPageable(), pageResult.getTotalElements()));
			for (Cocktail cocktail : cocktailList) {
				baseSpiritCategorySet.add(cocktail.getBaseSpiritCategory().toString());
				degreeOfDiffSet.add(cocktail.getDegreeOfDiff());
				if (StringUtils.isNotBlank(cocktail.getSupplierName())) {
					supplierSet.add(cocktail.getSupplierName());
				}
			}
			resultMap.put("baseSpiritCategorySet",baseSpiritCategorySet);
			resultMap.put("degreeOfDiffSet", degreeOfDiffSet);
			resultMap.put("categorySet", categorySet);
			resultMap.put("FlavorProfileSet", flavorProfileSet);
			resultMap.put("glassStylesSet", glassStylesSet);
			resultMap.put("outletTypeSet", outletTypeSet);
			resultMap.put("seasonalThemedSet", seasonalThemedSet);
			resultMap.put("supplierSet", supplierSet);
			resultMap.put("pageResult", pageResult);

			return new ResponseEntity<Map<String, Object>>(resultMap,HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = COCKTAIL_PATH_VARIABLE, method = RequestMethod.GET)
	public Cocktail getCocktail(@PathVariable("cocktailId") int cocktailId){
		return cocktailService.getCocktail(cocktailId);
	}
	
	@RequestMapping(value = COCKTAIL_PATH_VARIABLE, method = RequestMethod.PUT)
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void publish(@PathVariable("cocktailId") int cocktailId, @RequestBody Cocktail cocktail){
		cocktailService.publishCocktail(cocktailId, cocktail);
	}
	
	@RequestMapping(value = COCKTAIL_PATH_VARIABLE, method = RequestMethod.POST)
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void duplicate(@PathVariable("cocktailId") int cocktailId, @RequestBody Cocktail cocktail){
		cocktailService.duplicateCocktail(cocktailId, cocktail);
	}	
	
	@RequestMapping(method = RequestMethod.POST)
	public int saveCocktail(@RequestBody Cocktail cocktail){
		//DECODE
		try {
			if (StringUtils.isNotEmpty(cocktail.getName()))
				cocktail.setName(URLDecoder.decode(cocktail.getName(),"UTF-8"));
			if (StringUtils.isNotEmpty(cocktail.getMethod()))
				cocktail.setMethod(URLDecoder.decode(cocktail.getMethod(), "UTF-8"));
			if (StringUtils.isNotEmpty(cocktail.getGarnish()))
				cocktail.setGarnish(URLDecoder.decode(cocktail.getGarnish(), "UTF-8"));
			if (StringUtils.isNotEmpty(cocktail.getComments()))
				cocktail.setComments(URLDecoder.decode(cocktail.getComments(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Cocktail c = cocktailService.saveCocktail(cocktail);
		return c.getId();
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public void updateCocktail(@RequestBody Cocktail cocktail){
		cocktailService.updateCocktail(cocktail);
	}
	
	@RequestMapping(method = RequestMethod.DELETE)
	public void deleteCocktail(@RequestBody Cocktail cocktail){
		cocktailService.deleteCocktail(cocktail);
	}
	
	@RequestMapping(value = ACCESS_RIGHT, method = RequestMethod.POST)
	public void addAccessRight(@PathVariable(PATH_VARIABLE_COCKTAIL_ID) int cocktailId, @RequestBody Group group){
		cocktailService.addAccessGroup(cocktailId, group);
	}
	
	@RequestMapping(value = ACCESS_RIGHT, method = RequestMethod.DELETE)
	public void deleteAccessRight(@PathVariable(PATH_VARIABLE_COCKTAIL_ID) int cocktailId, @RequestBody Group group){
		cocktailService.deleteAccessGroup(cocktailId, group);
	}
	
	@RequestMapping(value = "/check/cocktail", method = RequestMethod.GET)
	public boolean validateCocktail(@RequestParam(name = "cocktailId",required =false) Integer cocktailId,@RequestParam("cocktailName") String cocktailName) {
		return cocktailService.validateCocktail(cocktailId, cocktailName);
	}
	
	@RequestMapping(value = "/cocktail-flig", method = RequestMethod.GET)
	public int validateCocktail() {
		if (UserContextHolder.getContext().isAdmin()) {
			return 0;
		}
		PageRequest pageRequest = PageUtil.buildPageRequest(0, 999999999, "name", true);
		Page<Cocktail> pageResult =  cocktailService.getCocktailList(pageRequest);
		Optional<Group> groups = null;
		if (CollectionUtils.isEmpty(UserContextHolder.getContext().getSupplierGroupIds())) {
			return 0;
		} else {
			for(int id : UserContextHolder.getContext().getSupplierGroupIds()) {
				groups =  groupRepository.findById(id);
			}
			Group group = groups.get();
			
			if (pageResult.getContent().size() >= group.getCocktailsLimit()) {
				return 1;
			}else {
				return 0;
			}
		}
	}
	
	@RequestMapping(value = "/archive", method = RequestMethod.POST)
	public int archiveAndCreateCocktail(@RequestBody Cocktail cocktail){
		//DECODE
		try {
			if (StringUtils.isNotEmpty(cocktail.getName()))
				cocktail.setName(URLDecoder.decode(cocktail.getName(),"UTF-8"));
			if (StringUtils.isNotEmpty(cocktail.getMethod()))
				cocktail.setMethod(URLDecoder.decode(cocktail.getMethod(), "UTF-8"));
			if (StringUtils.isNotEmpty(cocktail.getGarnish()))
				cocktail.setGarnish(URLDecoder.decode(cocktail.getGarnish(), "UTF-8"));
			if (StringUtils.isNotEmpty(cocktail.getComments()))
				cocktail.setComments(URLDecoder.decode(cocktail.getComments(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return cocktailService.editCocktailWithDeletedBrand(cocktail);
	}
	
	@RequestMapping(value = "/discontinued", method = RequestMethod.GET)
	public int getDiscontinuedCount () {
		return cocktailService.getDeletedBrandCocktailNum();
	}
	
	@RequestMapping(value = "/cocktails-all", method = RequestMethod.POST)
	public List<Cocktail> findAllCocktails(HttpServletRequest request) {
		String[] jsonStr = request.getParameterValues("id");
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < jsonStr.length; i++) {
			list.add(Integer.parseInt(jsonStr[i]));
		}
		return cocktailRepository.findByIdIn(list);
	}
	
}
