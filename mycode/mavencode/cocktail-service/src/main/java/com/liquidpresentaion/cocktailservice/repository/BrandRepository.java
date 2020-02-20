package com.liquidpresentaion.cocktailservice.repository;




import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.liquidpresentaion.cocktailservice.model.Brand;


@Repository
public interface BrandRepository extends CrudRepository<Brand, Integer> {
	List<Brand> findByDeletedIs(int deletedId);
}

