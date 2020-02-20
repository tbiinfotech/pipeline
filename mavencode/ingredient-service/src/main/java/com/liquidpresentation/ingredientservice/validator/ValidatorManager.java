package com.liquidpresentation.ingredientservice.validator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.liquidpresentation.ingredientservice.model.Price;

@Service
public final class ValidatorManager {

	private List<PriceValidator> validators;
	
    private static final Logger logger = LoggerFactory.getLogger(ValidatorManager.class);
    
    @Autowired
    private MessageSource messageSource;

    public ValidatorManager build(){
		this.validators = new LinkedList<>();
		return this;
	}
	
	public ValidatorManager withValidator(PriceValidator validator){
		this.validators.add(validator);
		return this;
	}
	
	public List<ValidationResult> validate(List<Price> prices){
		List<ValidationResult> results = new ArrayList<>();
		
		Price price;
		for (Iterator<Price> iterator = prices.iterator(); iterator.hasNext();) {
			price = iterator.next();
			boolean failed = false;
			for (PriceValidator validator : validators) {
				logger.info(messageSource.getMessage("validator.start", new Object[]{validator.getClass().getSimpleName()}, Locale.US));
				ValidationResult result = validator.validate(price);
				if (result.isFailed()) {
					results.add(result);
					failed = true;
				}
				logger.info(messageSource.getMessage("validator.end", new Object[]{validator.getClass().getSimpleName(), (failed?"Failed":"Passed")}, Locale.US));
			}
			if (failed) {
				iterator.remove();
			}
		}
		return results;
	}
}
