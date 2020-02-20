package com.liquidpresentation.ingredientservice.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.liquidpresentation.ingredientservice.model.DistributorSupplier;

@Repository
public interface DistributorSupplierRepository extends CrudRepository<DistributorSupplier, Long> {
	public boolean existsByDistributorPkidAndDistSupplierId(long distributorPkid, String distSupplierId);
	public List<DistributorSupplier> findByDistributorPkidAndDistSupplierId(long distributorPkid, String distSupplierId);
}