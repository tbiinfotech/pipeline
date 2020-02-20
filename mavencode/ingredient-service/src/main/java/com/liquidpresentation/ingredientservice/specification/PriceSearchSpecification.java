package com.liquidpresentation.ingredientservice.specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.liquidpresentation.ingredientservice.model.Price;

public class PriceSearchSpecification {
	public static Specification<Price> PriceSearch(String keyword,List<Long> supplierIds,List<Long> groupIds,Set<Long> priceIds) {
		return new Specification<Price>() {
			
			private static final long serialVersionUID = -5393960491190085289L;
			
			@Override
			public Predicate toPredicate(Root<Price> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<>();
				List<Predicate> prSalesGroupPkids = new ArrayList<>();
				List<Predicate> prSizeAndPrCasePack = new ArrayList<>();
				if (!StringUtils.isEmpty(keyword)) {
	                Path<String> prMpc = root.get("prMpc");
	                Path<String> prDistribtorItemCode = root.get("prDistribtorItemCode");
	                Path<String> prDescription = root.get("prDescription");
	                Path<String> prCategoryString = root.get("prCategoryString");
	                Path<String> prUom = root.get("prUom");
	                Path<Long> prSalesGroupPkid = root.get("prSalesGroupPkid");
	                predicates.add(cb.like(cb.upper(prMpc),  "%"+keyword.toUpperCase()+"%"));
	                predicates.add(cb.like(cb.upper(prDistribtorItemCode), "%" + keyword.toUpperCase()+ "%"));
	                predicates.add(cb.like(cb.upper(prDescription), "%" + keyword.toUpperCase()+ "%"));
	                predicates.add(cb.like(cb.upper(prCategoryString), "%" + keyword.toUpperCase()+ "%"));
	                predicates.add(cb.like(cb.upper(prUom), "%" + keyword.toUpperCase()+ "%"));
	                for (Long groupId : groupIds) {
	                	prSalesGroupPkids.add(cb.equal(prSalesGroupPkid,groupId));
	                }
				}
				
				if (!CollectionUtils.isEmpty(priceIds)) {
					Expression<BigDecimal> prPkIds = root.get("pkId");
					Predicate prPkIdsIn = prPkIds.in(priceIds);
					prSizeAndPrCasePack.add(prPkIdsIn);
				}
				
				if (supplierIds != null && supplierIds.size()>0) {
					Expression<Long> exp = root.get("prSupplierGroupPkid");
					Predicate supplierPkIdIn = exp.in(supplierIds);
					predicates.add(supplierPkIdIn);
				}
				
				Predicate pre = cb.or(predicates.toArray(new Predicate[predicates.size()]));
				Predicate group = cb.or(prSalesGroupPkids.toArray(new Predicate[prSalesGroupPkids.size()]));
				Predicate p = cb.or(prSizeAndPrCasePack.toArray(new Predicate[prSizeAndPrCasePack.size()]));
				if (prSizeAndPrCasePack.size() < 1) {
					return cb.and(pre,group);
				}
				return cb.and(cb.or(pre,p),group);
			}
		};
	}
}
