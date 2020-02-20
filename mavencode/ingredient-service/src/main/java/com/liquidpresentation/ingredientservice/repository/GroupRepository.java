package com.liquidpresentation.ingredientservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.liquidpresentation.common.GroupType;
import com.liquidpresentation.ingredientservice.model.Group;

public interface GroupRepository extends CrudRepository<Group, Long> {

	boolean existsByDistributorPkId(Long distributorPkId);
	boolean existsByPkIdAndType(Long pkId, GroupType type);
	Optional<Group> findByPkIdAndType(Long pkId, GroupType type);
	
	
	@Query(value = "select * from lp_group g1 join ( "
			+ "WITH RECURSIVE r AS (  "
			+ "SELECT gr.pk_id FROM lp_group gr where gr.g_distributor_pkid = :distributorId "
			+ "UNION ALL "
			+ "SELECT lp_group.pk_id	FROM lp_group,r WHERE lp_group.g_parent_pkid = r.pk_id  "
			+ ") SELECT distinct pk_id FROM	r ) g2 on g1.pk_id = g2.pk_id and g1.g_state = :uspsState ",
	nativeQuery = true)
	List<Group> findByDistributorPkIdAndState(@Param(value = "distributorId") Long distributorId, @Param(value = "uspsState") String uspsState);
	
	public List<Group> findByNameIgnoreCaseContaining(String name);
	
	@Query(value = "SELECT * FROM lp_group WHERE pk_id IN (SELECT gu_group_pkid FROM lp_group_user WHERE gu_user_pkid = :userId ) ",
			nativeQuery = true)
	List<Group> findSupplierGroupByUserIdNativeQuery(@Param(value = "userId") long userId);
	
	@Query(value = "SELECT * FROM lp_group WHERE g_distributor_pkid is not null and pk_id IN ( " + 
			"WITH RECURSIVE r AS ( " + 
			"SELECT pk_id, g_parent_pkid FROM lp_group WHERE pk_id = :salesGroupId " + 
			"UNION ALL " + 
			"SELECT lp_group.pk_id, lp_group.g_parent_pkid FROM lp_group,r WHERE lp_group.pk_id = r.g_parent_pkid) " + 
			"SELECT pk_id FROM	r)",nativeQuery = true)
	List<Group> findTopGroup(@Param(value = "salesGroupId") long salesGroupId);
	
	@Query(value = "SELECT * FROM lp_group WHERE pk_id IN ( " +
			"WITH RECURSIVE r AS ( " + 
			"SELECT pk_id, g_parent_pkid FROM lp_group WHERE pk_id IN :salesGroupIds " + 
			"UNION ALL " + 
			"SELECT lp_group.pk_id, lp_group.g_parent_pkid FROM lp_group,r WHERE lp_group.pk_id = r.g_parent_pkid) " + 
			"SELECT pk_id FROM	r)",nativeQuery = true)
	List<Group> findTopGroups(@Param(value = "salesGroupIds") List<Long> salesGroupIds);
	
	@Query(value = "SELECT * FROM lp_group WHERE pk_id IN ( " + 
			"WITH RECURSIVE r AS ( " + 
			"SELECT pk_id, g_parent_pkid FROM lp_group WHERE pk_id = :salesGroupId " + 
			"UNION ALL " + 
			"SELECT lp_group.pk_id, lp_group.g_parent_pkid FROM lp_group,r WHERE lp_group.pk_id = r.g_parent_pkid) " + 
			"SELECT pk_id FROM	r)",nativeQuery = true)
	List<Group> findTopGroupNoDistributor(@Param(value = "salesGroupId") long salesGroupId);
}