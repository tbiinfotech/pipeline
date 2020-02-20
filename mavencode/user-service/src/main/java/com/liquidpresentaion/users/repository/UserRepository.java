package com.liquidpresentaion.users.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.liquidpresentaion.users.model.User;
import com.liquidpresentation.common.Role;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Integer> {
	public Page<User> findByPkIdNotAndEmailIgnoreCaseContainingOrFirstNameIgnoreCaseContainingOrLastNameIgnoreCaseContaining(Integer userId,String email, 
			String firstName, String lastName, Pageable pageable);
	public List<User> findByEmailIgnoreCaseEndingWith(String domain);
	public List<User> findDistinctByUserGroupsRole(Role role);
	public List<User> findByEmailIgnoreCase(String email);
	public Page<User> findByPkIdIn(List<Integer> userIds,Pageable pageable);
	public Page<User> findByPkIdInAndEmailIgnoreCaseContainingOrFirstNameIgnoreCaseContainingOrLastNameIgnoreCaseContaining(List<Integer> userIds,String keyword,String firstName,String lastName,Pageable pageable);
	
	public Page<User> findByPkIdNot(@Param(value = "userId") Integer userId,Pageable pageable);
}
