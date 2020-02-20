package com.liquidpresentation.ingredientservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.liquidpresentation.common.Role;
import com.liquidpresentation.ingredientservice.model.GroupUser;
import com.liquidpresentation.ingredientservice.model.Ingredient;
import com.liquidpresentation.ingredientservice.model.User;

public interface GroupUserRepository extends PagingAndSortingRepository<GroupUser,Integer>{
	List<GroupUser> findByUser(User user);
	@Query(value= "SELECT * FROM lp_group_user WHERE gu_user_pkid = :userId",nativeQuery = true)
	List<GroupUser> findByUserIdNaiveQuery(@Param("userId") Long userId);
	Boolean existsByUserAndRole(User user,Role role);
	
}
