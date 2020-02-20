package com.liquidpresentaion.users.services;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.liquidpresentaion.users.context.UserContextHolder;
import com.liquidpresentaion.users.exceptions.DuplicateEntityException;
import com.liquidpresentaion.users.exceptions.MaxNumberOfUsersExceededException;
import com.liquidpresentaion.users.model.Cocktail;
import com.liquidpresentaion.users.model.CocktailGroup;
import com.liquidpresentaion.users.model.Group;
import com.liquidpresentaion.users.model.GroupUser;
import com.liquidpresentaion.users.model.User;
import com.liquidpresentaion.users.repository.CocktailGroupRepository;
import com.liquidpresentaion.users.repository.CocktailRepository;
import com.liquidpresentaion.users.repository.GroupRepository;
import com.liquidpresentaion.users.repository.GroupUserRepository;
import com.liquidpresentaion.users.repository.UserRepository;
import com.liquidpresentaion.users.util.GroupUtil;
import com.liquidpresentation.common.GroupType;
import com.liquidpresentation.common.Role;
import com.liquidpresentation.common.UspsState;

@Service
public class GroupService {

	@Autowired
	private GroupRepository groupRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CocktailRepository cocktailRepository;
	
	@Autowired
	private CocktailGroupRepository cocktailGroupRepository;
	
	@Autowired
	private GroupUserRepository groupUserRepository;
	
	@PersistenceContext
	private EntityManager entityManger;
	
	public Page<Group> findAll(int page, int size){
		return groupRepository.findAll(PageRequest.of(page, size));
	}
	
	public Group saveGroup(Group newGroup){
		valiateGroup(newGroup);
		Group group = groupRepository.save(newGroup);
		return group;
	}
	
	public void updateGroupIncludeLibrary(int groupId) {
		Optional<Group> opt = groupRepository.findById(groupId);
		if (opt.isPresent()) {
			Group persist = opt.get();
			persist.setIncludeDefaultCocktails(!persist.isIncludeDefaultCocktails());
			groupRepository.save(persist);
		}
	}
	
	public int findByUserRole(Long userId,Boolean isMixologist) {
		List<Integer> groupPkIds = new ArrayList<>();
		List<Group> groups = new ArrayList<>();
		if (isMixologist) {
			List<GroupUser> groupUsers = groupUserRepository.findByUserIdNaiveQuery(userId);
			if (groupUsers != null && groupUsers.size() > 0) {
					groupUsers.forEach(g->{
					groupPkIds.add(g.getGroup().getPkId());
				});
			}
			List<Group> grpupList = groupRepository.findByPkIdIn(groupPkIds);
			if (grpupList.size() > 0) {
					grpupList.forEach(g->{
							if (g.getType().equals(GroupType.supplier)) {
								groups.add(g);
							}
					});
			}
		}
		if (groups.size() > 0) {
			return 1;
		} else {
			return 0;
		}
	}
	
	public Group findBySupplierGroupOne() {
		long userId = UserContextHolder.getContext().getUserId();
		List<GroupUser> groupUsers = groupUserRepository.findByUserIdNaiveQuery(userId);
		List<Integer> groupPkIds = new ArrayList<>();
		if (groupUsers != null && groupUsers.size() > 0) {
			groupUsers.forEach(g->{
				groupPkIds.add(g.getGroup().getPkId());
			});
		}
		List<Group> grpupList = groupRepository.findByPkIdIn(groupPkIds);
		if (grpupList.size() > 0 && grpupList.size() < 2) {
			if (grpupList.get(0).getType().equals(GroupType.supplier) || grpupList.get(0).getType().equals(GroupType.sales)) {
				return grpupList.get(0);
			}
		}
		return null;
	}
	
