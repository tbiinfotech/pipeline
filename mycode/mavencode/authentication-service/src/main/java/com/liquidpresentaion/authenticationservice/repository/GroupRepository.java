package com.liquidpresentaion.authenticationservice.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.liquidpresentaion.authenticationservice.model.Group;

@Repository
public interface GroupRepository extends PagingAndSortingRepository<Group, Integer> {
//	Group findByPkId(int groupPkId);
}
