package com.liquidpresentaion.cocktailservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.liquidpresentaion.cocktailservice.model.CocktailGroup;

@Repository
public interface CocktailGroupRepository extends PagingAndSortingRepository<CocktailGroup, Integer>{
	List<CocktailGroup> findByGroupIdIn(List<Integer> groupIds);
	
	List<CocktailGroup> findByCocktailIdEquals(Integer cocktailId);
	
	@Query(value = "select distinct ctg_cocktail_pkid from lp_cocktail_group cg join ( "
				+ "WITH RECURSIVE r AS (  "
				+ "SELECT gr.pk_id, gr.g_parent_pkid FROM lp_group gr where gr.pk_id = :groupPkid "
				+ "UNION ALL "
				+ "SELECT lp_group.pk_id, lp_group.g_parent_pkid	FROM lp_group,r WHERE lp_group.pk_id = r.g_parent_pkid "
				+ ") SELECT distinct pk_id FROM	r "
			+ ") gr on cg.ctg_group_pkid = gr.pk_id ",
	nativeQuery = true)
	List<Integer> findCocktailsIdListGroupPkid(@Param(value = "groupPkid")Integer groupPkid);
	
	Boolean existsByCocktailIdAndGroupId(Integer cocktailId, Integer groupId);
	
	void deleteByCocktailId(int pkId);
}

