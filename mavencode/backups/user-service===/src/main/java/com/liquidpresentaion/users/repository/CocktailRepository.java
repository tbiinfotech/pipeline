package com.liquidpresentaion.users.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.liquidpresentaion.users.model.Brand;
import com.liquidpresentaion.users.model.Cocktail;

public interface CocktailRepository extends CrudRepository<Cocktail, Integer> {
	long countBySupplierPkId(int supplierGroupId);
	long countBySupplierPkIdAndPublished(int supplierGroupId, boolean published);
	
	@Query(value = "select id from Cocktail c where c.brandPkId = ?1 ")
	List<Integer> findByBrandPkId(int brandPkId);
	@Query(value = "select id from Cocktail c where c.supplierPkId = ?1 ")
	List<Integer> findBySupplierPkId(int supplierPkId);
	List<Cocktail> findByMixologistPkIdInAndPublishedIs(List<Integer> MixologistPkIds,Boolean published);
	List<Cocktail> findByCreatePkIdIn(List<Integer> userIds);
	
	@Query(value = "update lp_cocktail set ct_brand_name = :brandName where ct_brand_pkid = :brandPkId", nativeQuery = true)
	public void updateCocktailBrandNameUseBrandPkId(@Param("brandPkId") int brandPkId, @Param("brandName") String brandName);
	
	@Query(value="select " + 
			"  lu.u_email email, " + 
			"  count(ct.pk_id) num " + 
			"from lp_cocktail ct " + 
			"inner join lp_user lu " + 
			"on ct.ct_create_user_pkid = lu.pk_id " + 
			"where ct.ct_brand_pkid = :brandId " + 
			"OR EXISTS ( " + 
			"  select 1 from lp_cocktail_brand lcb " + 
			"  where lcb.cb_brand_pkid = :brandId " + 
			"  and lcb.cb_cocktail_pkid = ct.pk_id " + 
			") " + 
			"group by lu.u_email; "
			,nativeQuery = true)
	List<Map<String,String>> findByBrandPkIdIs(@Param("brandId") int brandId);
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
	
	List<Cocktail> findByBrandPkIdIn(List<Integer> brandPkIds);
	
	List<Cocktail> findByArchiveFlag(int archiveFlag);}
