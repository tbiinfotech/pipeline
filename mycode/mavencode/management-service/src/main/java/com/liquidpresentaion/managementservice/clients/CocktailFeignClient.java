package com.liquidpresentaion.managementservice.clients;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.liquidpresentaion.managementservice.model.Presentation;

@FeignClient(name = "cocktailservice")
public interface CocktailFeignClient {
	@RequestMapping(method = RequestMethod.POST, value = "internal/v1/presentations/pdf", consumes = "application/json")
	ResponseEntity<byte[]> getPresentationPdf(@RequestBody Presentation presentation, @RequestHeader("Authorization") String token);
}
