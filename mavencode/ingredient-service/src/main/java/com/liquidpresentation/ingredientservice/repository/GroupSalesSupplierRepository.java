package com.liquidpresentation.ingredientservice.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.liquidpresentation.ingredientservice.model.GroupSalesSupplier;

@Repository
public interface GroupSalesSupplierRepository extends CrudRepository<GroupSalesSupplier,Long>{
	List<GroupSalesSupplier> findBysalesGroupIdIn(List<Long> ids);
}
