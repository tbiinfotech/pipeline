package com.liquidpresentaion.authenticationservice.security;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.liquidpresentaion.authenticationservice.model.Group;
import com.liquidpresentaion.authenticationservice.model.GroupUser;
import com.liquidpresentaion.authenticationservice.model.LpUserDetails;
import com.liquidpresentaion.authenticationservice.model.User;
import com.liquidpresentaion.authenticationservice.repository.GroupRepository;
import com.liquidpresentaion.authenticationservice.repository.UserRepository;

public class LpUserDetailsService extends JpaRepositoryImpl {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private GroupRepository groupRepository;
	
    protected UserDetails createUserDetails(String username, UserDetails userFromUserQuery,
            List<GrantedAuthority> combinedAuthorities) {
    	
    	LpUserDetails user = (LpUserDetails) userFromUserQuery;
    	return new LpUserDetails(username, userFromUserQuery.getPassword(), userFromUserQuery.isEnabled(),
                true, true, true, combinedAuthorities, user.getUserId(), user.getPhone(), user.getFirstName(), user.getLastName(), user.getSupplierGroupIds(), user.getDistributorId());
    }
    
	protected List<UserDetails> loadUsersByUsername(String username) {
		String email = username;
		List<User> empList = userRepository.findByEmail(email);
		List<UserDetails> userList = new ArrayList<UserDetails>();
		
		for (User employee : empList) {
			Optional<BigInteger> distributorPkid = userRepository.findDistributorPkidByUserPkid(employee.getPkId());
			UserDetails userDetails = new LpUserDetails(employee.getFirstName(), employee.getPassword(), createAuthorityList(employee.getUserGroups()), Long.valueOf(employee.getPkId()),
					employee.getPhone(), employee.getFirstName(), employee.getLastName(), userRepository.findSupplierGroupsByUserPkid(employee.getPkId()).stream().map(s -> s.toString()).collect(Collectors.toSet()), distributorPkid.isPresent()?distributorPkid.get().longValue():0);
			userList.add(userDetails);
		}
        return userList;
    }
	
	private List<GrantedAuthority> createAuthorityList(List<GroupUser> userGroups) {
		List<GrantedAuthority> authorities = new ArrayList<>();

		for (GroupUser groupUser : userGroups) {
			/*// 2018.11.6 changed for differentiating sales mixologist and supplier mixologist
			if("MIXOLOGIST".equals(groupUser.getRole().name())) { // if mixologist add differentiation operate
				Group group = groupRepository.findOne(groupUser.getGroupPkId());
				String groupName = group.getType().name();
				authorities.add(new SimpleGrantedAuthority(groupUser.getGroupPkId() + "_" + groupUser.getRole().name() + "_" + groupName));
			} else { // otherwise work as old version
				authorities.add(new SimpleGrantedAuthority(groupUser.getGroupPkId() + "_" + groupUser.getRole().name()));
			}*/
			authorities.add(new SimpleGrantedAuthority(groupUser.getGroupPkId() + "_" + groupUser.getRole().name()));
		}

		return authorities;
	}
}
