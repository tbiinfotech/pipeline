package com.liquidpresentation.ingredientservice.specification;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;


import com.liquidpresentation.ingredientservice.model.Ingredient;

public class SearchSpecification {

	
	public static Specification<Ingredient> ingredinetSearch(String keyword, List<Long> brandPkIds, List<Long> supplierGroupIdList,List<Long> pkIdList,List<Integer> userIdList,Boolean showNew) {

		return new Specification<Ingredient>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -5393960491190085289L;

			@Override
			public Predicate toPredicate(Root<Ingredient> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<>();
				List<Predicate> pShowNew = new ArrayList<>();
				if (!StringUtils.isEmpty(keyword)) {
	                Path<String> inMpc = root.get("inMpc");
	                Path<String> inCategory = root.get("inCategoryString");
	                Path<String> inName = root.get("inName");
	                Path<String> inType = root.get("inTypeString");
	                
	                predicates.add(cb.like(cb.upper(inMpc),  "%"+keyword.toUpperCase()+"%"));
	                predicates.add(cb.like(cb.upper(inCategory), "%" + keyword.toUpperCase()+ "%"));
	                predicates.add(cb.like(cb.upper(inName), "%" + keyword.toUpperCase()+ "%"));
	                predicates.add(cb.like(cb.upper(inType), "%" + keyword.toUpperCase()+ "%"));
				}

				if (supplierGroupIdList != null && !supplierGroupIdList.isEmpty()) {
					Expression<Long> exp = root.get("inSupplierGroupPkid");
					Predicate supplierPkIdIn = exp.in(supplierGroupIdList);
					predicates.add(supplierPkIdIn);
				}
				
				if (brandPkIds !=null && !brandPkIds.isEmpty()) {
					Expression<Long> exp = root.get("inBrandPkid");
					Predicate brandPkIdes = exp.in(brandPkIds);
					predicates.add(brandPkIdes);
				}
				if (pkIdList != null && !pkIdList.isEmpty()) {
					Expression<Long> exp = root.get("pkId");
					Predicate pkIds = exp.in(pkIdList);
					predicates.add(pkIds);
				}
				if (userIdList != null && !userIdList.isEmpty()) {
					Expression<Integer> exp = root.get("inCreateUserPkid");
					Predicate userIds = exp.in(userIdList);
					predicates.add(userIds);
				}
				if (showNew) {
					Expression<Boolean> inNewCust = root.get("inNewCust");
					Predicate inNewCusts = cb.equal(inNewCust, showNew);
					pShowNew.add(inNewCusts);
					Predicate p = cb.or(predicates.toArray(new Predicate[predicates.size()]));
					Predicate pNewCust = cb.or(pShowNew.toArray(new Predicate[pShowNew.size()]));
					return cb.and(p,pNewCust);
				}
				Predicate p = cb.or(predicates.toArray(new Predicate[predicates.size()]));
				return p;
			}
		};
	}
}
