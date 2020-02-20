package com.liquidpresentation.ingredientservice.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.liquidpresentation.ingredientservice.model.Brand;
import com.liquidpresentation.ingredientservice.services.BrandService;

@RestController
@RequestMapping("internal/v1/brands")
public class BrandServiceController {
	
	@Autowired
	private BrandService brandService;
	

	@RequestMapping(value = "/lookup", method = RequestMethod.GET)
	public List<Brand> getIngredients(@RequestParam(name = "category") String category, @RequestParam(name = "keyword") String keyword){
		if (keyword != null) {
			keyword = keyword.trim();
		}
		return brandService.lookupBrand(category, keyword);
	}

}
