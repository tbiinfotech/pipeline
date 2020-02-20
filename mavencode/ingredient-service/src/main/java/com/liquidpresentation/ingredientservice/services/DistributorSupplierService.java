package com.liquidpresentation.ingredientservice.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.liquidpresentation.ingredientservice.model.DistributorSupplier;
import com.liquidpresentation.ingredientservice.model.Price;
import com.liquidpresentation.ingredientservice.repository.DistributorSupplierRepository;

@Service
public class DistributorSupplierService {
	
	@Autowired
	private DistributorSupplierRepository distributorSupplierRepository;
	
	public boolean existsByDistributorPkidAndDistSupplierId(Price price) {
		List<DistributorSupplier> distSuppliers = distributorSupplierRepository.findByDistributorPkidAndDistSupplierId(price.getPrDistributorPkid(), price.getDistSupplierId());
		if (!distSuppliers.isEmpty()) {
			price.setPrSupplierGroupPkid(distSuppliers.get(0).getSupplierGroupPkid());//find mapping supplier group by distPkid and distSupplierId
		}
		return !distSuppliers.isEmpty();
	}
	
	public boolean existsByDistributorPkidAndDistSupplierId(Long prDistributorPkid, String distSupplierId) {
		List<DistributorSupplier> distSuppliers = distributorSupplierRepository.findByDistributorPkidAndDistSupplierId(prDistributorPkid, distSupplierId);
		
		return !distSuppliers.isEmpty();
	}
}
