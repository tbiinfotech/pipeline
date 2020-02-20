package com.liquidpresentaion.managementservice.clients;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.liquidpresentaion.managementservice.model.Group;
import com.liquidpresentaion.managementservice.model.User;
import com.liquidpresentation.common.GroupType;

@FeignClient(name = "userservice"/*, configuration = UserFeignClientConfiguration.class*/)
public interface UserFeignClient {

	@RequestMapping(
			method = RequestMethod.POST, 
			value = "/internal/v1/users/groups/users", 
			consumes = "application/json")
	List<User> findUsersByGroups(@RequestBody List<Group> groups, @RequestHeader("Authorization") String token);
	
	@RequestMapping(
			method = RequestMethod.GET, 
			value = "/internal/v1/groups/{groupType}/groups/{groupId}/group", 
			consumes = "application/json")
	List<Integer> getSubGroupIdList(@PathVariable("groupType")GroupType groupType, @PathVariable("groupId")int selectedSalesGroupId, @RequestHeader("Authorization") String token);
	
	@RequestMapping(
			method = RequestMethod.POST, 
			value = "/internal/v1/users/register/recoveryCode/modification",
			consumes = "application/json")
	User updateUserRecoveryCode(@RequestParam(name = "email") String email, @RequestParam(name = "recoveryCode") String recoveryCode);
	
	@RequestMapping(
			method = RequestMethod.GET, 
			value = "/internal/v1/users/upgrade/mail/list", 
			consumes = "application/json")
	Map<String, List<String>> findIngredientUpgradeMailList(@RequestHeader("Authorization") String token);
}
