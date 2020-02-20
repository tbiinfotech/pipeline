package com.liquidpresentaion.users.controllers;

import static com.liquidpresentation.common.Constants.PAGE_PARAM_KEYWORD;
import static com.liquidpresentation.common.Constants.PAGE_PARAM_KEYWORD_DEFAULT;
import static com.liquidpresentation.common.Constants.PAGE_PARAM_PAGE;
import static com.liquidpresentation.common.Constants.PAGE_PARAM_PAGE_DEFAULT;
import static com.liquidpresentation.common.Constants.PAGE_PARAM_PROPERTY;
import static com.liquidpresentation.common.Constants.PAGE_PARAM_PROPERTY_DEFAULT;
import static com.liquidpresentation.common.Constants.PAGE_PARAM_SIZE;
import static com.liquidpresentation.common.Constants.PAGE_PARAM_SIZE_DEFAULT;
import static com.liquidpresentation.common.Constants.PAGE_PARAM_SORT;
import static com.liquidpresentation.common.Constants.PAGE_PARAM_SORT_DEFAULT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.liquidpresentaion.users.context.UserContextHolder;
import com.liquidpresentaion.users.model.Cocktail;
import com.liquidpresentaion.users.model.FlavorProfile;
import com.liquidpresentaion.users.model.GlassStyle;
import com.liquidpresentaion.users.model.Group;
import com.liquidpresentaion.users.model.GroupUser;
import com.liquidpresentaion.users.model.OutletType;
import com.liquidpresentaion.users.model.SeasonalThemed;
import com.liquidpresentaion.users.model.User;
import com.liquidpresentaion.users.repository.GroupRepository;
import com.liquidpresentaion.users.repository.GroupUserRepository;
import com.liquidpresentaion.users.services.CocktailService;
import com.liquidpresentaion.users.services.UserService;
import com.liquidpresentation.common.BaseSpiritCategory;
import com.liquidpresentation.common.Category;
import com.liquidpresentation.common.GroupType;
import com.liquidpresentation.common.Role;
import com.liquidpresentation.common.utils.PageUtil;

@RestController
@RequestMapping(value="internal/v1/users")
public class UserServiceController {

	@Autowired
	private UserService userService;
	@Autowired
	private CocktailService cocktailService;
	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private GroupUserRepository groupUserRepository;

	@RequestMapping(method = RequestMethod.GET)
	public Page<User> getUsers(	@RequestParam(name = "keyword", defaultValue = "")String keyword,
									@RequestParam(name = "page", defaultValue = "0") int page, 
									@RequestParam(name = "size", defaultValue = "25") int size,
									@RequestParam(name = "property", defaultValue = "email") String property,
									@RequestParam(name = "asc", defaultValue = "true") boolean asc,
									@RequestParam(name = "isAddUser", defaultValue = "false") boolean isAddUser
									){
		if("firstName".equals(property)) {
			property = "firstName,lastName";
		}
		PageRequest pageRequest = PageUtil.buildPageRequest(page, size, property, asc);
		if (keyword != null) {
			keyword = keyword.trim();
		}
		if ("".equals(keyword)) {
			Page<User> u = userService.findAll(pageRequest);
			List<User> resultUser = new ArrayList<>();
			
			if (isAddUser) {
//				List<User> user = u.getContent();
//				for(User ur : user) {
//					if (!groupUserRepository.existsByUserAndRole(ur, Role.SALES) || !groupUserRepository.existsByUserAndRole(ur, Role.MANAGER)) {
//						resultUser.add(ur);
//					}
//				};
				resultUser = u.getContent();
				
				return new PageImpl<>(resultUser.stream().map(b -> new User(b.getPkId(),b.getFirstName(),b.getLastName(), b.getCreateDate(), b.getEmail(), b.getPhone())).collect(Collectors.toList()), pageRequest, resultUser.size());
			}
				return new PageImpl<>(u.stream().map(b -> new User(b.getPkId(),b.getFirstName(),b.getLastName(), b.getCreateDate(), b.getEmail(), b.getPhone())).collect(Collectors.toList()), pageRequest, u.getTotalElements());
		} else {
			return userService.findByKeyword(keyword, pageRequest);
		}
	}
	
