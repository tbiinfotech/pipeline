package com.liquidpresentaion.cocktailservice.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.liquidpresentaion.cocktailservice.model.Group;


@Repository
public interface GroupRepository extends CrudRepository<Group, Integer> {
	@Query(value = "SELECT * FROM lp_group WHERE pk_id IN (" + 
			"WITH RECURSIVE r AS ( " + 
			"SELECT pk_id FROM lp_group WHERE pk_id = :salesGroupId " + 
			"UNION ALL " + 
			"SELECT lp_group.pk_id	FROM lp_group,r WHERE lp_group.g_parent_pkid = r.pk_id) " + 
			"SELECT pk_id FROM	r)",nativeQuery = true)
	List<Group> findGroupNativeQuery(@Param(value = "salesGroupId") Integer salesGroupId);
	
	@Query(value = "WITH RECURSIVE r AS (  " + 
			"SELECT pk_id, g_parent_pkid FROM lp_group WHERE pk_id IN :salesGroupIds  " + 
			"UNION ALL " + 
			"SELECT lp_group.pk_id, lp_group.g_parent_pkid	FROM lp_group,r WHERE lp_group.pk_id = r.g_parent_pkid)  " + 
			"SELECT pk_id FROM	r",nativeQuery = true)
	List<BigInteger> findUpperSalesGroupsNativeQuery(@Param(value = "salesGroupIds") List<Integer> salesGroupIds);}
