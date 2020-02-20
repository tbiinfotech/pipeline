package com.liquidpresentaion.cocktailservice.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.liquidpresentaion.cocktailservice.model.Cocktail;

@Repository
public interface CocktailRepository extends PagingAndSortingRepository<Cocktail, Integer> {

	Page<Cocktail> findByBrandNameIgnoreCaseContainingOrNameIgnoreCaseContainingOrMixologistNameIgnoreCaseContaining(String brandName, String name, String mixologistName, Pageable of);
	
	Page<Cocktail> findByBrandNameIgnoreCaseContaining(String brandName, Pageable of);
	Page<Cocktail> findAll(Specification<Cocktail> cocktailSearchSpecification, Pageable of);
	List<Cocktail> findAll(Specification<Cocktail> cocktailSearchSpecification);
	List<Cocktail> findByIdIn(List<Integer> pkidList);
	Cocktail findByName(String ctName);
	@Query(value = "SELECT DISTINCT  " + 
			"				*   " + 
			"			FROM  " + 
			"				lp_cocktail ct   " + 
			"			WHERE   " + 
			"			 ((   " + 
			"				ct_create_user_pkid IN ( SELECT gu_user_pkid FROM lp_group_user WHERE gu_group_pkid IN ( SELECT gss_supplier_group_pkid FROM lp_group_sales_supplier WHERE gss_sales_group_pkid IN :salesGroupIds ) ))    " + 
			"				OR (  " + 
			"				pk_id IN ( SELECT ctg_cocktail_pkid FROM lp_cocktail_group WHERE ctg_group_pkid IN :salesGroupIds AND ctg_flg IS NULL ))   " + 
			"				OR (   " + 
			"				ct_brand_pkid IN ( SELECT bdg_brand_pkid FROM lp_brand_group WHERE bdg_group_pkid IN :salesGroupIds )    " + 
			"				AND NOT EXISTS (SELECT 1 FROM lp_group_user gu   " + 
			"				JOIN lp_group g ON g.pk_id = gu.gu_group_pkid  " + 
			"				WHERE gu.gu_user_pkid = ct.ct_mixologist_pkid  " + 
			"				AND g.g_type = 'sales'  " + 
			"				AND gu.gu_role = 'MIXOLOGIST'  " + 
			"				)  " + 
			"				)   " + 
			"				OR (  " + 
			"					ct_create_user_pkid IN (  " + 
			"					SELECT  " + 
			"						gu_user_pkid   " + 
			"					FROM  " + 
			"						lp_group_user   " + 
			"					WHERE  " + 
			"						gu_group_pkid IN :salesGroupIds)  " + 
			"				) )AND ct.ct_published IS TRUE",nativeQuery = true)
	List<Cocktail> findBySalesNativeQuery(@Param("salesGroupIds") List<Integer> salesGroupIds);
	
	@Query(value = "SELECT DISTINCT * FROM lp_cocktail ct WHERE (ct.ct_create_user_pkid IN ("
			+ "			SELECT gu_user_pkid FROM lp_group_user WHERE gu_group_pkid IN :salesGroupIds) AND ct_create_user_pkid = :userId ) "
			+ "				OR ( ct.ct_create_user_pkid IN (SELECT gu_user_pkid FROM lp_group_user WHERE gu_group_pkid IN :salesGroupIds AND ct_published IS TRUE))",nativeQuery = true)
	List<Cocktail> findBySalesMixologistNativeQuery(@Param("salesGroupIds") List<Integer> salesGroupIds,@Param("userId") Long userId);
	
	@Query(value = "SELECT * FROM lp_cocktail WHERE ct_create_user_pkid IN (SELECT gu_user_pkid FROM lp_group_user WHERE gu_group_pkid IN :supplierGroupIds)",nativeQuery = true)
	List<Cocktail> findBySupplierMixologistNativeQuery(@Param("supplierGroupIds") List<Integer> supplierGroupIds);
	
	@Query(value = "SELECT * FROM lp_cocktail WHERE ct_create_user_pkid IN (SELECT gu_user_pkid FROM lp_group_user WHERE gu_group_pkid IN :supplierGroupIds) AND ct_published = TRUE",nativeQuery = true)
	List<Cocktail> findBySupplierNativeQuery(@Param("supplierGroupIds") List<Integer> supplierGroupIds);
	
	@Query(value = "select id from Cocktail c where c.brandPkId = ?1 ")
	List<Integer> findByBrandPkId(int brandPkId);
	
	List<Cocktail> findByBrandPkIdIn(List<Integer> brandPkIds);
	
	List<Cocktail> findByArchiveFlag(int archiveFlag);
}
