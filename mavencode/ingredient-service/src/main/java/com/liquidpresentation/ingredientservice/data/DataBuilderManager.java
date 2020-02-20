package com.liquidpresentation.ingredientservice.data;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.liquidpresentation.ingredientservice.model.Price;

@Service
public class DataBuilderManager {

	private List<Price> priceList;
	private DataBuilder firstBuilder;
	private Map<DataKey, List> buildResults;
	
	public DataBuilderManager withPriceList(List<Price> priceList){
		this.priceList  = priceList;
		return this;
	}
	
	public DataBuilderManager firstDataBuilder(DataBuilder firstBuilder) {
		this.firstBuilder = firstBuilder;
		return this;
	}
	
	public DataBuilderManager nextDataBuilder(DataBuilder nextBuilder){
		this.firstBuilder.setNext(nextBuilder);
		return this;
	}
	
	public DataBuilderManager build(){
		for (Iterator<Price> iterator = priceList.iterator(); iterator.hasNext();) {
			if (this.firstBuilder.build(iterator.next())) {
				iterator.remove();
			}
		}
		return this;
	}
	
	public void collect(){
		this.buildResults = this.firstBuilder.collect();
	}
	
	public List<Price> getBadPricList(){
		return this.buildResults.get(DataKey.BADPRICE);
	}
}
