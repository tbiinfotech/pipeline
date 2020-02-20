package com.liquidpresentaion.users.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.liquidpresentaion.users.model.Banner;
import com.liquidpresentaion.users.services.BannerService;
import com.liquidpresentation.common.utils.PageUtil;

@RestController
@RequestMapping("internal/v1/banners")
public class BannerServiceController {

	@Autowired
	private BannerService bannerService;
	
	@RequestMapping(method = RequestMethod.GET)
	public Page<Banner> getBanners(@RequestParam(name = "name", defaultValue = "")String name,
									@RequestParam(name = "page", defaultValue = "0") int page, 
									@RequestParam(name = "size", defaultValue = "25") int size,
									@RequestParam(name = "property", defaultValue = "name") String property,
									@RequestParam(name = "asc", defaultValue = "true") boolean asc){
		
		PageRequest pageRequest = PageUtil.buildPageRequest(page, size, property, asc);
		if (name != null) {
			name = name.trim();
		}
		if ("".equals(name)) {
			return bannerService.findAll(pageRequest);
		} else {
			return bannerService.findByNameIgnoreCaseContaining(name, pageRequest);
		}
	}
	
	@RequestMapping(value = "/{bannerId}", method = RequestMethod.GET)
	public Banner getBanner(@PathVariable("bannerId") int bannerId){
		return bannerService.getBanner(bannerId);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public void saveBanner(@RequestBody Banner banner){
		bannerService.saveBanner(banner);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public void updateBanner(@RequestBody Banner banner){
		bannerService.updateBanner(banner);
	}
	
	@RequestMapping(method = RequestMethod.DELETE)
	public void deleteBanner(@RequestBody Banner banner){
		bannerService.deleteBanner(banner);
	}
}
