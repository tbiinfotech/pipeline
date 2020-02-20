package com.liquidpresentaion.cocktailservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.liquidpresentaion.cocktailservice.model.Price;

@Repository
public interface PriceRepository extends PagingAndSortingRepository<Price, Long> {

	@Query(value = "SELECT DISTINCT P " + 
			"	.*, " + 
			"	b.pk_id brand_pkid  " + 
			"FROM " + 
			"	lp_ingredientpricing P " + 
			"	JOIN lp_ingredient i ON P.pr_ingredient_pkid = i.pk_id " + 
			"	JOIN lp_brand b ON i.in_brand_pkid = b.pk_id " + 
			"	JOIN lp_cocktail_brand cb ON b.pk_id = cb.cb_brand_pkid  " + 
			"	AND cb.cb_cocktail_pkid IN ( SELECT DISTINCT pc_cocktail_pkid FROM lp_presentation_cocktail WHERE pc_presentation_pkid = :presentationPkid ) " + 
			"UNION " + 
			"	SELECT DISTINCT P " + 
			"	.*, " + 
			"	i.in_brand_pkid brand_pkid  " + 
			"	FROM " + 
			"	lp_ingredientpricing P " + 
			"	JOIN lp_ingredient i ON P.pr_ingredient_pkid = i.pk_id " + 
			"	AND i.in_brand_pkid IN ( " + 
			"	SELECT ih_brand_pkid FROM lp_brand_housemade WHERE ih_housemade_ingredient_pkid IN (SELECT pk_id FROM lp_ingredient WHERE in_brand_pkid IN (SELECT cb_brand_pkid FROM lp_cocktail_brand WHERE cb_cocktail_pkid IN ( SELECT DISTINCT pc_cocktail_pkid FROM lp_presentation_cocktail WHERE pc_presentation_pkid = :presentationPkid ) AND cb_category = 'HOUSEMADE')) " + 
			"	) " + 
			"ORDER BY " + 
			"	brand_pkid ASC",
	nativeQuery = true)
	public List<Price> findByPresentationPkid(@Param(value = "presentationPkid")long presentationPkid);
	
	public List<Price> findByPrSalesGroupPkidIn(List<Long> prSalesGroupPkids);
}

