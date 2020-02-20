package com.liquidpresentation.ingredientservice.data;

import java.util.List;
import java.util.Map;

import com.liquidpresentation.ingredientservice.model.Price;

public interface DataBuilder {

	boolean build(Price price);
	boolean hasNext();
	void setNext(DataBuilder nextBuilder);
	DataBuilder next();
	Map<DataKey, List> collect();
}
