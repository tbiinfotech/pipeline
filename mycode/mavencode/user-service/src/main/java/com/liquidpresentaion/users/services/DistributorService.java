package com.liquidpresentaion.users.services;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.liquidpresentaion.users.model.Distributor;
import com.liquidpresentaion.users.model.DistributorCategory;
import com.liquidpresentaion.users.model.Group;
import com.liquidpresentaion.users.model.SupplierDistributor;
import com.liquidpresentaion.users.repository.DistributorRepository;
import com.liquidpresentaion.users.repository.GroupRepository;
import com.liquidpresentaion.users.repository.SupplierDistributorRepository;

@Service
public class DistributorService {

	@Autowired
	private DistributorRepository distributorRepository;
	
	@Autowired
	private GroupRepository groupRepository;
	
	@PersistenceContext
	private EntityManager entityManger;
	
	@Autowired
	private SupplierDistributorRepository supplierDistributorRepository;
	
	public Page<Distributor> findAll(PageRequest pageRequet){
		return distributorRepository.findAll(pageRequet);
	}

	public Page<Distributor> findByNameContainingIgnoreCase(String name, PageRequest pageRequet){
		return distributorRepository.findByNameContainingIgnoreCase(name, pageRequet);
	}
	
	public void saveDistributor(Distributor newDistributor){
		distributorRepository.save(newDistributor);
	}
	
	public void updateDistributor(Distributor distributor){
		Distributor load = distributorRepository.findById(distributor.getId()).get();
		load.setName(distributor.getName());
		distributorRepository.save(load);
	}
	
	@Transactional
	public void deleteDistributor(Distributor distributor){
		distributorRepository.delete(distributor);
		//add fuyu 20180614 fix bug Distributors-2 Distributor Remove后,[lp_group]表中的[g_distributor_pkid]没有被清空 start
		entityManger.createNativeQuery("UPDATE LP_GROUP SET G_DISTRIBUTOR_PKID = NULL WHERE G_DISTRIBUTOR_PKID =  " + distributor.getId()).executeUpdate();
		//add end
	}
	
	public Distributor getDistributor(int distributorId){
		return distributorRepository.findById(distributorId).get();
	}

	public void addDistributorCategory(int distributorId, DistributorCategory distributorCategory) {
		distributorCategory.setDistributorCategory(distributorCategory.getDistributorCategory().toUpperCase());
		Distributor distributor = distributorRepository.findById(distributorId).get();
		for (DistributorCategory dc : distributor.getCategorySet()) {
			if (dc.getDistributorCategory().equals(distributorCategory.getDistributorCategory()) 
					&& dc.getIngredientCategory().equals(distributorCategory.getIngredientCategory())
					&& dc.getBaseSpiritCategory().equals(distributorCategory.getBaseSpiritCategory())) {
				return;
			}
		}
		distributor.getCategorySet().add(distributorCategory);
		distributorRepository.save(distributor);
	}

	public void removeDistributorCategory(int distributorId, DistributorCategory distributorCategory) {
		Distributor distributor = distributorRepository.findById(distributorId).get();
		for (Iterator<DistributorCategory> iterator = distributor.getCategorySet().iterator(); iterator.hasNext();) {
			if (distributorCategory.getId() == iterator.next().getId()) {
				iterator.remove();
			}
		}
		distributorRepository.save(distributor);
	}

	public void addTopGroup(int distributorId, Group salesGroup) {
		Group save = groupRepository.findById(salesGroup.getPkId()).get();
		if (save.getDistributor() == null) {
			Distributor d = new Distributor();
			d.setId(distributorId);
			save.setDistributor(d);
		} else {
			save.getDistributor().setId(distributorId);
		}
		groupRepository.save(save);
	}

	public void removeTopGroup(int distributorId, Group salesGroup) {
		Group save = groupRepository.findById(salesGroup.getPkId()).get();
		save.setDistributor(null);
		groupRepository.save(save);
	}
	
	public void addDistributorSupplier(int distributorId, SupplierDistributor supplierDistributor) {
		supplierDistributor.setDistributorPkId(distributorId);
		supplierDistributorRepository.save(supplierDistributor);
	}

	public void removeDistributorSupplier(Integer pkId) {
		supplierDistributorRepository.deleteById(pkId);
	}

	public List<SupplierDistributor> getDistributorSupplier(int distributorId) {
		return supplierDistributorRepository.findByDistributorPkId(distributorId);
	}
	
	public SupplierDistributor getDistributorPkId(int distributorId) {
		return supplierDistributorRepository.findByPkId(distributorId);
	}
	
	public Integer existsByDistributorPkIdAndDistSupplierId(Integer distributorId,String distributorSupplierId) {
		Integer flag = 0;
		boolean result = supplierDistributorRepository.existsByDistributorPkIdAndDistSupplierId(distributorId,distributorSupplierId);
		if (result) {
			return flag = 1;
		}
		return flag;
	}
}
