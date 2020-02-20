package com.liquidpresentaion.users.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.liquidpresentaion.users.model.Ingredient;
import com.liquidpresentaion.users.model.User;
import com.liquidpresentaion.users.services.ConsoleService;

@Controller
@RequestMapping(value = "console")
public class ConsoleController {
	private String encodeKouling = "$2a$12$x9oMaWIod2I27gjncu7c3e9LpUO0AyA0W1yJUxcacOY2gwZJwPQz6";
	
	@Autowired
	private ConsoleService consoleService;
	
	@RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)
	public ModelAndView getUserGroups(@PathVariable("userId") Long userId, @RequestParam(name = "kouling", defaultValue = "")String rawKouling){
		
		ModelAndView modelView = new ModelAndView("UserGroup");
		try {
			this.kouLing(rawKouling);
			
			User user = consoleService.findUserById(userId.intValue());
			modelView.addObject("user", user);
		} catch (Exception e) {
			modelView.addObject("error", e.getMessage());
		}
		return modelView;
	}
	
	@RequestMapping(value = "/ingredients/{ingredientId}", method = RequestMethod.GET)
	public ModelAndView getIngrent(@PathVariable("ingredientId") Long ingredientId, @RequestParam(name = "kouling", defaultValue = "")String rawKouling){
		this.kouLing(rawKouling);
		
		ModelAndView modelView = new ModelAndView("UserGroup");
		Ingredient ingredient = consoleService.findIngredientById(ingredientId.intValue());
		
		modelView.addObject("ingredient", ingredient);
		return modelView;
	}
	
	private void kouLing(String rawKouling){
		PasswordEncoder encoder = new BCryptPasswordEncoder(12);
		if (!encoder.matches(rawKouling, encodeKouling)) {
			throw new RuntimeException("Kou Ling Cuo Wu!");
		}
	}
}
