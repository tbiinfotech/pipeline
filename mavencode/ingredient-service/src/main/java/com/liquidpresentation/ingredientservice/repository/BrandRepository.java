package com.liquidpresentation.ingredientservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.liquidpresentation.common.Category;
import com.liquidpresentation.ingredientservice.model.Brand;

@Repository
public interface BrandRepository extends PagingAndSortingRepository<Brand, Long> {
	public Optional<Brand> findByBrMpc(String brMpc);
	//public Page<Brand> findByBrReviewed(boolean brReviewed);
	public List<Brand> findByBrReviewedAndBrBaseSpiritCategoryAndBrNameContaining(boolean brReviewed, String brCategory, String brName);
	public List<Brand> findAllByDistributorBrandId(String prDistributorBrandId);
	
	@Query("select b from Brand b where b.brMpc = :mpc or b.brName = :name")
	public List<Brand> findAllByBrMpcEqualsOrBrName(@Param("mpc") String mpc, @Param("name") String name);
	public Optional<Brand> findByBrNameIgnoreCase(String name);
	public List<Brand> findByBrNameAndBrCategory(String brName, Category brCategory);
	public List<Brand> findByBrNameIgnoreCaseContaining(String brName);
	Boolean existsByBrName(String brName);
	Boolean existsByBrNameAndBrCategory(String brName, Category brCategory);
	Brand findByPkId(Long PkId);
	public List<Brand> findAllByBrMpcStartingWithOrderByPkIdDesc(String mpc);
	
	@Query(value = "select * from lp_brand where br_category != '' AND br_deleted = 0 ", nativeQuery=true)
	public List<Brand> findByBrands();
}