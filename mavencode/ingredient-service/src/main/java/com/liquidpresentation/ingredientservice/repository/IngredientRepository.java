package com.liquidpresentation.ingredientservice.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.liquidpresentation.common.Category;
import com.liquidpresentation.ingredientservice.model.Ingredient;

@Repository
public interface IngredientRepository extends PagingAndSortingRepository<Ingredient, Long> {
	public Ingredient findByPkId(long pkId);
	public Ingredient findByPkIdAndInCategory(long pkId, Category InCategory);
	public Page<Ingredient> findByInNewCust(boolean inNewCust, Pageable pageable);
	public Page<Ingredient> findByInNewCustAndInNameIgnoreCaseContaining(boolean inNewCust, String keyword, Pageable pageable);
	public Page<Ingredient> findByInNameIgnoreCaseContainingOrInMpcIgnoreCaseContainingOrInCategoryIgnoreCaseContainingOrInTypeIgnoreCaseContaining(String keyword, Pageable pageable);
	public boolean existsByInBrandPkid(long inBrandPkid);
	public List<Ingredient> findAll();
	public List<Ingredient> findByInMpc(String mpc);
	public List<Ingredient> findAllByInDistribtorItemCodeStartingWithOrderByPkIdDesc(String itemCode);
	public boolean existsByInDistribtorPkidAndInDistribtorItemCode(long inDistribtorPkid, String inDistribtorItemCode);
	public List<Ingredient> findByInDistribtorPkidAndInDistribtorItemCode(Long inDistribtorPkid, String inDistribtorItemCode);
	public boolean existsByInName(String inName);
	public boolean existsByInNameAndInCategory(String inName, Category category);
	public List<Ingredient> findByInNameAndInCategory(String InName, Category InCategory);
	public Ingredient findByInName(String InName);
	public List<Ingredient> findByInDistribtorPkidAndDistributorBrandId(long inDistribtorPkid, String distributorBrandId);
	
	@Query(value = "SELECT " + 
			"	lpIn.pk_id , " + 
			"	lpIn.in_brand_pkid , " + 
			"	lpIn.in_create_user_pkid , " + 
			"	lpIn.in_base_spirit_category , " + 
			"	lpIn.in_case_pack , " + 
			"	lpIn.in_category , " + 
			"	lpIn.in_create_timestamp , " + 
			"	lpIn.in_distribtor_item_code , " + 
			"	lpIn.in_distributor_pkid , " + 
			"	lpIn.in_group_name , " + 
			"	lpIn.in_method , " + 
			"	lpIn.in_mpc , " + 
			"	lpIn.in_name , " + 
			"	lpIn.in_new_cust , " + 
			"	lpIn.in_size , " + 
			"	lpIn.in_supplier_group_pkid , " + 
			"	lpIn.in_type , " + 
			"	lpIn.in_uom , " + 
			"	lpIn.in_upc , " + 
			"	lpIn.in_update_timestamp , " + 
			"	lpIn.in_update_user_pkid " + 
			"FROM " + 
			"lp_ingredient lpIn " + 
			"WHERE " + 
			"	UPPER (lpIn.in_name) LIKE '%' || UPPER ( :keyWord ) || '%' " + 
			"	OR UPPER ( lpIn.in_mpc) LIKE '%' || UPPER ( :keyWord ) || '%' " + 
			"	OR UPPER (lpIn.in_type) LIKE '%' || UPPER ( :keyWord ) || '%' " + 
			"	OR UPPER ( lpIn.in_category) LIKE '%' || UPPER ( :keyWord ) || '%' " +
			"   OR (lpIn.in_brand_pkid IN :brandPkIds) " + 
			"   OR (lpIn.in_supplier_group_pkid IN :supplierPkIds)",nativeQuery = true)
	public Page<Ingredient> findIngredinetLikeNativeQuery(@Param(value = "keyWord") String keyWord, @Param(value = "brandPkIds") List<Long> brandPkIds, @Param(value = "supplierPkIds") List<Long> supplierPkIds, Pageable pageable);
	
	Page<Ingredient> findAll(Specification<Ingredient> presentationsSearchSpecification, Pageable pageRequest);
	
	@Query(value="SELECT * FROM lp_ingredient WHERE cast(in_size AS VARCHAR(100)) LIKE '%' ||  :inSize  || '%' AND UPPER ( in_uom) LIKE '%' ||  UPPER ( :inUom )  || '%'",nativeQuery = true)
	List<Ingredient> findIngredinetLikeInSize(@Param("inSize") String inSize, @Param("inUom") String inUom);
	
	
	/*@Query(value="SELECT * FROM lp_ingredient T "
			+ "WHERE T.pk_id IN ("
			+ "SELECT P.pr_ingredient_pkid FROM lp_ingredientpricing AS P WHERE P.pr_sales_group_pkid IN :groupPkids )"
			+ "AND T.in_brand_pkid IN :brandPkId " 
			+ "AND T.in_distributor_pkid IN :distributorPkIds ",nativeQuery = true)*/
	@Query(value="SELECT * FROM lp_ingredient T "
			+ "WHERE T.pk_id IN ("
			+ "SELECT P.pr_ingredient_pkid FROM lp_ingredientpricing AS P WHERE P.pr_sales_group_pkid IN :groupPkids )"
			+ "AND T.in_distributor_pkid IN :distributorPkIds," 
			+ "AND UPPER (T.in_name) LIKE '%' || UPPER ( :keyWord ) || '%' " 
			+ "	OR UPPER ( T.in_size) LIKE '%' || UPPER ( :keyWord ) || '%' " 
			+ "	OR UPPER (T.in_uom) LIKE '%' || UPPER ( :keyWord ) || '%' " 
			+ " OR (T.in_brand_pkid IN :brandPkIds) "   ,nativeQuery = true)
	public Page<Ingredient> findAllsParam(@Param(value = "keyWord") String keyWord, @Param(value = "brandPkId") List<Long> brandPkId, @Param(value = "distributorPkIds") List<Long> distributorPkIds, @Param(value = "groupPkids") List<Long> groupPkids, Pageable pageable);
	
	@Query(value="SELECT T.* FROM lp_ingredient T  " + 
			"			WHERE T.pk_id IN ( " + 
			"			SELECT P.pr_ingredient_pkid FROM lp_ingredientpricing AS P WHERE P.pr_sales_group_pkid IN :groupPkids ) " + 
			"			 AND T.in_distributor_pkid IN :distributorPkIds ",nativeQuery = true)
	public List<Ingredient> findAlls(@Param(value = "distributorPkIds") List<Long> distributorPkIds, @Param(value = "groupPkids") List<Long> groupPkids);
	public List<Ingredient> findByInMpcOrInName(String gtin, String name);
	
	@Query(value="SELECT li.pk_id pkid,"
			+ "EXISTS("
			+ "SELECT 1 FROM lp_brand lb WHERE lb.br_name = li.in_name OR li.in_old_brand_id IS NOT NULL"
			+ ") isupgrade "
			+ "from lp_ingredient li ",nativeQuery = true)
	public List<Map<Long,Object>> getIsUpgradeds();
	
	@Query(value="SELECT * FROM lp_ingredient where	in_category != '' ",nativeQuery = true)
	public List<Ingredient> findAllByInCategoryNotNull();
}

