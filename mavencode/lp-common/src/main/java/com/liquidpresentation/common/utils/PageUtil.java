package com.liquidpresentation.common.utils;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.util.CollectionUtils;

public final class PageUtil {

	public static PageRequest buildPageRequest(int pageNumber, int pageSize, String sortProperty, boolean asc){
		if(StringUtil.isNotEmpty(sortProperty) && sortProperty.contains(",")) {
			return PageRequest.of(pageNumber, pageSize, Sort.by(asc ? Direction.ASC : Direction.DESC, sortProperty.split(",")));
		}else {
			return PageRequest.of(pageNumber, pageSize, Sort.by(asc ? Direction.ASC : Direction.DESC, sortProperty));
		}
	}
	
	public static PageRequest buildPageRequest(int pageNumber, int pageSize){
		return PageRequest.of(pageNumber, pageSize);
	}
	
	public static <T> List<T> buildPage(List<T> list,int page,int size) {
		if (CollectionUtils.isEmpty(list)) {
			return list;
		}
		return list.subList(page*size > list.size() ? 0 : page*size,
				list.size() > (page*size + size) ? page*size + size : list.size());
	}
}
