package com.liquidpresentaion.cocktailservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.liquidpresentaion.cocktailservice.model.Brand;
import com.liquidpresentaion.cocktailservice.model.User;

@FeignClient("userservice")
public interface UserFeignClient {

	@RequestMapping(
			method = RequestMethod.GET, 
			value = "/internal/v1/brands/{brandId}", 
			consumes = "application/json")
	Brand getBrand(@PathVariable("brandId") int brandId);

	@RequestMapping(
			method = RequestMethod.GET, 
			value = "/internal/v1/users/{userId}", 
			consumes = "application/json")
	User getUser(@PathVariable("userId") int userId);
}