	public void updateGroup(Group group){
		if (!StringUtils.isEmpty(group.getName())) {
			valiateGroup(group);
		}
		String name = group.getName();
		UspsState state = group.getState();
		int stateNumberOfUsers = group.getStateNumberOfUsers();
//		boolean includeDefault = group.isIncludeDefaultCocktails();
		int cocktailsLimit = group.getCocktailsLimit();
		String titleImage = group.getTitleImage();
		
		Optional<Group> opt = groupRepository.findById(group.getPkId());
		if (opt.isPresent()) {
			Group persist = opt.get();
			if (state != null && !"".equals(state)) {
				persist.setState(state);
			}
			if (name != null && !"".equals(name)) {
				persist.setName(name);
			}
			if (stateNumberOfUsers != 0) {
				persist.setStateNumberOfUsers(stateNumberOfUsers);
			}
			if (cocktailsLimit != 0 ) {
				persist.setCocktailsLimit(cocktailsLimit);
			}
			if (titleImage != null && !"".equals(titleImage)) {
				persist.setTitleImage(titleImage);
			}
			groupRepository.save(persist);
		}
	}
	
	@Transactional
	public void deleteGroup(Group group){
		//add fuyu fix bug 删除group, 关联的banner一起删除掉 start
		entityManger.createNativeQuery("DELETE FROM LP_BANNER  WHERE B_GROUP_PKID IN ( " + 
				"				WITH RECURSIVE R AS ( " + 
				"					SELECT PK_ID FROM LP_GROUP WHERE PK_ID = " + group.getPkId() +
				"					UNION ALL  " + 
				"					SELECT LP_GROUP.PK_ID	FROM LP_GROUP,R WHERE LP_GROUP.G_PARENT_PKID = R.PK_ID  " + 
				"				) SELECT PK_ID FROM	R)  ").executeUpdate();
		//add end
		
		// 删除lp_group_user对应数据
		entityManger.createNativeQuery("DELETE FROM lp_group_user  WHERE gu_group_pkid in ( " + 
				"				WITH RECURSIVE r AS ( " + 
				"					SELECT pk_id FROM lp_group WHERE pk_id = " + group.getPkId() +
				"					UNION ALL  " + 
				"					SELECT lp_group.pk_id	FROM lp_group,r WHERE lp_group.g_parent_pkid = r.pk_id  " + 
				"				) SELECT pk_id FROM	r) ").executeUpdate();
		
		// 删除子group
		entityManger.createNativeQuery("DELETE FROM lp_group WHERE pk_id in ( " + 
				"				WITH RECURSIVE r AS ( " + 
				"					SELECT pk_id FROM lp_group WHERE pk_id =  " + group.getPkId() +
				"					UNION ALL  " + 
				"					SELECT lp_group.pk_id	FROM lp_group,r WHERE lp_group.g_parent_pkid = r.pk_id  " + 
				"				) SELECT pk_id FROM	r) ").executeUpdate();
		
	}
	
	public Group getGroup(int groupId){
		Group group = groupRepository.findById(groupId).get();
		List<GroupUser> groupUsers = groupUserRepository.findByGroupPkId(groupId);
		List<Integer> userIds = new ArrayList<>();
		groupUsers.forEach(gu->{
			userIds.add(gu.getUser().getPkId());
		});
		List<Cocktail> cocktails = cocktailRepository.findByCreatePkIdIn(userIds);
//		group.setCocktailsCount(cocktailRepository.countBySupplierPkId(groupId));
		group.setCocktailsCount(cocktails.size());
		List<Cocktail> coc = new ArrayList<>();
		cocktails.forEach(cocktail->{
			if (cocktail.isPublished()) {
				coc.add(cocktail);
			}
			
		});
//		group.setPublishedCount(cocktailRepository.countBySupplierPkIdAndPublished(groupId, true));
		group.setPublishedCount(coc.size());
		return group;
	}

