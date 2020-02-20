package com.liquidpresentaion.users.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.liquidpresentaion.users.model.Group;
import com.liquidpresentation.common.GroupType;

@Repository
public interface GroupRepository extends PagingAndSortingRepository<Group, Integer> {

	List<Group> findByTypeOrderByNameAsc(GroupType type);
	List<Group> findByStateNotNull();
	List<Group> findByPkIdIn(List<Integer> groupPkIds);
	List<Group> findByPkIdInAndTypeOrderByNameAsc(List<Integer> groupPkIds,GroupType type);
	List<Group> findByTypeAndDistributorIdIsNull(GroupType type);
	List<Group> findByTypeAndGroupUsersUserPkIdOrderByNameAsc(GroupType type, int userPkid);
	Boolean existsByName(String name);
	
	
	@Query(value = "select * from lp_group where pk_id in ("
			+ "WITH RECURSIVE r AS ( "
			+ "SELECT gr.pk_id, gr.g_name, gr.g_parent_pkid, gr.g_state, gr.g_state_numberofusers FROM lp_group gr where gr.pk_id = :groupId "
			+ "UNION ALL  "
			+ "SELECT gr.pk_id, gr.g_name, gr.g_parent_pkid, gr.g_state, gr.g_state_numberofusers FROM lp_group gr,r WHERE gr.pk_id = r.g_parent_pkid  "
			+ ") SELECT distinct pk_id FROM	r where g_state <> 'NONE') ",
	nativeQuery = true)
	List<Group> findStateGroup(@Param(value = "groupId")int groupId);
	
	
	@Query(value = "select count(1) numberOfUsers from lp_group_user where gu_group_pkid in ( "
			+ "WITH RECURSIVE r AS (  "
			+ "SELECT gr.pk_id FROM lp_group gr where gr.pk_id = :groupPkId  "
			+ "UNION ALL  "
			+ "SELECT lp_group.pk_id	FROM lp_group,r WHERE lp_group.g_parent_pkid = r.pk_id   "
			+ ") SELECT distinct pk_id FROM	r "
			+ ") ",
	nativeQuery = true)
	int countNumberOfUsersByStateGroupId(@Param(value = "groupPkId")int groupPkId);
	List<Group> findByNameIgnoreCaseContaining(String name);
	List<Group> findByNameIgnoreCase(String name);
	@Query(value = "select distinct ss.gss_supplier_group_pkid from lp_group_sales_supplier ss, "
			+ "(WITH RECURSIVE r AS (  "
			+ "SELECT gr.pk_id FROM lp_group gr join lp_group_user gu on gu.gu_user_pkid = :userPkid and gr.pk_id = gu.gu_group_pkid  "
			+ "UNION ALL  "
			+ "SELECT lp_group.pk_id	FROM lp_group,r WHERE lp_group.g_parent_pkid = r.pk_id "
			+ ") SELECT distinct pk_id FROM	r "
			+ ") sales "
			+ "where ss.gss_sales_group_pkid = sales.pk_id  ",
	nativeQuery = true)
	List<Integer> findSupplierGroupsByUserPkid(@Param(value = "userPkid")int userPkid);
	
	
	@Query(value = "SELECT * FROM lp_group WHERE pk_id IN (" + 
			"WITH RECURSIVE r AS ( " + 
			"SELECT pk_id FROM lp_group WHERE pk_id = :salesGroupId " + 
			"UNION ALL " + 
			"SELECT lp_group.pk_id	FROM lp_group,r WHERE lp_group.g_parent_pkid = r.pk_id) " + 
			"SELECT pk_id FROM	r)",nativeQuery = true)
	List<Group> findGroupNativeQuery(@Param(value = "salesGroupId") Integer salesGroupId);
	
	@Query(value = "SELECT * FROM lp_group WHERE g_distributor_pkid is not null and pk_id IN ( " + 
			"WITH RECURSIVE r AS ( " + 
			"SELECT pk_id, g_parent_pkid FROM lp_group WHERE pk_id = :salesGroupId " + 
			"UNION ALL " + 
			"SELECT lp_group.pk_id, lp_group.g_parent_pkid FROM lp_group,r WHERE lp_group.pk_id = r.g_parent_pkid) " + 
			"SELECT pk_id FROM	r)",nativeQuery = true)
	List<Group> findTopGroup(@Param(value = "salesGroupId") Integer salesGroupId);

	@Query(value = "SELECT * FROM lp_group WHERE pk_id IN ( " +
			"WITH RECURSIVE r AS ( " +
			"SELECT pk_id, g_parent_pkid FROM lp_group WHERE pk_id IN :salesGroupIds " +
			"UNION ALL " +
			"SELECT lp_group.pk_id, lp_group.g_parent_pkid FROM lp_group,r WHERE lp_group.pk_id = r.g_parent_pkid) " +
			"SELECT pk_id FROM	r)",nativeQuery = true)
	List<Group> findTopGroupsNativeQuery(@Param(value = "salesGroupIds") List<Integer> salesGroupIds);
}
