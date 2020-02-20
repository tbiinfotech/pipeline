package com.liquidpresentaion.cocktailservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.liquidpresentaion.cocktailservice.model.Group;
import com.liquidpresentaion.cocktailservice.model.GroupUser;

@Repository
public interface GroupUserRepository extends CrudRepository<GroupUser, Integer>{
	List<GroupUser> findByGroupIn(List<Group> groups);
	@Query(value= "SELECT * FROM lp_group_user WHERE gu_user_pkid = :userId",nativeQuery = true)
	List<GroupUser> findByUserIdNaiveQuery(@Param("userId") Long userId);
}