	public List<Group> findByType(GroupType type) {
		if (UserContextHolder.getContext().isAdmin()) {
			if(GroupType.all.equals(type)) {
				return (List<Group>) groupRepository.findAll(Sort.by(Direction.ASC, "name"));
			} else {
				return groupRepository.findByTypeOrderByNameAsc(type);
			}
		}
		Long userId = UserContextHolder.getContext().getUserId();
		List<GroupUser> groupUsers = groupUserRepository.findByUserIdNaiveQuery(userId);
		List<Integer> groupIds = new ArrayList<>();
		List<Integer> userIds = new ArrayList<>();
		List<Group> groups = new ArrayList<>();
		List<Group> results = new ArrayList<>();
		groupUsers.forEach(gu->{
			groups.addAll(groupRepository.findGroupNativeQuery(gu.getGroup().getPkId()));
		});
		if (groups.size() > 0) {
			if (GroupType.all.equals(type)) {
				return groups;
			}
			groups.forEach(g->{
				groupIds.add(g.getPkId());
			});
			results = groupRepository.findByPkIdInAndTypeOrderByNameAsc(groupIds, type);
		}
		return results;
	}
	public List<Group> findTreeByType(GroupType type,String fromPage) {
		boolean currentUserIsAdmin = UserContextHolder.getContext().isAdmin();
		if (!currentUserIsAdmin) {
			return findTreeByUserAssignedGroup(type, UserContextHolder.getContext().getUserId());
		}
		
		List<Group> groupList = groupRepository.findByTypeOrderByNameAsc(type);
		
		if (GroupType.sales.equals(type)) {
			List<Group> topGroupList = new LinkedList<>();
			for (Group parentGroup : groupList) {
				for (Group subGroup : groupList) {
					if (subGroup.getParentPkId() == parentGroup.getPkId()) {
						parentGroup.addSubGroup(subGroup);
					}
				}
				
				if (parentGroup.getParentPkId() == 0) {
					if ("cocktailStatistics".equals(fromPage)) {
						if (parentGroup.getDistributor() != null) {
							topGroupList.add(parentGroup);
						}
					} else {
						topGroupList.add(parentGroup);
					}
				}
			}
			
			groupList = topGroupList;
		}
		
		return groupList;
	}

	/**
	 * 取得当前用户所属的组，及其子组
	 * 
	 * @param type
	 * @return
	 */
	private List<Group> findTreeByUserAssignedGroup(GroupType type, long userPkid) {
		List<Integer> assignedGroupIds = groupRepository.findByTypeAndGroupUsersUserPkIdOrderByNameAsc(type, Integer.valueOf("" + userPkid)).stream().map(Group::getPkId).collect(Collectors.toList());
		List<Group> groupList = groupRepository.findByTypeOrderByNameAsc(type);
		
		if (GroupType.sales.equals(type)) {
			List<Group> assignedGroupList = new LinkedList<>();
			for (Group parentGroup : groupList) {
				for (Group subGroup : groupList) {
					if (subGroup.getParentPkId() == parentGroup.getPkId()) {
						parentGroup.addSubGroup(subGroup);
					}
				}
				List<GroupUser> groupUsers = groupUserRepository.findByUserIdAndGroupPkIdNaiveQuery(UserContextHolder.getContext().getUserId(), parentGroup.getPkId());
				if (groupUsers.size()>0) {
					groupUsers.forEach(groupUser->{
						if(groupUser.getRole().name().equals(Role.SALESADMIN.name())) {
							parentGroup.setAddTopFlig(0);
						}else{
							parentGroup.setAddTopFlig(1);
						}
//						
					});	
				}
//				parentGroup.setAddTopFlig(0);
				if (assignedGroupIds.contains(parentGroup.getPkId())) {
					assignedGroupList.add(parentGroup);
				}
			}
			
			groupList = GroupUtil.retainAssignedTopGroups(assignedGroupList);
		}
		
		return groupList;
	}

	public void addGroupUser(int groupId, GroupUser groupUser) {
		Optional<Group> optional = groupRepository.findById(groupId);
		
		if (optional.isPresent()) {
			Group group = optional.get();
			if (GroupType.sales.equals(group.getType())) {
				validateStateMaxLimitByGroupId(groupId);
			}
			for (GroupUser gu : group.getGroupUsers()) {
				if (gu.equals(groupUser)) {
					throw new DuplicateEntityException("There already is a role named '" + groupUser.getRole() + "'!");
				}
			}
			group.addGroupUser(groupUser);
			groupRepository.save(group);
		}
	}
	
