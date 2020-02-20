package com.liquidpresentaion.users.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.liquidpresentaion.users.context.UserContextHolder;
import com.liquidpresentaion.users.model.UserColumn;
import com.liquidpresentaion.users.services.UserColumnService;

@RestController
@RequestMapping(value="internal/v1/users/{userId}/columns")
public class UserColumnServiceController {

	@Autowired
	private UserColumnService userColumnService;
	
	@RequestMapping(method = RequestMethod.GET)
	public List<UserColumn> getUserColumns(@PathVariable("userId") int userId){
		return this.userColumnService.findByUserPkid(UserContextHolder.getContext().getUserId().intValue());
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public void addUserColumn(@PathVariable("userId") Integer userId, @RequestBody UserColumn userColumn){
		userColumnService.addUserColumn(userColumn);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public void updateUserColumn(@PathVariable("userId") int userId, @RequestBody List<UserColumn> columns){
		userColumnService.updateUserColumn(columns);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/defaults")
	public void resetDefault(@PathVariable("userId") int userId){
		userColumnService.resetDefault(UserContextHolder.getContext().getUserId().intValue());
	}
	
	@RequestMapping(method = RequestMethod.DELETE)
	public void removeUserColumn(@PathVariable("userId") int userId, @RequestBody UserColumn userColumn){
		userColumnService.removeUserColumn(userColumn);
	}
}
