package com.liquidpresentaion.users.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.liquidpresentaion.users.model.Banner;

@Repository
public interface BannerRepository extends PagingAndSortingRepository<Banner, Integer> {
	public Page<Banner> findByNameIgnoreCaseContaining(String name, Pageable pageable);

	public boolean existsByName(String name);

	// add fuyu 20180621 修改时增加IdIsNot条件
	public boolean existsByNameAndIdIsNot(String name, int id);
}
