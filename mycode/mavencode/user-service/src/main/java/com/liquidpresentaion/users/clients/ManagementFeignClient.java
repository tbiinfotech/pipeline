package com.liquidpresentaion.users.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient("managementservice")
public interface ManagementFeignClient {
	@RequestMapping(
			method = RequestMethod.POST, 
			value = "/internal/v1/settings/email/notification",
			consumes = "application/json")
	public void sendEmailNotification(@RequestParam("subject") String subject, 
			  @RequestParam("content") String content, 
			  @RequestParam("email") String email, @RequestHeader("Authorization") String token);
}
