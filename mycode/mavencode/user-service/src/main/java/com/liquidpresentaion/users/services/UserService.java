package com.liquidpresentaion.users.services;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.liquidpresentaion.users.context.UserContextHolder;
import com.liquidpresentaion.users.exceptions.DuplicateEntityException;
import com.liquidpresentaion.users.exceptions.MaxNumberOfUsersExceededException;
import com.liquidpresentaion.users.model.Domain;
import com.liquidpresentaion.users.model.Group;
import com.liquidpresentaion.users.model.GroupUser;
import com.liquidpresentaion.users.model.User;
import com.liquidpresentaion.users.repository.DomainRepository;
import com.liquidpresentaion.users.repository.GroupRepository;
import com.liquidpresentaion.users.repository.GroupUserRepository;
import com.liquidpresentaion.users.repository.UserRepository;
import com.liquidpresentation.common.GroupType;
import com.liquidpresentation.common.Role;
import com.liquidpresentation.common.utils.StringUtil;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private DomainRepository domainRepository;
	
	@Autowired
	private GroupRepository groupRepository;
	
	@PersistenceContext
	private EntityManager entityManger;
	
	@Autowired
	private UserColumnService userColumnService;
	
	@Autowired
	private GroupUserRepository groupUserRepository;
	
	public Page<User> findAll(PageRequest pageRequest){
		Long userId = UserContextHolder.getContext().getUserId();
		if (UserContextHolder.getContext().isAdmin()) {
			return userRepository.findByPkIdNot(userId.intValue(),pageRequest);
		}
		List<GroupUser> groupUsers = groupUserRepository.findByUserIdNaiveQuery(userId);
		List<Integer> groupIds = new ArrayList<>();
		List<Integer> userIds = new ArrayList<>();
		List<Group> groups = new ArrayList<>();
		groupUsers.forEach(gu->{
			groups.addAll(groupRepository.findGroupNativeQuery(gu.getGroup().getPkId()));
		});
		if (groups.size() > 0) {
			groups.forEach(g->{
				groupIds.add(g.getPkId());
			});
		}
		Page<User> users = null;
		if (groupIds.size() > 0 ) {
			List<GroupUser> usersGroup = groupUserRepository.findByGroupPkIdsIdNaiveQuery(groupIds);
			usersGroup.forEach(u->{
				if (!userId.equals((long)u.getUser().getPkId())) {
					userIds.add(u.getUser().getPkId());
				}
			});
			users = userRepository.findByPkIdIn(userIds,pageRequest);
		}
		return users;
	}
	
	/**
	 * validate if number of users exceeds the max limit defined in domain table
	 * 
	 * @param newUser
	 */
	public void saveUser(User newUser){
		//modify fuyu 20180614 fix bug user-1  追加校验，如果doman不存在，直接返回。无错误提示。start
		boolean checkReasult = valiateDomainMaxLimitForSave(newUser);
		boolean checkEmail = valiateEmail(newUser);
		//modify end
		String encodePassword = passwordEncoder().encode(newUser.getPassword());
		newUser.setPassword(encodePassword);
		User saved = userRepository.save(newUser);
		this.userColumnService.saveDefaults(saved.getPkId());
	}
	
	public void updateUser(User user){
		
		User persist = this.getUser(user.getPkId());
		String oldDomain = persist.getEmail().substring(persist.getEmail().indexOf("@"));
		if (user.getEmail() != null && !user.getEmail().endsWith(oldDomain)) {
			//modify fuyu 20180614 fix bug user-1  追加校验，如果doman不存在，直接返回。无错误提示。start
			boolean checkReasult = valiateDomainMaxLimit(user);
			if(!checkReasult) {
				return;
			}
			//modify end
		}
		
		if (StringUtil.isAllEmpty(user.getEmail())) {
			user.setEmail(persist.getEmail());
		}
		if (StringUtil.isAllEmpty(user.getPassword())) {
			user.setPassword(persist.getPassword());
		} else{
			String encodePassword = passwordEncoder().encode(user.getPassword());
			user.setPassword(encodePassword);
		}
		user.setUserGroups(persist.getUserGroups());
		userRepository.save(user);
	}
	
	public void updateUserPwd(String email, String password, String recoveryCode, String sign){
		/*String validateStr = recoveryCode + "_" + email;
		String decryptStr = null;
		try {
			decryptStr = UrlUtil.deCryptAndDecode(sign);
		}catch(Exception e) {
			throw new RuntimeException("System exception occurred, please contact the administrator.");
		}
		if(validateStr.equals(decryptStr)) {*/
			List<User> userList = userRepository.findByEmailIgnoreCase(email);
			if(!CollectionUtils.isEmpty(userList)) {
				User persist = userList.get(0);
				if(!recoveryCode.equals(persist.getRecovery())) {
					throw new RuntimeException("The recovery code Expired.");
				}
				String encodePassword = passwordEncoder().encode(password);
				persist.setPassword(encodePassword);
				persist.setRecovery(null);
				userRepository.save(persist);
			} else {
				throw new RuntimeException("Could not find the email" + email + ".");
			}
/*		} else {
			throw new RuntimeException("The email was not that you filled at the previous time. Please check.");
		}*/
	}
	
	public User updateUserRecoveryCode(String email, String recoveryCode){
		List<User> userList = userRepository.findByEmailIgnoreCase(email);
		if(!CollectionUtils.isEmpty(userList)) {
			User persist = userList.get(0);
			persist.setRecovery(recoveryCode);
			return userRepository.save(persist);
		}
		return new User();
	}
	
	@Transactional
	public void deleteUser(User user){
		// TODO  前台关联删除参数 userGroups 已拼装好,报错,以后调查,暂时初始化userGroups
		user.setUserGroups(new ArrayList<>());
		entityManger.createNativeQuery("DELETE FROM LP_PRESENTATION WHERE ps_create_user_pkid = " + user.getPkId()).executeUpdate();
		//add fuyu fix bug user-5 User删除后，[lp_group_user]Table中，相关的userPkId没有被清除。 start
		entityManger.createNativeQuery("DELETE FROM LP_GROUP_USER WHERE GU_USER_PKID = " + user.getPkId()).executeUpdate();
		//add end
		userRepository.delete(user);
	}
	
	public User getUser(int userId){
		return userRepository.findById(userId).get();
	}

	public void saveUserGroups(int userId, List<GroupUser> groups) {
		 Optional<User> optional = userRepository.findById(userId);
		if (optional.isPresent()) {
			User user = optional.get();
			user.updateUserGroups(groups);
			userRepository.save(user);
		}
	}

	public Page<User> findByKeyword(String keyword, PageRequest pageRequest) {
		Long userId = UserContextHolder.getContext().getUserId();
		if (UserContextHolder.getContext().isAdmin()) {
			return userRepository.findByPkIdNotAndEmailIgnoreCaseContainingOrFirstNameIgnoreCaseContainingOrLastNameIgnoreCaseContaining(userId.intValue(),keyword,keyword,keyword,pageRequest);
		}
		List<GroupUser> groupUsers = groupUserRepository.findByUserIdNaiveQuery(userId);
		List<Integer> groupIds = new ArrayList<>();
		List<Integer> userIds = new ArrayList<>();
		List<Group> groups = new ArrayList<>();
		groupUsers.forEach(gu->{
			groups.addAll(groupRepository.findGroupNativeQuery(gu.getGroup().getPkId()));
		});
		if (groups.size() > 0) {
			groups.forEach(g->{
				groupIds.add(g.getPkId());
			});
		}
		Page<User> users = null;
		if (groupIds.size() > 0 ) {
			List<GroupUser> usersGroup = groupUserRepository.findByGroupPkIdsIdNaiveQuery(groupIds);
			usersGroup.forEach(u->{
				if (!userId.equals((long)u.getUser().getPkId())) {
					userIds.add(u.getUser().getPkId());
				}
			});
			users = userRepository.findByPkIdInAndEmailIgnoreCaseContainingOrFirstNameIgnoreCaseContainingOrLastNameIgnoreCaseContaining(userIds,keyword,keyword,keyword, pageRequest);
		}
		return users;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder(){
		//PasswordEncoder encoder = new BCryptPasswordEncoder(12);
		//return encoder;
		return new PasswordEncoder() {
	        @Override
	        public String encode(CharSequence charSequence) {
	            return getMd5(charSequence.toString());
	        }

	        @Override
	        public boolean matches(CharSequence charSequence, String s) {
	            return getMd5(charSequence.toString()).equals(s);
	        }
	        
	        public synchronized String getMd5(String input) {
	        	try {
	        		MessageDigest md = MessageDigest.getInstance("MD5");
	        		byte[] messageDigest = md.digest(input.getBytes());
	        		BigInteger number = new BigInteger(1, messageDigest);
	        		String hashtext = number.toString(16);
	        		// Now we need to zero pad it if you actually want the full 32 chars.
	        		while (hashtext.length() < 32) {
	        			hashtext = "0" + hashtext;
	        		}
	        		return hashtext;
	        	}
	        	catch (NoSuchAlgorithmException e) {
	        		throw new RuntimeException(e);
	        	}
	        }
	    };
	}
	
	private boolean valiateDomainMaxLimit(User user){
		boolean result = true;
		String domainName = user.getEmail().substring(user.getEmail().indexOf("@"));
		
		List<User> users = userRepository.findByEmailIgnoreCaseEndingWith(domainName);
		List<Domain> domains = domainRepository.findByNameIgnoreCaseEndingWith(domainName);
		if (!domains.isEmpty()) {
			if (users.size() >= domains.get(0).getNumberOfUsers()) {
				throw new MaxNumberOfUsersExceededException("Number of users exceeds the maximum domain limit!");
			}
		} else {
			//add fuyu 20180614 fix bug user-1  追加校验，如果doman不存在，直接返回。无错误提示。start
			throw new MaxNumberOfUsersExceededException("Registration denied for your email!");
			//add end
		}
		return result;
	}
	
	private boolean valiateDomainMaxLimitForSave(User user){
		boolean result = true;
		String domainName = user.getEmail().substring(user.getEmail().indexOf("@"));
		
		List<User> users = userRepository.findByEmailIgnoreCaseEndingWith(domainName);
		List<Domain> domains = domainRepository.findByNameIgnoreCaseEndingWith(domainName);
		if (!domains.isEmpty()) {
			if (users.size() >= domains.get(0).getNumberOfUsers()) {
				throw new MaxNumberOfUsersExceededException("Domain name has exceeded its user limit. Please contact your administrator.");
			}
		} else {
			//add fuyu 20180614 fix bug user-1  追加校验，如果doman不存在，直接返回。无错误提示。start
			throw new MaxNumberOfUsersExceededException("Registration denied for your email!");
			//add end
		}
		return result;
	}
	
	public boolean valiateEmail(User user) {
		boolean result = true;
		String email = user.getEmail();
		List<User> checkList = userRepository.findByEmailIgnoreCase(email);
		if (checkList != null && checkList.size() > 0) {
			throw new DuplicateEntityException("There already is an Email named '" + email + "'!");
		}
		return result;
	}
	
	public boolean valiateEmail(String email) {
		User user = new User(email);
		valiateDomainMaxLimitForSave(user);
		valiateEmail(user);
		return true;
	}
	
	public List<User> findUsersByGroups(List<Group> groups) {
		List<Integer> groupPkIds = groups.stream().map(Group::getPkId).collect(Collectors.toList());
		List<Group> loadedGroups = groupRepository.findByPkIdIn(groupPkIds);
		List<User> users = new ArrayList<User>();
		loadUsersFromGroups(users, loadedGroups);
		return users.stream().map(user -> new User(user.getEmail())).collect(Collectors.toList());
	}

	private void loadUsersFromGroups(List<User> users, List<Group> groups) {
		for (Group group : groups) {
			if (!group.getGroupUsers().isEmpty()) {
				for (GroupUser groupUser : group.getGroupUsers()) {
					if (!users.contains(groupUser.getUser())) {
						users.add(groupUser.getUser());
					}
				}
			}
			if (!group.getSubGroups().isEmpty()) {
				loadUsersFromGroups(users, group.getSubGroups());
			}
		}
	}

	public List<User> findByGroupUser(Role role) {
		List<User> userPage = (List<User>) userRepository.findAll();
		List<User> resultUser = new ArrayList<>();
		userPage.forEach(u->{
			if (!groupUserRepository.existsByUserAndRole(u, Role.ADMINISTRATOR)) {
				resultUser.add(u);
			}
		});
		return resultUser;
	}
	
	public String getUserRole4Home(){
		Set<String> roles = UserContextHolder.getContext().getAuthorities();
		List<String> roleList = new ArrayList<>();
		for(String roleStr : roles) {
			if(roleStr.contains("MIXOLOGIST")) {
				if(roleStr.contains("_")) {
					String[] idAndRoleArr = roleStr.split("_");
					String groupPkIdStr = idAndRoleArr[0];
					Optional<Group> groupOp = groupRepository.findById(Integer.valueOf(groupPkIdStr));
					if(groupOp.isPresent()) {
						Group group = groupOp.get();
						roleList.add(roleStr + "_" + group.getType().name());
					}
				}
			}
		}
		return StringUtils.join(roleList, ",");	
	}
	
	public boolean validateRecoveryCode(String email, String recoveryCode, String sign) {
		List<User> userList = userRepository.findByEmailIgnoreCase(email);
		if (CollectionUtils.isEmpty(userList)) {
			throw new RuntimeException("Could not find the email " + email + ".");
		}
		if (!recoveryCode.equals(userList.get(0).getRecovery())) {
			return false;
		}
		/*String validateStr = recoveryCode + "_" + email;
		String decryptStr;
		try {
			decryptStr = UrlUtil.deCryptAndDecode(sign);
		} catch (Exception e) {
			return false;
		}
		if (validateStr.equals(decryptStr)) {
			return true;
		}*/
		return true;
	}
	
	public boolean checkEmailExistsInDb(String email) {
		List<User> userList = userRepository.findByEmailIgnoreCase(email);
		return !CollectionUtils.isEmpty(userList);
	}

	public List<String> findAdministrators() {
		List<User> allusers = (List<User>) userRepository.findAll();
		return allusers.stream().filter(user -> user.isAdministrator()).map(user -> user.getEmail()).collect(Collectors.toList());
	}

	/**
	 * find distributor mixologists who are in the sales group or distributor with the current user
	 * 
	 * @param user
	 * @return
	 */
	public List<String> findDistributorMixologists(User user) {
		int salesGroupId = 0;
		String distributorName = "";
		List<String> distributorMixologists = new ArrayList<>();
		
		for (GroupUser groupUser : user.getUserGroups()) {
			//Get the salesGroupID where the current user is a Mixologist
			if (GroupType.sales.equals(groupUser.getGroup().getType()) && Role.MIXOLOGIST.equals(groupUser.getRole())) {
				salesGroupId = groupUser.getGroup().getPkId();
			}
		}
		
		List<Group> groups = groupRepository.findTopGroup(salesGroupId);
		if (!groups.isEmpty()) {
			//get all the sales groups under the top group
			List<Group> allSalesGroups = groupRepository.findGroupNativeQuery(groups.get(0).getPkId());
			List<Integer> salesGroupIds = allSalesGroups.stream().map(group -> group.getPkId()).collect(Collectors.toList());
			
			List<User> allusers = (List<User>) userRepository.findAll();
			distributorMixologists = allusers.stream().filter(user2 -> {
				boolean isSameDistributorMixologist = false;
				for (GroupUser groupUser : user2.getUserGroups()) {
					if (salesGroupIds.contains(groupUser.getGroup().getPkId()) && Role.MIXOLOGIST.equals(groupUser.getRole())) {
						isSameDistributorMixologist = true;
					}
				}
				return isSameDistributorMixologist;
			}).map(user3 -> user3.getEmail()).collect(Collectors.toList());
			
			distributorName = groups.get(0).getDistributor().getName();
		}
		
		//Save state name for email content
		List<Group> states = groupRepository.findStateGroup(salesGroupId);
		if (!states.isEmpty()) {
			user.setStateName(states.get(0).getState().toString());
			user.setDistributorName(distributorName);
		}
		
		return distributorMixologists;
	}
}
