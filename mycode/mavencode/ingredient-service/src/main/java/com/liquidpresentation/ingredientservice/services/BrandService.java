package com.liquidpresentation.ingredientservice.services;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.liquidpresentation.common.Category;
import com.liquidpresentation.common.utils.UserContextHolder;
import com.liquidpresentation.ingredientservice.model.Brand;
import com.liquidpresentation.ingredientservice.repository.BrandRepository;

@Service
public class BrandService {
	
	@Autowired
	private BrandRepository brandRepository;
	
	public Optional<Brand> findBrandByMpc(String mpc) {
		return brandRepository.findByBrMpc(mpc);
	}
	
	public Optional<Brand> findBrandByMpcOrName(String mpc, String name) {
		List<Brand> brands = brandRepository.findAllByBrMpcEqualsOrBrName(mpc, name);
		if (brands.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(brands.get(0));
	}
	
	public long saveBrand(Brand newBrand) {
		long userId = UserContextHolder.getContext().getUserId();
		newBrand.setBrCreateUserPkid(userId);
		newBrand.setBrUpdateUserPkid(userId);
		newBrand.setBrReviewed(false);
		return brandRepository.save(newBrand).getPkId();
	}
	
	public void updateBrand(Brand brand) {
		long userId = UserContextHolder.getContext().getUserId();
		brand.setBrUpdateUserPkid(userId);
		Timestamp ts = new Timestamp(new java.util.Date().getTime());
		brand.setBrUpdateTimestamp(ts);
		brandRepository.save(brand);
	}
	
	public void updateBrandByIngredient(Brand brand) {
		Optional<Brand> opt = this.brandRepository.findById(brand.getPkId());
		if (opt.isPresent()) {
			Brand persist = opt.get();
			persist.setBrName(brand.getBrName());
			brandRepository.save(persist);
		}
	}
	
	public void deleteBrand(long pkId) {
		Brand brand = new Brand();
		brand.setPkId(pkId);
		brandRepository.delete(brand);
	}
	
	/*public Page<Brand> findAllByReviewed(boolean brReviewed){
		return brandRepository.findByBrReviewed(brReviewed);
	}*/
	
	public List<Brand> lookupBrand(String category, String keyword){
		return brandRepository.findByBrReviewedAndBrBaseSpiritCategoryAndBrNameContaining(true, category, keyword);
	}

	public Brand findByDistributorBrandId(String prDistributorBrandId) {
		List<Brand> brands = brandRepository.findAllByDistributorBrandId(prDistributorBrandId);
		return brands.isEmpty() ? null : brands.get(0);
	}

	public Optional<Brand> findBrandByName(String name) {
		return brandRepository.findByBrNameIgnoreCase(name);
	}
	
	public List<Brand> findBrandByBrNameAndBrCategory(String brName,Category brCategory){
		return brandRepository.findByBrNameAndBrCategory(brName, brCategory);
	}

	public List<Brand> findAllByMpcStartingWithOrderByPkIdDesc(String mpc) {
		return brandRepository.findAllByBrMpcStartingWithOrderByPkIdDesc(mpc);
	}
}
