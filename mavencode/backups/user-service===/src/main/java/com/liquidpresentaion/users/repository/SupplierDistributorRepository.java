package com.liquidpresentaion.users.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.liquidpresentaion.users.model.SupplierDistributor;

@Repository
public interface SupplierDistributorRepository extends PagingAndSortingRepository<SupplierDistributor, Integer> {
	List<SupplierDistributor> findByDistributorPkId(Integer distributorPkId);
	SupplierDistributor findByPkId(Integer distributorPkId);
	boolean existsByDistributorPkIdAndDistSupplierId(Integer distributorPkId,String distributorSupplierId);
} 
