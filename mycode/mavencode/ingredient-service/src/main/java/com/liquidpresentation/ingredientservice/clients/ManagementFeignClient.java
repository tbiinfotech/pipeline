package com.liquidpresentation.ingredientservice.clients;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "managementservice")
public interface ManagementFeignClient {

	@RequestMapping(
			method = RequestMethod.GET, 
			value = "internal/v1/settings/send/upgrade/email", 
			consumes = "application/json")
	Map<String, List<String>> sendIngredientUpgradeEmail(@RequestParam(name = "newBrandName") String newBrandName, @RequestHeader("Authorization") String token);

}
