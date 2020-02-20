package com.liquidpresentaion.users.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.liquidpresentaion.users.model.GroupUser;
import com.liquidpresentaion.users.model.User;
import com.liquidpresentation.common.Role;


@Repository
public interface GroupUserRepository extends CrudRepository<GroupUser, Integer>{
	@Query(value= "SELECT * FROM lp_group_user WHERE gu_user_pkid = :userId",nativeQuery = true)
	List<GroupUser> findByUserIdNaiveQuery(@Param("userId") Long userId);
	Boolean existsByUserAndRole(User user,Role role);
	@Query(value= "SELECT * FROM lp_group_user WHERE gu_group_pkid IN :groupPkIds ",nativeQuery = true)
	List<GroupUser> findByGroupPkIdsIdNaiveQuery(@Param("groupPkIds") List<Integer> groupPkIds);
	@Query(value = "SELECT * FROM lp_group_user WHERE gu_group_pkid = :groupPkId ",nativeQuery = true)
	List<GroupUser> findByGroupPkIdsIdNaiveQuery(@Param("groupPkId") Integer groupPkId);
	List<GroupUser> findByGroupPkId(@Param("GroupPkIds") Integer groupId);
	@Query(value= "SELECT * FROM lp_group_user WHERE gu_user_pkid = :userId AND gu_group_pkid = :GroupPkIds",nativeQuery = true)
	List<GroupUser> findByUserIdAndGroupPkIdNaiveQuery(@Param("userId") Long userId,@Param("GroupPkIds") Integer groupId);
}
