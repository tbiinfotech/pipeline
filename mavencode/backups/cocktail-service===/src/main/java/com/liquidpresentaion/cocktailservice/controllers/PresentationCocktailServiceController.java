package com.liquidpresentaion.cocktailservice.controllers;

import static com.liquidpresentaion.cocktailservice.constants.API.PRESENTATION_COCKTAIL_CONTROLLER;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.liquidpresentaion.cocktailservice.context.UserContextHolder;
import com.liquidpresentaion.cocktailservice.model.Presentation;
import com.liquidpresentaion.cocktailservice.services.PresentationCocktailService;
import com.liquidpresentaion.cocktailservice.services.PresentationService;

@RestController
@RequestMapping(PRESENTATION_COCKTAIL_CONTROLLER)
public class PresentationCocktailServiceController {
	@Autowired
	private PresentationCocktailService presentationCocktailService;
	@Autowired
	private PresentationService presentationService;
	
	@RequestMapping(method = RequestMethod.GET)
	public List<String> getPresentationsCocktailByCreatePkId(
			@RequestParam(name = "cocktailId", required = false) Integer cocktailId, 
			@RequestParam(name = "salesGroupId", required = false) Integer salesGroupId,
			@RequestParam(name = "startDate", required = true) String startDate,
			@RequestParam(name = "endDate", required = true) String endDate){
		List<Presentation> list = new ArrayList<>();
		if (UserContextHolder.getContext().isAdmin()) {
			if (cocktailId != null && salesGroupId != null) {
				list = presentationCocktailService.findCustomerAcctNameBySalesGroupId(startDate, endDate, salesGroupId,cocktailId);
			} else {
				list = presentationCocktailService.findCustomerAcctNameBySalesGroupIdAndAdminUserId(startDate, endDate, salesGroupId);
			}
		} else if (!UserContextHolder.getContext().isAdmin() && cocktailId != null) {
			list = presentationCocktailService.findCustomerAcctNameIsBycocktailId(startDate, endDate, cocktailId);
		} else {
			list = presentationCocktailService.findCustomerAcctNameBySalesGroupId(startDate, endDate, salesGroupId);
		}
		return list.stream().map(l -> l.getCustomerAcctName() + "(" + l.getCustomerAccountId() + ")").collect(Collectors.toList());
	}
	
	@RequestMapping(value = "/cocktail/download", method = RequestMethod.GET)
	public void downloadCocktail(@RequestParam(name = "cocktailId" ,required = false) Integer cocktailId,
			@RequestParam(name = "salesGroupId" ,required = false) Integer salesGroupId,
			@RequestParam(name = "startDate", required = true) String startDate,
			@RequestParam(name = "endDate", required = true) String endDate,
			HttpServletResponse response) throws Exception {
		List<Presentation> list = new ArrayList<>();
		if (UserContextHolder.getContext().isAdmin()) {
			if (cocktailId != null && salesGroupId != null) {
				list = presentationCocktailService.findCustomerAcctNameBySalesGroupId(startDate, endDate, salesGroupId,cocktailId);
			} else {
				list = presentationCocktailService.findCustomerAcctNameBySalesGroupIdAndAdminUserId(startDate, endDate, salesGroupId);
			}
		} else if (!UserContextHolder.getContext().isAdmin() && cocktailId != null) {
			list = presentationCocktailService.findCustomerAcctNameIsBycocktailId(startDate, endDate, cocktailId);
		} else {
			list = presentationCocktailService.findCustomerAcctNameBySalesGroupId(startDate, endDate, salesGroupId);
		}
		presentationService.exportCustomers(response, list);
	}
	
}
