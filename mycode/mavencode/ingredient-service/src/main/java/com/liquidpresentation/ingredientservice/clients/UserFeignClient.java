package com.liquidpresentation.ingredientservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("userservice")
public interface UserFeignClient {

	/*@RequestMapping(
			method = RequestMethod.GET, 
			value = "/internal/v1/users/{userId}/groups", 
			consumes = "application/json")
	//Group getUserGroups(@PathVariable("userId") int userId);*/
}
