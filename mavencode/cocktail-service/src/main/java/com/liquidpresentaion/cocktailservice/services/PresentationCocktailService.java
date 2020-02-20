package com.liquidpresentaion.cocktailservice.services;

import static com.liquidpresentation.common.utils.StringUtil.toDate;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.liquidpresentaion.cocktailservice.model.Presentation;
import com.liquidpresentaion.cocktailservice.model.PresentationCocktail;
import com.liquidpresentaion.cocktailservice.repository.PresentationCocktailRepository;
import com.liquidpresentaion.cocktailservice.repository.PresentationRepository;

@Service
public class PresentationCocktailService {
	@Autowired
	private PresentationCocktailRepository presentationCocktailRepository;
	
	@Autowired
	private PresentationRepository presentationRepository;
	
	public List<Presentation> findCustomerAcctNameIsBycocktailId (String startDate,String endDate, Integer cocktailId) {
		List<Integer> presentationIds = new ArrayList<>();
		List<PresentationCocktail> presentationCocktails = presentationCocktailRepository.findByCocktailIdIs(cocktailId);
		if (presentationCocktails.size() == 0 || presentationCocktails == null) {
			return null;
		}
		presentationCocktails.forEach(p->{
			presentationIds.add(p.getPresenttationId());
		}); 
		return presentationRepository.findByCreateDateBetweenAndIdInOrderByCustomerAcctName(toDate(startDate), toDate(endDate), presentationIds);
	}
	
	public List<Presentation> findCustomerAcctNameBySalesGroupId (String startDate,String endDate, Integer salesGroupId) {
		return presentationRepository.findPresentationNativeQuery(toDate(startDate), toDate(endDate), salesGroupId);
	}
	
	public List<Presentation> findCustomerAcctNameBySalesGroupIdAndAdminUserId (String startDate,String endDate, Integer salesGroupId) {
		return presentationRepository.findPresentationNativeQueryBySaleGroup(toDate(startDate), toDate(endDate), salesGroupId);
	}
	
	public List<Presentation> findCustomerAcctNameBySalesGroupId (String startDate,String endDate, Integer salesGroupId, Integer cocktailId) {
		return presentationRepository.findPresentationNativeQuery(toDate(startDate), toDate(endDate), salesGroupId,cocktailId);
	}
}