package com.liquidpresentaion.users.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.liquidpresentaion.users.exceptions.DuplicateEntityException;
import com.liquidpresentaion.users.model.Banner;
import com.liquidpresentaion.users.repository.BannerRepository;

@Service
public class BannerService {

	@Autowired
	private BannerRepository bannerRepository;

	public Page<Banner> findAll(PageRequest pageRequet) {
		return bannerRepository.findAll(pageRequet);
	}

	public Page<Banner> findByNameIgnoreCaseContaining(String name, PageRequest pageRequet) {
		return bannerRepository.findByNameIgnoreCaseContaining(name, pageRequet);
	}

	public void saveBanner(Banner newBanner) {
		this.validateDuplicate(newBanner.getName());
		bannerRepository.save(newBanner);
	}

	public void updateBanner(Banner banner) {
		this.validateDuplicate(banner.getName(), banner.getId());
		bannerRepository.save(banner);
	}

	public void deleteBanner(Banner banner) {
		banner.setGroup(null);
		bannerRepository.delete(banner);
	}

	public Banner getBanner(int bannerId) {
		return bannerRepository.findById(bannerId).get();
	}

	private void validateDuplicate(String name) {
		if (bannerRepository.existsByName(name)) {
			throw new DuplicateEntityException("There already is a banner named '" + name + "'!");
		}
	}

	// add fuyu 20180621 修改时增加IdIsNot条件
	private void validateDuplicate(String name, int id) {
		if (bannerRepository.existsByNameAndIdIsNot(name, id)) {
			throw new DuplicateEntityException("There already is a banner named '" + name + "'!");
		}
	}
}
