package com.liquidpresentaion.cocktailservice.specification;

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

import com.liquidpresentaion.cocktailservice.model.Presentation;

import static com.liquidpresentation.common.utils.StringUtil.*;

public class PresentationSpecification {

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
}
