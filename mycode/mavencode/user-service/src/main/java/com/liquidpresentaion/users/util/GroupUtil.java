package com.liquidpresentaion.users.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.liquidpresentaion.users.model.Group;

public final class GroupUtil {

	/**
	 * 非admin用户，可能属于不同层级的组；
	 * 取目录树的时候，只保留最高层级的组；
	 * 
	 * @param assignedGroupList
	 * @return
	 */
	public static List<Group> retainAssignedTopGroups(List<Group> assignedGroupList){
		List<Group> assignedTopGroups = new ArrayList<>();
		Group groupI;
		for (int i = 0; i < assignedGroupList.size(); i++) {
			groupI = assignedGroupList.get(i);
			boolean hasParent = false;
			for (int j = 0; j < assignedGroupList.size(); j++) {
				if (assignedGroupList.get(j).parentOf(groupI)) {
					hasParent = true;
				}
			}
			if (!hasParent) {
				assignedTopGroups.add(groupI);
			}
		}
		return assignedTopGroups;
	}
	

	public static Optional<Group> findSelectedGroupFromTree(int selectedSalesGroupId, Group group) {
		if (selectedSalesGroupId == group.getPkId()) {
			return Optional.of(group);
		} else if (group.hasSubGroups()) {
			for (Group subGroup : group.getSubGroups()) {
				Optional<Group> ret = findSelectedGroupFromTree(selectedSalesGroupId, subGroup);
				if (ret.isPresent()) {
					return ret;
				}
			}
		}
		return Optional.empty();
	}

	public static void loadSubGroupIdList(List<Integer> subGroupIdList, List<Group> subGroupList) {
		for (Group sub : subGroupList) {
			subGroupIdList.add(sub.getPkId());
			if (sub.hasSubGroups()) {
				loadSubGroupIdList(subGroupIdList, sub.getSubGroups());
			}
		}
	}
}
