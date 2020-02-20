package com.liquidpresentaion.managementservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.liquidpresentaion.managementservice.clients.CocktailFeignClient;
import com.liquidpresentaion.managementservice.clients.UserFeignClient;
import com.liquidpresentaion.managementservice.context.UserContextHolder;
import com.liquidpresentaion.managementservice.model.Group;
import com.liquidpresentaion.managementservice.model.Presentation;
import com.liquidpresentaion.managementservice.model.User;
import com.liquidpresentation.common.GroupType;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

@Component
public class FeignClientService {

	@Autowired
	private UserFeignClient userFeignClient;

	@Autowired
	private CocktailFeignClient cocktailFeignClient;

	@HystrixCommand(fallbackMethod = "buildFallbackUserList", threadPoolKey = "findUsersByGroupsThreadPool", threadPoolProperties = {
			@HystrixProperty(name = "coreSize", value = "30"), @HystrixProperty(name = "maxQueueSize", value = "10"), }, commandProperties = {
					@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
					@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "75"),
					@HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "7000"),
					@HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "15000"), @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "5") })
	public List<User> findUsersByGroups(List<Group> groups) {
		return userFeignClient.findUsersByGroups(groups, UserContextHolder.getContext().getAuthToken());
	}

	@SuppressWarnings("unused")
	private List<User> buildFallbackUserList() {
		List<User> fallbackUserList = new ArrayList<>();
		User user = new User("lp.fallback.account@gmail.com");
		fallbackUserList.add(user);

		return fallbackUserList;
	}

	@HystrixCommand(fallbackMethod = "buildFallbackSubGroupIdList", threadPoolKey = "getSubGroupIdListThreadPool", threadPoolProperties = {
			@HystrixProperty(name = "coreSize", value = "30"), @HystrixProperty(name = "maxQueueSize", value = "10"), }, commandProperties = {
					@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
					@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "75"),
					@HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "7000"),
					@HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "15000"), @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "5") })
	private List<Integer> buildFallbackSubGroupIdList(int selectedSalesGroupId) {
		List<Integer> fallbackSubGroupIdList = new ArrayList<>();
		fallbackSubGroupIdList.add(selectedSalesGroupId);
		return fallbackSubGroupIdList;
	}

	public List<Integer> getSubGroupIdList(int selectedSalesGroupId) {
		return userFeignClient.getSubGroupIdList(GroupType.sales, selectedSalesGroupId, UserContextHolder.getContext().getAuthToken());
	}
	
	public ResponseEntity<byte[]> getPresentationPdf(Presentation presentation) {
		return cocktailFeignClient.getPresentationPdf(presentation, UserContextHolder.getContext().getAuthToken());
	}
	
	public User updateUserRecoveryCode(String email, String recoveryCode) {
		return userFeignClient.updateUserRecoveryCode(email, recoveryCode);
	}
	
	@HystrixCommand(fallbackMethod = "buildFallbackMailMap", threadPoolKey = "findUsersByGroupsThreadPool", threadPoolProperties = {
			@HystrixProperty(name = "coreSize", value = "30"), @HystrixProperty(name = "maxQueueSize", value = "10"), }, commandProperties = {
					@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
					@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "75"),
					@HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "7000"),
					@HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "15000"), @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "5") })
	public Map<String, List<String>> findIngredientUpgradeMailList() {
		String token = UserContextHolder.getContext().getAuthToken();
		return userFeignClient.findIngredientUpgradeMailList(token);
	}

	@SuppressWarnings("unused")
	private Map<String, List<String>> buildFallbackMailMap() {
		Map<String, List<String>> fallbackMailMap = new HashMap<>();
		return fallbackMailMap;
	}
	
}
