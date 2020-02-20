package com.liquidpresentation.ingredientservice.controllers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
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

import com.liquidpresentation.common.Category;
import com.liquidpresentation.common.IngredientType;
import com.liquidpresentation.common.Role;
import com.liquidpresentation.common.utils.PageUtil;
import com.liquidpresentation.ingredientservice.model.Brand;
import com.liquidpresentation.ingredientservice.model.GroupUser;
import com.liquidpresentation.ingredientservice.model.HousemadeBrand;
import com.liquidpresentation.ingredientservice.model.Ingredient;
import com.liquidpresentation.ingredientservice.model.User;
import com.liquidpresentation.ingredientservice.repository.GroupUserRepository;
import com.liquidpresentation.ingredientservice.repository.HousemadeBrandRepository;
import com.liquidpresentation.ingredientservice.services.BrandService;
import com.liquidpresentation.ingredientservice.services.IngredientService;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

@RestController
@RequestMapping("internal/v1/ingredients")
public class IngredientServiceController {

	@Autowired
	private IngredientService ingredientService;
	@Autowired
	private BrandService brandService;
	@Autowired
	private HousemadeBrandRepository housemadeIngredientRepository;
	@Autowired
	private GroupUserRepository groupUserRespository;

	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public void saveIngredient(@RequestBody Ingredient ingredient) {
		try {
			if (StringUtils.isNotEmpty(ingredient.getInMethod())) {
				ingredient.setInMethod(URLDecoder.decode(ingredient.getInMethod(), "UTF-8"));
			}
			if (StringUtils.isNotEmpty(ingredient.getInName())) {
				ingredient.setInName(ingredient.getInName().trim());
				ingredient.setInName(URLDecoder.decode(ingredient.getInName(), "UTF-8"));
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Long pkId = ingredient.getPkId();
		if (pkId != null) {
			if (ingredient.getHousemadeList().size() > 0) {
				housemadeIngredientRepository.deleteByIhHousemadeIngredientPkId(pkId);
				List<HousemadeBrand> list = ingredient.getHousemadeList();
				for(HousemadeBrand housemade : list) {
	    			housemade.setIhHousemadeIngredientPkId(pkId);
	    			housemadeIngredientRepository.save(housemade);
	    		}
			}
			ingredientService.updateIngredientByHousemade(ingredient);
			Brand brand = new Brand();
			brand.setPkId(ingredient.getInBrandPkid());
			brand.setBrName(ingredient.getInName());
			brandService.updateBrandByIngredient(brand);
		} else {
			ingredientService.saveIngredient(ingredient);
		}
	}

	@RequestMapping(method = RequestMethod.GET)
	public Page<Ingredient> getIngredients(@RequestParam(name = "keyword", defaultValue = "") String keyword,
			@RequestParam(name = "showNew", defaultValue = "false") boolean showNew,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "25") int size,
			@RequestParam(name = "property", defaultValue = "inMpc") String property,
			@RequestParam(name = "asc", defaultValue = "true") boolean asc) {
		
		if("brandName".equals(property)) {
			property = "brand.brName";
		}
		if("supplierName".equals(property)) {
			property = "supplierGroup.name";
		}
		if("inRawsize".equals(property)) {
			property = "inSize,inUom";
		}
		PageRequest pageRequest; // = PageUtil.buildPageRequest(page, size, property, asc);
		if ("createdBySupplierGroup".equals(property)) {
			pageRequest = PageUtil.buildPageRequest(0, 10000, "pkId", asc);
		} else {
			pageRequest = PageUtil.buildPageRequest(page, size, property, asc);
		}

		Page<Ingredient> pageResult;
		if (keyword != null) {
			keyword = keyword.trim();
		}	
		pageResult = ingredientService.findAll(keyword, pageRequest,showNew);
		//TODO orderBy 
		if ("createdBySupplierGroup".equals(property)) {
			Page<Ingredient> pages;
			List<GroupUser> groupList = (List<GroupUser>) groupUserRespository.findAll();
			List<Ingredient> list = pageResult.getContent();
			for (Ingredient i : list) {
				for(GroupUser g : groupList) {
					if (i.getInCreateUserPkid() == new Long(g.getUser().getPkId())) {
						if (StringUtils.isNotBlank(keyword)) {
							if (g.getGroup().getName().toLowerCase().contains(keyword.toLowerCase())) {
								i.setCreatedBySupplierGroup(g.getGroup().getName());
								break;
							}
						} else {
							i.setCreatedBySupplierGroup(g.getGroup().getName());
							break;
						}
					}
				}
				if (i.getInType().equals(IngredientType.custom) || i.getInType().equals(IngredientType.housemade)) {
					i.setSupplierName(null);
				}
				User user = new User();
				if (i.getInCreateUserPkid() != null) {
					user.setPkId(i.getInCreateUserPkid().intValue());
					if(groupUserRespository.existsByUserAndRole(user, Role.ADMINISTRATOR)) {
						i.setCreatedBySupplierGroup(null);
					}
				}
			}
			Comparator<Ingredient> comparator = Comparator.comparing(Ingredient::getCreatedBySupplierGroup,Comparator.nullsFirst(Comparator.reverseOrder()));
			if (asc) {
				pages = new PageImpl<>(list.stream().sorted(comparator).collect(Collectors.toList()), pageResult.getPageable(), pageResult.getTotalElements());
			} else {
				pages = new PageImpl<>(list.stream().sorted(comparator.reversed()).collect(Collectors.toList()), pageResult.getPageable(), pageResult.getTotalElements());
			}
			List<Ingredient> pageList = PageUtil.buildPage(pages.getContent(), page, size);
			
			return new PageImpl<>(pageList, PageRequest.of(page, size), list.size());
		}
		
		List<GroupUser> list = (List<GroupUser>) groupUserRespository.findAll();
		for (Ingredient i : pageResult.getContent()) {
			if (i.getInType().equals(IngredientType.custom) || i.getInType().equals(IngredientType.housemade)) {
				i.setSupplierName(null);
			}
			for(GroupUser g : list) {
				if (i.getInCreateUserPkid() != null && i.getInCreateUserPkid() == new Long(g.getUser().getPkId())) {
					if (StringUtils.isNotBlank(keyword)) {
						if (g.getGroup().getName().toLowerCase().contains(keyword.toLowerCase())) {
							i.setCreatedBySupplierGroup(g.getGroup().getName());
							break;
						}
					} else {
						i.setCreatedBySupplierGroup(g.getGroup().getName());
						break;
					}
				}
			}
			if (i.getInCreateUserPkid() != null) {
				User user = new User();
				user.setPkId(i.getInCreateUserPkid().intValue());
				if(groupUserRespository.existsByUserAndRole(user, Role.ADMINISTRATOR)) {
					i.setCreatedBySupplierGroup(null);
				}
			}
		}
		return pageResult;
	}	
	
	@RequestMapping(method = RequestMethod.PUT)
	public void updateIngredient(@RequestBody Ingredient ingredient){
		ingredientService.updateIngredient(ingredient);
	}
	
	@RequestMapping(method = RequestMethod.DELETE)
	public void deleteIngredient(@RequestBody Ingredient ingredient){
		ingredientService.deleteIngredient(ingredient.getPkId());
	}
	
	@RequestMapping(value = "/upload/ingredients", method = RequestMethod.POST)
	@ResponseBody
    public ResponseEntity<String> uploadFile(@RequestParam("csvFile") MultipartFile uploadfile) {

        if (uploadfile.isEmpty()) {
            return new ResponseEntity<String>("Please select a file!", HttpStatus.OK);
        }
        
        try {
        	ingredientService.importIngredients(new InputStreamReader(uploadfile.getInputStream()));
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
		}

        return new ResponseEntity<String>("Successfully uploaded - " + uploadfile.getOriginalFilename(), new HttpHeaders(), HttpStatus.OK);

    }
	
	@RequestMapping(value = "/download/ingredients", method = RequestMethod.GET)
	public void downloadFile(HttpServletResponse response) {
		try {
			String mimeType = "application/octet-stream";
			response.setContentType(mimeType);
			//response.setContentLength((int) downloadFile.length());
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"Ingredients.csv\"");
			response.setHeader(headerKey, headerValue);
			ingredientService.exportIngredients(response.getWriter());
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
	
	@RequestMapping(value = "/download/ingredients/safari", method = RequestMethod.GET)
	public String downloadFileInSafari() throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, IOException {
		return ingredientService.exportIngredientsInSafari();
	}
	
	@RequestMapping(value = "/{ingredientId}", method = RequestMethod.GET)
	public Ingredient getIngredient(@PathVariable("ingredientId") long ingredientId) {
		return ingredientService.getIngredient(ingredientId);
	}
	
	@RequestMapping(value = "/validate/ingredient",method = RequestMethod.POST)
	public boolean validateIngredientNameAndCategory(@RequestBody Ingredient ingredient,
													 @RequestParam(name = "isBrand" ,defaultValue = "false") Boolean isBrand
													 ) {
		String inName = ingredient.getInName();
		Category category = ingredient.getInCategory();
		Long pkId =  ingredient.getPkId();
		return ingredientService.validateIngredientNameAndCategory(inName,pkId,isBrand,category);
	}
	
	@RequestMapping(value="/distributor/find", method = RequestMethod.GET)
	public Page<Ingredient> findIngredients(@RequestParam(name = "keyword", defaultValue = "") String keyword,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "25") int size,
			@RequestParam(name = "property", defaultValue = "in_name") String property,
			@RequestParam(name = "asc", defaultValue = "true") boolean asc) {
		Comparator<Ingredient> comparator = null;
		if("brandName".equals(property)) {
			comparator = Comparator.comparing(Ingredient::getBrandName,Comparator.nullsFirst(Comparator.reverseOrder()));
		} else if ("inSize".equals(property)) {
			comparator = Comparator.comparing(Ingredient::getInSize,Comparator.nullsFirst(Comparator.reverseOrder()));
		} else if ("inUom".equals(property)) {
			comparator = Comparator.comparing(Ingredient::getInUom,Comparator.nullsFirst(Comparator.reverseOrder()));
		} else if ("inName".equals(property)) {
			comparator = Comparator.comparing(Ingredient::getInName,Collator.getInstance(Locale.ENGLISH));
		} else {
			comparator = Comparator.comparing(Ingredient::getInCreateTimestamp,Comparator.nullsFirst(Comparator.reverseOrder()));
		}
		PageRequest pageRequest = PageUtil.buildPageRequest(page, size,property, asc);

		if (keyword != null) {
			keyword = keyword.trim();
		}	
		List<Ingredient> ingredients = ingredientService.findAllIngredients(keyword);
		List<Ingredient> sortedIngredients = new ArrayList<>();
		if (asc) {
			sortedIngredients.addAll(ingredients.stream().sorted(comparator.reversed()).collect(Collectors.toList()));
		} else {
			sortedIngredients.addAll(ingredients.stream().sorted(comparator).collect(Collectors.toList()));
		}
		sortedIngredients = PageUtil.buildPage(sortedIngredients, pageRequest.getPageNumber(), pageRequest.getPageSize());
		return new PageImpl<>(sortedIngredients, pageRequest, ingredients.size());
	}
	
	@RequestMapping(value = "/revert/old-brand/ingredient/{ingredientId}", method = RequestMethod.GET)
	public Map<String, String> revertOldBrand(@PathVariable("ingredientId") Long ingredientId) {
		return ingredientService.getRevertOldBrand(ingredientId);
	}
	
	@RequestMapping(value = "/{ingredientId}/distributor/upgrade", method = RequestMethod.PUT)
	public Map<String, Object> upgradeIngredient(@PathVariable("ingredientId") long ingredientId) {
		return ingredientService.upgradeIngredient(ingredientId);
	}
	
	@RequestMapping(value = "/{ingredientId}/administrator/revert", method = RequestMethod.PUT)
	public Map<String, Object> revertIngredient(@PathVariable("ingredientId") long ingredientId) {
		return ingredientService.revertIngredient(ingredientId);
	}
	
	@RequestMapping(value = "/brands", method = RequestMethod.GET)
	public List<Brand> getBrands() {
		return ingredientService.getBrands();
	}
}