	public void removeGroupUser(int groupId, GroupUser groupUser){
		Optional<Group> optional = groupRepository.findById(groupId);
		if (optional.isPresent()) {
			Group group = optional.get();
			group.removeGroupUser(groupUser);
			groupRepository.save(group);
		}
	}

	public List<Group> findStateGroups() {
		long userId = UserContextHolder.getContext().getUserId();
		List<GroupUser> groupUser = groupUserRepository.findByUserIdNaiveQuery(userId);
		List<Group> groups = new ArrayList<>();
		List<Group> results = new ArrayList<>();
		Set<String> role = UserContextHolder.getContext().getAuthorities();
		role.forEach(r->{
			if (r.contains("ADMINISTRATOR")) {
				results.addAll(groupRepository.findByTypeOrderByNameAsc(GroupType.sales));
			}
		});
		if (results != null && results.size() > 0) {
			return results;
		}
		groupUser.forEach(g->{
			if (g.getGroup().getType().equals(GroupType.sales) && g.getGroup().getParentPkId() != 0) {
				groups.add(g.getGroup());
			}
		});
		groups.forEach(g->{
			results.add(findByGroupFromParents(g.getPkId(),null));
		});
		return results;
	}

	@Transactional
	public void addSalesGroup(int supplierGroupId, Group salesGroup) {
		Optional<Group> optional = groupRepository.findById(supplierGroupId);
		if (optional.isPresent()) {
			Group group = optional.get();
			// add fuyu 20180619 fix bug: Supplier Groups [Add Sales Groups] 相同的group可以添加多次 start
			List<Group> salesGroups= group.getSalesGroups();
			salesGroups = salesGroups.stream().filter(salesGroupTmep -> salesGroupTmep.getPkId() == salesGroup.getPkId()).collect(Collectors.toList());
			if(salesGroups.size() > 0) {
				throw new DuplicateEntityException("There already is a salesGroup named '" + salesGroups.get(0).getName() + "'!");
			}
			// add end
			group.addSalesGroup(salesGroup);
			List<GroupUser> groupUsers = group.getGroupUsers();
			List<Integer> userPkIds = groupUsers.stream().map(gu-> gu.getUser().getPkId()).collect(Collectors.toList());
//			List<Integer> cocktailsIdList = this.cocktailRepository.findBySupplierPkId(supplierGroupId);
			List<Cocktail> cocktailsList = this.cocktailRepository.findByMixologistPkIdInAndPublishedIs(userPkIds, true);
			List<Integer> cocktailIdList = cocktailsList.stream().map(ct-> ct.getId()).collect(Collectors.toList());
			List<CocktailGroup> accessGroups = new ArrayList<>();
			for (Integer cocktailPkid : cocktailIdList) {
				accessGroups.add(new CocktailGroup(cocktailPkid, salesGroup.getPkId()));
			}
			
			groupRepository.save(group);
			cocktailGroupRepository.saveAll(accessGroups);
		}
	}

	@Transactional
	public void removeSalesGroup(int supplierGroupId, Group salesGroup) {
		Optional<Group> optional = groupRepository.findById(supplierGroupId);
		if (optional.isPresent()) {
			Group group = optional.get();
			group.removeSalesGroup(salesGroup);
			
			cocktailGroupRepository.deleteByGroupId(salesGroup.getPkId());
			groupRepository.save(group);
		}
	}
	
	private void validateStateMaxLimit(int userId){
		Group stateGroup = getStateGroup(userRepository.findById(userId).get());
		int stateLimit = stateGroup.getStateNumberOfUsers();
		int numberOfUsers = stateGroup.getGroupUsers().size();
		if (numberOfUsers >= stateLimit) {
			throw new MaxNumberOfUsersExceededException("Number of users exceeds the maximum state [" + stateGroup.getPkId() + "] limit: " + stateGroup.getStateNumberOfUsers() + "!");
		}
		
	}

