package com.liquidpresentaion.cocktailservice.specification;

import static com.liquidpresentation.common.utils.StringUtil.isNotEmpty;
import static com.liquidpresentation.common.utils.StringUtil.toDate;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.joda.time.DateTime;
import org.springframework.data.jpa.domain.Specification;

import com.liquidpresentaion.cocktailservice.model.Cocktail;
import com.liquidpresentaion.cocktailservice.model.Presentation;

public class SearchSpecification {

	public static Specification<Presentation> presentationsSearch(String startDate, String endDate, String keyword, Integer currentUserPkid, List<Integer> sameGroupUsersList) {

		return new Specification<Presentation>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -104840007972661847L;

			@Override
			public Predicate toPredicate(Root<Presentation> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<>();
				
				if (isNotEmpty(startDate)) {
					Path<Date> psDate = root.<Date> get("date");
					Predicate psDateAfter = cb.greaterThan(psDate, toDate(startDate));
					predicates.add(psDateAfter);
				} else {
					Path<Date> psDate = root.<Date> get("date");
					Predicate psDateAfter = cb.greaterThan(psDate, new Date(new DateTime().minusMonths(6).getMillis()));//query past 6 month by default
					predicates.add(psDateAfter);
				}
				
				if (isNotEmpty(endDate)) {
					Path<Date> psDate = root.<Date> get("date");
					Predicate psDateBefore = cb.lessThan(psDate, toDate(endDate));
					predicates.add(psDateBefore);
				}
				
				if (isNotEmpty(keyword)) {
					Expression<String> title = root.<String> get("title");
					Predicate titleLike = cb.like(cb.upper(title),  "%"+keyword.toUpperCase()+"%");
					predicates.add(titleLike);
				}

				if (currentUserPkid != null) {
					Expression<Integer> createPkId = root.get("createPkId");
					Predicate createPkIdEquals = cb.equal(createPkId, currentUserPkid);
					predicates.add(createPkIdEquals);
					
				}
				
				if (sameGroupUsersList != null) {
					Expression<Integer> exp = root.get("createPkId");
					Predicate sameGroupUsers = exp.in(sameGroupUsersList);
					predicates.add(sameGroupUsers);
				}
				
				return cb.and(predicates.toArray(new Predicate[predicates.size()]));
			}
		};
	}
	
	public static Specification<Cocktail> cocktailsSearch(String keyword, List<FilterCriteria> filterCriteria, List<Integer> cocktailsIdListFromSalesGroups, List<Integer> supplierGroupIdList, boolean searchPublishedOnly) {

		return new Specification<Cocktail>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -5393960491190085289L;

			@Override
			public Predicate toPredicate(Root<Cocktail> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates2 = new ArrayList<>();
				List<Predicate> predicates3 = new ArrayList<>();
				
//				if (isNotEmpty(keyword)) {
//					Expression<String> brandName = root.<String> get("brandName");
//					Predicate brandNameLike = cb.like(cb.upper(brandName),  "%"+keyword.toUpperCase()+"%");
//					predicates2.add(brandNameLike);
//					
////					Expression<String> baseSpiritCategory = root.<String> get("baseSpiritCategory");
////					Predicate baseSpiritCategoryLike = cb.like(cb.upper(baseSpiritCategory),  "%"+keyword.toUpperCase()+"%");
////					predicates.add(baseSpiritCategoryLike);
////					predicates2.add(baseSpiritCategoryLike);
//					
//					Expression<String> name = root.<String> get("name");
//					Predicate nameLike = cb.like(cb.upper(name),  "%"+keyword.toUpperCase()+"%");
//					predicates2.add(nameLike);
//					
//					Join<Cocktail, Group> joinSupplierGroup = root.join("supplierGroup");
//					Expression<String> supplierGroupExp = joinSupplierGroup.get("name");
//					predicates2.add(cb.like(cb.upper(supplierGroupExp), "%" + keyword.toUpperCase()+ "%"));
//					
//					SetJoin joinCategory = root.joinSet("brandSet");
//					Expression<String> categorySetExp = joinCategory.get("brandName");
//					predicates2.add(cb.like(cb.upper(categorySetExp), "%" + keyword.toUpperCase()+ "%"));
//				}
//				Predicate p2 = cb.or(predicates2.toArray(new Predicate[predicates2.size()]));
				
				if (supplierGroupIdList != null && !supplierGroupIdList.isEmpty()) {
					Expression<Integer> exp = root.get("supplierPkId");
					Predicate supplierPkIdIn = exp.in(supplierGroupIdList);
					predicates3.add(supplierPkIdIn);
				}
				
				if (cocktailsIdListFromSalesGroups != null && !cocktailsIdListFromSalesGroups.isEmpty()) {
					Expression<Integer> exp = root.get("id");
					Predicate pkIdIn = exp.in(cocktailsIdListFromSalesGroups);
					predicates3.add(pkIdIn);
				}
				Predicate p3 = cb.or(predicates3.toArray(new Predicate[predicates3.size()]));
				
				if (searchPublishedOnly) {
					Expression<Boolean> published = root.get("published");
					Predicate publishedEquals = cb.equal(published, true);
					
//					return cb.and(publishedEquals, p2, p3);
					return cb.and(publishedEquals, p3);
				} else {
//					return cb.and(p2, p3);
					return p3;
				}
			}
		};
	}
}
