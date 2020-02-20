package com.liquidpresentaion.cocktailservice.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.liquidpresentaion.cocktailservice.model.User;
import com.liquidpresentation.common.Role;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Integer> {
	public Page<User> findByEmailIgnoreCaseContainingOrFirstNameIgnoreCaseContainingOrLastNameIgnoreCaseContaining(String email, 
			String firstName, String lastName, Pageable pageable);
	public List<User> findByEmailIgnoreCaseEndingWith(String domain);
	public List<User> findDistinctByUserGroupsRole(Role role);
}