	private void validateStateMaxLimitByGroupId(int groupId){
		List<Group> stateGroupList = groupRepository.findStateGroup(groupId);
		if (!stateGroupList.isEmpty()) {
			int stateLimit = stateGroupList.get(0).getStateNumberOfUsers();
			int numberOfUsers = groupRepository.countNumberOfUsersByStateGroupId(stateGroupList.get(0).getPkId());;
			if (numberOfUsers >= stateLimit) {
				throw new MaxNumberOfUsersExceededException("Number of users exceeds the maximum state [" + stateGroupList.get(0).getPkId() + "] limit: " + stateLimit + "!");
			}
		}
	}
	
	private Group getStateGroup(User user) {
		Group group;
		UspsState state = null;
		for (GroupUser groupUser : user.getUserGroups()) {
			group = groupUser.getGroup();
			state = group.getState();
			if (!UspsState.NONE.equals(state) && state != null && !"".equals(state)) {
				return group;
			}
		}
		for (GroupUser groupUser : user.getUserGroups()) {
			group = groupUser.getGroup();
			if (group.getParentPkId() != 0) {
				return findStateGroupFromParents(group.getParentPkId());
			}
		}
		
		//No limit in case the user does not belong to any sales group
		Group g = new Group();
		g.setStateNumberOfUsers(99999);
		return g;
	}

	private Group findStateGroupFromParents(int pkId) {
		Group group = groupRepository.findById(pkId).get();
		UspsState state = group.getState();
		if (state != null && !"".equals(state)) {
			return group;
		} else {
			if (group.getParentPkId() != 0) {
				return findStateGroupFromParents(group.getParentPkId());
			}
		}
		
		//No limit in case the user does not belong to any state group
		Group top = new Group();
		top.setStateNumberOfUsers(99999);
		return top;
	}
	
	private Group findByGroupFromParents(int pkId,Group group) {
		Group g = groupRepository.findById(pkId).get();
		if (g.getParentPkId() != 0) {
			return findByGroupFromParents(g.getParentPkId(),g);
		} else {
			return group;
		}
	}

	public List<Group> findByTypeAndDistributorIdIsNull(GroupType type) {
		return groupRepository.findByTypeAndDistributorIdIsNull(type).stream().map(g -> new Group(g.getPkId(), g.getName())).collect(Collectors.toList());
	}

	public List<Integer> getSubGroupIdList(int selectedSalesGroupId) {
		Optional<Group> opt;
		Group selectedGroup = new Group(selectedSalesGroupId);
		for (Group topGroup : this.findTreeByType(GroupType.sales,null)) {
			opt = GroupUtil.findSelectedGroupFromTree(selectedSalesGroupId, topGroup);
			if (opt.isPresent()) {
				selectedGroup = opt.get();
			}
		}

		List<Integer> subGroupIdList = new ArrayList<>();
		subGroupIdList.add(selectedSalesGroupId);
		if (selectedGroup.hasSubGroups()) {
			GroupUtil.loadSubGroupIdList(subGroupIdList, selectedGroup.getSubGroups());
		}
		return subGroupIdList;
	}
	
	public void valiateGroup(Group group) {
		Integer groupPkId = group.getPkId();
		List<Group> groups = groupRepository.findByNameIgnoreCase(group.getName());
		if (groupPkId != null && groupPkId != 0) {
			 Group g = groupRepository.findById(groupPkId).get();
			 if (g != null && !g.getName().toLowerCase().equals(group.getName().toLowerCase())) {
				 if (groups.size() > 0) {
					 throw new DuplicateEntityException("Repetitive group!");
				 }
			 }
		} else {
			if (groups.size() > 0) {
				throw new DuplicateEntityException("Repetitive group!");
			}
		}
	}
}
