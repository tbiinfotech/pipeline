package com.liquidpresentaion.users.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.liquidpresentaion.users.model.Brand;
import com.liquidpresentaion.users.model.Group;
import com.liquidpresentaion.users.services.BrandService;
import com.liquidpresentation.common.Category;
import com.liquidpresentation.common.utils.PageUtil;

@RestController
@RequestMapping("internal/v1/brands")
public class BrandServiceController {

	@Autowired
	private BrandService brandService;
	
	@RequestMapping(method = RequestMethod.GET)
	public Page<Brand> getBrands(@RequestParam(name = "name", defaultValue = "")String name,
									@RequestParam(name = "page", defaultValue = "0") int page, 
									@RequestParam(name = "size", defaultValue = "25") int size,
									@RequestParam(name = "property", defaultValue = "name") String property,
									@RequestParam(name = "asc", defaultValue = "true") boolean asc){
		
		PageRequest pageRequest = PageUtil.buildPageRequest(page, size, property, asc);
		if (name != null) {
			name = name.trim();
		}
		if ("".equals(name)) {
			return brandService.findAll(pageRequest);
		} else {
			return brandService.findByNameContaining(name, pageRequest);
		}
	}
	
	@RequestMapping(value = "/lookup/brands", method = RequestMethod.GET)
	public List<Brand> lookupBrands(@RequestParam(name = "brCategory", defaultValue = "")Category brCategory,
									@RequestParam(name = "name", defaultValue = "")String name,
									@RequestParam(name = "isIngredinet", defaultValue = "false") boolean isIngredinet){
		if (name != null) {
			name = name.trim();
		}
		return brandService.findByBrCategoryAndNameContainingIgnoreCase(brCategory, name,isIngredinet);
	}
	
	@RequestMapping(value = "/{brandId}", method = RequestMethod.GET)
	public Brand getBrand(@PathVariable("brandId") int brandId){
		return brandService.getBrand(brandId);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public void saveBrand(@RequestBody Brand brand){
		brandService.saveBrand(brand);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public void updateBrand(@RequestBody Brand brand){
		brandService.updateBrand(brand);
	}
	
	@RequestMapping(method = RequestMethod.DELETE)
	public void deleteBrand(@RequestBody Brand brand){
		brandService.deleteBrand(brand);
	}
	
	@RequestMapping(value = "/{brandId}", method = RequestMethod.POST)
	public void addAccessGroup(@PathVariable("brandId") int brandId, @RequestBody Group salesGroup){
		brandService.addAccessGroup(brandId, salesGroup);
	}
	
	@RequestMapping(value = "/{brandId}", method = RequestMethod.DELETE)
	public void removeAccessGroup(@PathVariable("brandId") int brandId, @RequestBody Group salesGroup){
		brandService.removeAccessGroup(brandId, salesGroup);
	}
	
}
