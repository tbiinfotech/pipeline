package com.liquidpresentation.ingredientservice.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.liquidpresentation.common.GroupType;
import com.liquidpresentation.ingredientservice.model.Group;
import com.liquidpresentation.ingredientservice.repository.GroupRepository;

@Service
public class GroupService {

	@Autowired
	private GroupRepository groupRepository;

	public boolean existsByIdAndType(Long pkId, GroupType type) {
		return groupRepository.existsByPkIdAndType(pkId, type);
	}

	public Optional<Group> findByIdAndType(Long pkId, GroupType type) {
		return groupRepository.findByPkIdAndType(pkId, type);
	}

	public boolean existsByDistributorPkId(Long distributorPkId) {
		return groupRepository.existsByDistributorPkId(distributorPkId);
	}

	public List<Group> findByDistributorIdAndState(Long distributorId, String state) {
		return groupRepository.findByDistributorPkIdAndState(distributorId, state);
	}
	
	public String getStateCode(Long pkId){
		Optional<Group> group = groupRepository.findById(pkId);
		if (group.isPresent()) {
			return group.get().getState().name();
		}
		return "";
	}
	
	public long findDistributorPkid(long salesGroupPkid) {
		long distributorPkid = 0;
		List<Group> results = this.groupRepository.findTopGroup(salesGroupPkid);
		if (!results.isEmpty()) {
			distributorPkid = results.get(0).getDistributorPkId();
		}
		return distributorPkid;
	}
}
