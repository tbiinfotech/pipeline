package com.liquidpresentaion.users.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.liquidpresentaion.users.model.UserColumn;

@Repository
public interface UserColumnRepository extends CrudRepository<UserColumn, Integer> {

	List<UserColumn> findByUserPkidOrderByColumnIndexAsc(int userId);

	void deleteByUserPkid(int userId);
}
