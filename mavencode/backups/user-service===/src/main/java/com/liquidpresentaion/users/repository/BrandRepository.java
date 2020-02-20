package com.liquidpresentaion.users.repository;

import com.liquidpresentaion.users.model.Brand;
import com.liquidpresentaion.users.model.Group;
import com.liquidpresentation.common.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface BrandRepository extends PagingAndSortingRepository<Brand, Integer> {
	public Page<Brand> findByDeletedNotAndNameIgnoreCaseContainingOrSupplierGroupInOrIdIn(int deletedId, String name, Pageable pageable, List<Group> groups, List<Integer> brandIds);
	public List<Brand> findByDeletedNotAndBrCategoryAndNameContainingIgnoreCaseOrderByName(int deletedId, Category brCategory, String name);
	public List<Brand> findByBrCategoryNotInAndNameContainingIgnoreCaseOrderByName(List<Category> brCategory, String name);
	public List<Brand> findByNameContainingIgnoreCaseOrderByName(String name);
	public List<Brand> findByIdInOrderByName(Set<Integer> pkIds);
	@Query(value = "SELECT * FROM lp_brand WHERE UPPER (br_category) LIKE UPPER('%' || :name || '%')",nativeQuery = true)
	public List<Brand> findByBrCategoryNativeQuery(@Param("name") String name);
	
	@Query(value = "SELECT DISTINCT br.pk_id, br.br_name, br.br_distributor_brand_id, br.br_supplier_group_pkid,br.br_base_spirit_category,br.br_create_timestamp ,br.br_category ,br.br_create_user_pkid ,br.br_default_image ,"
			+ "br.br_mpc ,br.br_reviewed,br.br_update_timestamp,br.br_update_user_pkid,br.br_southern_brand_group_id,br.br_deleted "
			+ "FROM lp_brand br, lp_ingredient ing, lp_ingredientpricing pri "
			+ "WHERE ing.pk_id = pri.pr_ingredient_pkid AND br.pk_id = ing.in_brand_pkid AND "
			+ "ing.in_distributor_pkid = :distributorId AND pri.pr_sales_group_pkid IN :salesGroupPkIds AND ing.in_type NOT IN ('custom','housemade')",nativeQuery = true)
	public List<Brand> findBrandNameInCocktailPage(@Param("salesGroupPkIds") List<Integer> salesGroupPkIds, @Param("distributorId") Long distributorId);
	
	@Query(value = "SELECT * FROM lp_brand WHERE pk_id IN (SELECT in_brand_pkid FROM lp_ingredient WHERE in_type = 'housemade' ) ",nativeQuery = true)
	public List<Brand> findByhousemadeNativeQuery();
	public Page<Brand> findByDeletedNot(int deletedId, Pageable pageable);
	List<Brand> findByDeletedIs(int deletedId);
	
	@Query(value = 
			"SELECT DISTINCT" +
			" 	br.pk_id, br.br_name, br.br_distributor_brand_id, br.br_supplier_group_pkid,br.br_base_spirit_category,br.br_create_timestamp ,br.br_category ,br.br_create_user_pkid ,br.br_default_image ," + 
			"	br.br_mpc ,br.br_reviewed,br.br_update_timestamp,br.br_update_user_pkid,br.br_southern_brand_group_id,br.br_deleted " +
			"FROM " + 
			"	lp_brand br " + 
			"INNER JOIN lp_ingredient ing ON br.pk_id = ing.in_brand_pkid " + 
			"	AND ing.in_type NOT IN ('custom','housemade') " + 
			"ORDER BY " + 
			"	br.br_name " ,nativeQuery = true)
	public List<Brand> findBrandOrderByNameByAdminNativeQuery();
	
}