	@RequestMapping(value = "/{userId}/groups", method = RequestMethod.GET)
	public Page<GroupUser> getUserGroups(@PathVariable("userId") int userId, 
											@RequestParam(name = "page", defaultValue = "0") int page, 
											@RequestParam(name = "size", defaultValue = "25") int size){
		List<GroupUser> groups = new LinkedList<>(userService.getUser(userId).getUserGroups());
		return new PageImpl<>(groups, PageRequest.of(page, size), groups.size());
	}
	
	@Deprecated
	@RequestMapping(value = "/{userId}/groups", method = RequestMethod.PUT)
	public void saveUserGroups(@RequestBody List<GroupUser> groups, @PathVariable("userId") int userId){
		userService.saveUserGroups(userId, groups);
	}
	
	@RequestMapping(value = "/{userId}", method = RequestMethod.GET)
	public User getUser(@PathVariable("userId") int userId){
		return userService.getUser(userId);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public void saveUser(@RequestBody User user){
		userService.saveUser(user);
	}
	
	@RequestMapping(value = "/user/role/home", method = RequestMethod.GET)
	public String getUserRole4Home(){
		return userService.getUserRole4Home();
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public void registerUser(@RequestBody User user){
		userService.saveUser(user);
	}
	
	@RequestMapping(value = "/register/email/validation", method = RequestMethod.GET)
	public boolean emailValidate4Register(@RequestParam(name = "email") String email){
		return userService.valiateEmail(email);
	}
	
	@RequestMapping(value = "/register/recoveryCode/validation", method = RequestMethod.GET)
	public boolean validateRecoveryCode(@RequestParam(name = "email") String email,
										@RequestParam(name = "recoveryCode") String recoveryCode,
										@RequestParam(name = "sign") String sign){
		return userService.validateRecoveryCode(email, recoveryCode, sign);
	}
	
	@RequestMapping(value = "/register/email/existence/validation", method = RequestMethod.GET)
	public boolean checkEmailExistsInDb(@RequestParam(name = "email") String email){
		return userService.checkEmailExistsInDb(email);
	}
	
	@RequestMapping(value = "/register/pwd/modification", method = RequestMethod.POST)
	public void updateUserPwd(@RequestParam(name = "email") String email,
							  @RequestParam(name = "password") String password,
							  @RequestParam(name = "recoveryCode") String recoveryCode,
							  @RequestParam(name = "sign") String sign){
		userService.updateUserPwd(email, password, recoveryCode, sign);
	}
	
	@RequestMapping(value = "/register/recoveryCode/modification", method = RequestMethod.POST)
	public User updateUserRecoveryCode(@RequestParam(name = "email") String email,
									   @RequestParam(name = "recoveryCode") String recoveryCode){
		return userService.updateUserRecoveryCode(email, recoveryCode);
	}

	@RequestMapping(method = RequestMethod.PUT)
	public void updateUser(@RequestBody User user){
		userService.updateUser(user);
	}
	
	@RequestMapping(method = RequestMethod.DELETE)
	public void deleteUser(@RequestBody User user){
		userService.deleteUser(user);
	}
	
	@RequestMapping(value = "/groups/users", method = RequestMethod.POST)
	public List<User> findUsersByGroups(@RequestBody List<Group> groups){
		return userService.findUsersByGroups(groups);
	}
	
	@SuppressWarnings("rawtypes")
	@GetMapping(value = "/current")
	public User getCurrentUser(){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Map userMap = (Map) authentication.getPrincipal();
		User user = userService.getUser(Integer.valueOf(userMap.get("userId").toString()));
		user.setPassword(null);
		return user;
	}
	
	@RequestMapping(value = "/get/{role}/users", method = RequestMethod.GET)
	public List<User> findByGroupUser(@PathVariable("role") Role role){
		return userService.findByGroupUser(role).stream().map(u->new User(u.getPkId(), u.getFirstName(), u.getLastName())).collect(Collectors.toList());
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/upgrade/mail/list", method = RequestMethod.GET)
	public Map<String, List<String>> findIngredientUpgradeMailList(){
		HashMap<String, List<String>> restuls = new HashMap<>();

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Map userMap = (Map) authentication.getPrincipal();
		User user = userService.getUser(Integer.valueOf(userMap.get("userId").toString()));
		user.setPassword(null);

		restuls.put("administrators", userService.findAdministrators());
		restuls.put("distributorMixologists", userService.findDistributorMixologists(user));
		restuls.put("currentUser", Arrays.asList(user.getEmail(), user.getStateName(), user.getDistributorName()));
		
		return restuls;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/cocktails")
	public ResponseEntity<Map<String, Object>> getCocktails(	@RequestParam(name = PAGE_PARAM_KEYWORD, defaultValue = PAGE_PARAM_KEYWORD_DEFAULT)String keyword,
			@RequestParam(name = "fromPage", required = false)String fromPage,
			@RequestParam(name = PAGE_PARAM_PAGE, defaultValue = PAGE_PARAM_PAGE_DEFAULT) int page, 
			@RequestParam(name = PAGE_PARAM_SIZE, defaultValue = PAGE_PARAM_SIZE_DEFAULT) int size,
			@RequestParam(name = PAGE_PARAM_PROPERTY, defaultValue = PAGE_PARAM_PROPERTY_DEFAULT) String property,
			@RequestParam(name = PAGE_PARAM_SORT, defaultValue = PAGE_PARAM_SORT_DEFAULT) boolean asc,
			@RequestParam(name = "filter", required = false) String filter){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Cocktail> cocktailList = new ArrayList<>();
		Set<String> baseSpiritCategorySet = new HashSet<>();
		Set<String> flavorProfileSet = new HashSet<>();
		Set<String> glassStylesSet = new HashSet<>();
		Set<String> outletTypeSet = new HashSet<>();
		Set<String> seasonalThemedSet = new HashSet<>();
		Set<Integer> degreeOfDiffSet = new HashSet<>();
		Set<String> supplierSet = new HashSet<>();
		Set<String> categorySet = new HashSet<>();
		for (BaseSpiritCategory b : BaseSpiritCategory.values()) {
			baseSpiritCategorySet.add(b.toString());
		}
		for (FlavorProfile f : FlavorProfile.values()) {
			flavorProfileSet.add(f.toString());
		}
		for (GlassStyle g : GlassStyle.values()) {
			glassStylesSet.add(g.toString());
		}
		for (OutletType o : OutletType.values()) {
			outletTypeSet.add(o.toString());
		}
		for (SeasonalThemed s : SeasonalThemed.values()) {
			seasonalThemedSet.add(s.toString());
		}
		for (Category c : Category.values()) {
			categorySet.add(c.toString());
		}
		for (int i = 0; i<6; i++) {
			degreeOfDiffSet.add(i);
		}
		PageRequest pageRequest = PageUtil.buildPageRequest(page, size, property, asc);
		
		
		Page<Cocktail> pageResult; 
		if (keyword != null) {
			keyword = keyword.trim();
		}
		if (PAGE_PARAM_KEYWORD_DEFAULT.equals(keyword) && StringUtils.isBlank(filter)) {
			pageResult =  cocktailService.findAll(pageRequest,fromPage);
			
			Optional<Group> groups = null;
			if (UserContextHolder.getContext().isMixologist()) {
				List<Integer> salesGroupIds = new ArrayList<>();
				List<GroupUser> groupUsers = groupUserRepository.findByUserIdNaiveQuery(UserContextHolder.getContext().getUserId());
				groupUsers.forEach(gu -> {
					if (gu.getGroup().getType().equals(GroupType.sales)) {
						salesGroupIds.add(gu.getGroup().getPkId());
					}
				});
				if (CollectionUtils.isEmpty(UserContextHolder.getContext().getSupplierGroupIds())) {
					resultMap.put("addFlig", 0);
				} else {
					for(int id : UserContextHolder.getContext().getSupplierGroupIds()) {
						groups =  groupRepository.findById(id);
					}
					Group group =	groups.get();
					
					if (pageResult.getContent().size() >= group.getCocktailsLimit()) {
						resultMap.put("addFlig", 1);
					}else {
						resultMap.put("addFlig", 0);
					}
				}
			}
		} else {
//			if(StringUtils.isNotEmpty(fromPage) && StringUtils.isNotEmpty(keyword)) {
//				pageRequest = PageUtil.buildPageRequest(0, 10000, property, asc);
//				
//			}
			pageResult =  cocktailService.findByKeyword(fromPage, keyword, filter, pageRequest);
			
			
		}
		
		if (StringUtils.isEmpty(fromPage)) {
			cocktailList = pageResult.getContent();
			
			for (Cocktail cocktail : cocktailList) {
				baseSpiritCategorySet.add(cocktail.getBaseSpiritCategory().toString());
				degreeOfDiffSet.add(cocktail.getDegreeOfDiff());
				if (StringUtils.isNotBlank(cocktail.getSupplierName())) {
					supplierSet.add(cocktail.getSupplierName());
				}
			}
			resultMap.put("baseSpiritCategorySet",baseSpiritCategorySet);
			resultMap.put("degreeOfDiffSet", degreeOfDiffSet);
			resultMap.put("categorySet", categorySet);
			resultMap.put("FlavorProfileSet", flavorProfileSet);
			resultMap.put("glassStylesSet", glassStylesSet);
			resultMap.put("outletTypeSet", outletTypeSet);
			resultMap.put("seasonalThemedSet", seasonalThemedSet);
			resultMap.put("supplierSet", supplierSet);
			resultMap.put("pageResult", pageResult);
			return new ResponseEntity<Map<String, Object>>(resultMap,HttpStatus.OK);
		} else {
			cocktailList = pageResult.getContent();
			if(StringUtils.isEmpty(keyword)) {
				resultMap.put("pageResult", new PageImpl<>(cocktailList, pageResult.getPageable(), pageResult.getTotalElements()));
				for (Cocktail cocktail : cocktailList) {
					baseSpiritCategorySet.add(cocktail.getBaseSpiritCategory().toString());
					degreeOfDiffSet.add(cocktail.getDegreeOfDiff());
					if (StringUtils.isNotBlank(cocktail.getSupplierName())) {
						supplierSet.add(cocktail.getSupplierName());
					}
				}
				resultMap.put("baseSpiritCategorySet",baseSpiritCategorySet);
				resultMap.put("degreeOfDiffSet", degreeOfDiffSet);
				resultMap.put("categorySet", categorySet);
				resultMap.put("FlavorProfileSet", flavorProfileSet);
				resultMap.put("glassStylesSet", glassStylesSet);
				resultMap.put("outletTypeSet", outletTypeSet);
				resultMap.put("seasonalThemedSet", seasonalThemedSet);
				resultMap.put("supplierSet", supplierSet);
				resultMap.put("pageResult", pageResult);
			} else {
				cocktailList = pageResult.getContent();
				List<Cocktail> finalListTemp = new ArrayList<>();
				if (cocktailList.size() > page*size) {
					finalListTemp = cocktailList.subList(page*size, cocktailList.size() > (page*size + size) ? page*size + size : cocktailList.size());
					for (Cocktail cocktail : finalListTemp) {
						baseSpiritCategorySet.add(cocktail.getBaseSpiritCategory().toString());
						degreeOfDiffSet.add(cocktail.getDegreeOfDiff());
						if (StringUtils.isNotBlank(cocktail.getSupplierName())) {
							supplierSet.add(cocktail.getSupplierName());
						}
					}
				}
				
				resultMap.put("baseSpiritCategorySet",baseSpiritCategorySet);
				resultMap.put("degreeOfDiffSet", degreeOfDiffSet);
				resultMap.put("categorySet", categorySet);
				resultMap.put("FlavorProfileSet", flavorProfileSet);
				resultMap.put("glassStylesSet", glassStylesSet);
				resultMap.put("outletTypeSet", outletTypeSet);
				resultMap.put("seasonalThemedSet", seasonalThemedSet);
				resultMap.put("supplierSet", supplierSet);
				resultMap.put("pageResult", new PageImpl<>(finalListTemp, PageRequest.of(page, size), cocktailList.size()));
			}
			return new ResponseEntity<Map<String, Object>>(resultMap,HttpStatus.OK);
		}
	}
	
	
	@RequestMapping(value = "/discontinued", method = RequestMethod.GET)
	public int getDiscontinuedCount () {
		return cocktailService.getDeletedBrandCocktailNum();
	}
}
