package com.liquidpresentation.ingredientservice.validator;

import com.liquidpresentation.ingredientservice.model.Price;
import com.liquidpresentation.ingredientservice.model.PriceCsv;

public class ValidationResult {

	private boolean failed;
	private Price price;
	private String message;
	
	public ValidationResult(boolean failed, Price price, String message){
		this.failed = failed;
		this.price = price;
		this.message = message;
	}
	public boolean isFailed() {
		return failed;
	}
	public Price getPrice() {
		return price;
	}
	public String getMessage() {
		return message;
	}
	public PriceCsv getPriceCsv(String batchId){
		return this.price.getPriceCsv(batchId, this.message);
	}
}
