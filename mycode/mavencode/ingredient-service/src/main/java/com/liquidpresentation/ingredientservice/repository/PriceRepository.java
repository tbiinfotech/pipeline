package com.liquidpresentation.ingredientservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.liquidpresentation.ingredientservice.model.Group;
import com.liquidpresentation.ingredientservice.model.Price;

@Repository
public interface PriceRepository extends PagingAndSortingRepository<Price, Long> {
	public Price findByPkId(long pkId);
	public Page<Price> findByPrSalesGroupPkidIn(List<Long> prSalesGroupPkids, Pageable pageable);
	public List<Price> findAllByPrSalesGroupPkid(long prSalesGroupPkid);
	public List<Price> findByPrSalesGroupPkid(Group prSalesGroupPkid);
	public List<Price> findAllByPrDistribtorItemCodeStartingWithOrderByPkIdDesc(String itemCode);
	public boolean existsByPrIngredientPkid(long prIngredientPkid);
	public Page<Price> findByPrSalesGroupPkidAndPrDescriptionIgnoreCaseContaining(long prSalesGroupPkid, String prDescription, Pageable pageable);
	public Price findByPrIngredientPkidAndPrDistributorPkidAndPrStateAndPrSalesGroupPkid(long prIngredientPkid, long prDistributorPkid, String prState, long prSalesGroupPkid);

	@Query("select p from Price p where p.prDistributorPkid = ?1 and p.prDistribtorItemCode = ?2 and (prSalesGroupPkid = ?3 or prState = ?4)")
	public Optional<Price> findByPrDistributorPkidAndPrDistribtorItemCodeAndPrSalesGroupPkidOrPrState(long prDistributorPkid, String prDistribtorItemCode, long prSalesGroupPkid, String prState);
	public void deleteByPrDistributorPkidAndPrDistribtorItemCodeAndPrSalesGroupPkidOrPrState(Long prDistributorPkid,
                                                                                             String prDistribtorItemCode, Long prSalesGroupPkid, String prState);
	public Page<Price> findAll(Specification<Price> priceSearchSpecification, Pageable pageRequest);
	
	@Query(value = "SELECT " + 
			"price.pk_id ," + 
			"price.pr_ingredient_pkid ," + 
			"price.pr_base_spirit_category ," + 
			"price.pr_case_pack ," + 
			"price.pr_case_price ," + 
			"price.pr_category ," + 
			"price.pr_create_timestamp ," + 
			"price.pr_create_user_pkid ," + 
			"price.pr_description ," + 
			"price.pr_distribtor_item_code ," + 
			"price.pr_distributor_pkid ," + 
			"price.pr_mpc ," + 
			"price.pr_sales_group_pkid ," + 
			"price.pr_size ," + 
			"price.pr_state ," + 
			"price.pr_supplier_group_pkid ," + 
			"price.pr_uom ," + 
			"price.pr_upc ," + 
			"price.pr_update_timestamp ," + 
			"price.pr_update_user_pkid " + 
			"FROM " + 
			"	(SELECT *,ROUND( pr_case_price/pr_case_pack , 2) pr_bottle_price FROM lp_ingredientpricing) price " + 
			"WHERE " + 
			"price.pr_sales_group_pkid IN :groupIds AND ( " + 
			" cast(price.pr_size as varchar(100)) LIKE '%' || :keyword || '%' " + 
			" OR cast(price.pr_case_pack as varchar(100)) LIKE '%' || :keyword || '%' " +
			" OR cast(price.pr_case_price as varchar(100)) LIKE '%' || :keyword || '%' " +
			" OR cast(price.pr_bottle_price as varchar(10)) LIKE '%' || :keyword || '%' )", nativeQuery = true)
	public List<Price> findByPrSizeAndPrCasrPackNativeQuery(@Param("groupIds") List<Long> groupId, @Param("keyword") String keyword);
	
	//wanggjiao
	public List<Price> findByPrSalesGroupPkid(long groupPkid);
	
	public List<Price> findByPrIngredientPkid(Long ingredientId);
}

