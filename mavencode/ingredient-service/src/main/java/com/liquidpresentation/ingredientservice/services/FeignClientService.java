package com.liquidpresentation.ingredientservice.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.liquidpresentation.ingredientservice.clients.ManagementFeignClient;
import com.liquidpresentation.ingredientservice.context.UserContextHolder;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

@Component
public class FeignClientService {

	@Autowired
	private ManagementFeignClient managementFeignClient;

	@HystrixCommand(fallbackMethod = "buildFallbackMailMap", threadPoolKey = "findUsersByGroupsThreadPool", threadPoolProperties = {
			@HystrixProperty(name = "coreSize", value = "30"), @HystrixProperty(name = "maxQueueSize", value = "10"), }, commandProperties = {
					@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
					@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "75"),
					@HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "7000"),
					@HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "15000"), @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "5") })
	public Map<String, List<String>> sendIngredientUpgradeEmail(String newBrandName) {
		String token = UserContextHolder.getContext().getAuthToken();
		return managementFeignClient.sendIngredientUpgradeEmail(newBrandName,token);
	}

	@SuppressWarnings("unused")
	private Map<String, List<String>> buildFallbackMailMap() {
		Map<String, List<String>> fallbackMailMap = new HashMap<>();
		return fallbackMailMap;
	}
}
