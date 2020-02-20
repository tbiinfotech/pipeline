package com.liquidpresentaion.users.services;

import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.liquidpresentaion.users.context.UserContextHolder;
import com.liquidpresentaion.users.model.Brand;
import com.liquidpresentaion.users.model.Category;
import com.liquidpresentaion.users.model.Cocktail;
import com.liquidpresentaion.users.model.CocktailBrand;
import com.liquidpresentaion.users.model.CocktailCategory;
import com.liquidpresentaion.users.model.CocktailFlavorProfile;
import com.liquidpresentaion.users.model.CocktailGlassStyle;
import com.liquidpresentaion.users.model.CocktailOutletType;
import com.liquidpresentaion.users.model.CocktailSeasonalThemed;
import com.liquidpresentaion.users.model.FlavorProfile;
import com.liquidpresentaion.users.model.GlassStyle;
import com.liquidpresentaion.users.model.Group;
import com.liquidpresentaion.users.model.GroupUser;
import com.liquidpresentaion.users.model.OutletType;
import com.liquidpresentaion.users.model.SeasonalThemed;
import com.liquidpresentaion.users.repository.BrandRepository;
import com.liquidpresentaion.users.repository.CocktailGroupRepository;
import com.liquidpresentaion.users.repository.CocktailRepository;
import com.liquidpresentaion.users.repository.GroupRepository;
import com.liquidpresentaion.users.repository.GroupUserRepository;
import com.liquidpresentation.common.GroupType;
import com.liquidpresentation.common.utils.PageUtil;
import com.liquidpresentation.common.utils.StringUtil;


@Service
public class CocktailService {

	private static final Logger logger = LoggerFactory.getLogger(CocktailService.class);
	private static final int deletedId = 1;

	@Autowired
	private CocktailRepository cocktailRepository;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private CocktailGroupRepository cocktailGroupRepository;

	@Autowired
	private GroupUserRepository groupUserRepository;

	@Autowired
	private BrandRepository brandRepository;
	
	public Page<Cocktail> findAll(PageRequest pageRequet,String fromPage) {
		// return cocktailRepository.findAll(pageRequet);
		if (UserContextHolder.getContext().isAdmin()) {
			return this.filterCocktails(null, null, null, null, false,fromPage , pageRequet);
		} else {
			List<Integer> groupPkidList = UserContextHolder.getContext().getGroupPkidList();
			if (groupPkidList.isEmpty()) {
				return new PageImpl<Cocktail>(new ArrayList<Cocktail>(), pageRequet, 0);
			} else {
				List<Integer> cocktailsIdListFromSalesGroups = new ArrayList<>();
				for (Integer groupPkid : groupPkidList) {
					cocktailsIdListFromSalesGroups
							.addAll(cocktailGroupRepository.findCocktailsIdListGroupPkid(groupPkid));
				}
				return this.filterCocktails(null, null, cocktailsIdListFromSalesGroups,
						UserContextHolder.getContext().getSupplierGroupIds(),
						!UserContextHolder.getContext().isMixologist(),fromPage, pageRequet);
			}
		}
	}

	public Page<Cocktail> findByKeyword(String fromPage, String keyword, String filter, PageRequest pageRequet) {

		List<FilterCriteria> filterCriteria = new ArrayList<FilterCriteria>();
		if (filter != null) {
			filter = filter.replace(":,", ": ,");
			Pattern pattern = Pattern.compile("(.+?)(:|<|>)(.+?),");
			Matcher matcher = pattern.matcher(filter + ",");
			while (matcher.find()) {
				if(StringUtils.isNotBlank(matcher.group(3))) {
					filterCriteria.add(new FilterCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
				}
			}
		}

		if (UserContextHolder.getContext().isAdmin()) {
			return this.filterCocktails(keyword, filterCriteria, null, null, false,fromPage , pageRequet);
		} else {
			List<Integer> groupPkidList = UserContextHolder.getContext().getGroupPkidList();
			if (groupPkidList.isEmpty()) {
				return new PageImpl<Cocktail>(new ArrayList<Cocktail>(), pageRequet, 0);
			} else {
				List<Integer> cocktailsIdListFromSalesGroups = new ArrayList<>();
				for (Integer groupPkid : groupPkidList) {
					cocktailsIdListFromSalesGroups
							.addAll(cocktailGroupRepository.findCocktailsIdListGroupPkid(groupPkid));
				}
				return this.filterCocktails(keyword, filterCriteria, cocktailsIdListFromSalesGroups,
						UserContextHolder.getContext().getSupplierGroupIds(),
						!UserContextHolder.getContext().isMixologist(), fromPage, pageRequet);
			}
		}
	}


	private Page<Cocktail> filterCocktails(String keyword, List<FilterCriteria> filterCriteria,
			List<Integer> cocktailsIdListFromSalesGroups, List<Integer> supplierGroupIdList,
			boolean searchPublishedOnly,String fromPage, PageRequest pageRequet) {
		List<Cocktail> results = new ArrayList<>();
		List<Cocktail> resultList = new ArrayList<>();
		if (UserContextHolder.getContext().isAdmin()) {

			if (keyword != null && !keyword.equals("")) {
				if (fromPage != null) {
					String key1 = keyword.trim();
					String[] result11 = key1.split(" ");
					List<String> list = new ArrayList<>();
					String str11 = "";
					Boolean flg11 = false;
					for (int i = 0; i <= result11.length - 1; i++) {
						if (!result11[i].isEmpty() || !result11[i].equals("")) {
							if (result11[i].indexOf("\"")>-1) {
								if (result11[i].startsWith("\"") && result11[i].endsWith("\"")) {
									list.add(result11[i].replaceAll("\"", ""));
									continue;
								}
								if (!flg11) {
									str11 = result11[i].replaceAll("\"", "");;
									flg11 = true;
									continue;
								}else {
									str11 = str11 + " " + result11[i].replaceAll("\"", "");;
									list.add(str11);
									flg11 = false;
									continue;
								}
							}
							if(flg11) {
								str11 = str11 + " " + result11[i];
								continue;
							}
							list.add(result11[i]);
						}
					}
					
					List<String> listOr = new ArrayList<>();

					StringBuffer str1 = new StringBuffer();
					if(!list.get(0).equals("or")) {
						str1.append(list.get(0));
					}
					Boolean flg = false;
					if (list.size() > 1) {
						for (int i = 1; i < list.size(); i++) {
							list.get(i).trim();
							if ("or".equals(list.get(i))) {
								if (i == list.size() - 1) {
									listOr.add(str1.toString());
									break;
								}
								str1.append(" " + list.get(i));
								flg = true;
							} else {
								if (flg) {
									str1.append(" " + list.get(i));
									flg = false;
									if (i != list.size() - 1) {
										continue;
									}
								}
								listOr.add(str1.toString());
								str1 = new StringBuffer();
								str1.append(list.get(i));
								if(!"or".equals(list.get(i)) && i == list.size() - 1) {
									listOr.add(str1.toString());
									break;
								}

							}
						}
					}else {
						listOr.add(str1.toString());
						str1 = new StringBuffer();
					}

					List<Cocktail> temp = Lists.newArrayList(cocktailRepository.findAll());
					for (String str : listOr) {
						if (!str.equals("") && !str.equals(" ")) {
							temp = temp.stream().filter(cocktailPredicate(str, filterCriteria,fromPage))
									.collect(Collectors.toList());
						}
					}
					resultList = temp;
				} else {
					resultList = Lists.newArrayList(cocktailRepository.findAll()).stream()
							.filter(cocktailPredicate(keyword, filterCriteria,fromPage)).collect(Collectors.toList());
				}
			} else {
				resultList = Lists.newArrayList(cocktailRepository.findAll()).stream()
						.filter(cocktailPredicate(keyword, filterCriteria,fromPage)).collect(Collectors.toList());
			}
		} else {
			results = getCocktailsByRoles();
			if (keyword != null && !keyword.equals("")) {
				if (fromPage != null) {
					String key1 = keyword.trim();
					String[] result11 = key1.split(" ");
					List<String> list = new ArrayList<>();
					String str11 = "";
					Boolean flg11 = false;
					for (int i = 0; i <= result11.length - 1; i++) {
						if (!result11[i].isEmpty() || !result11[i].equals("")) {
							if (result11[i].indexOf("\"")>-1) {
								if (result11[i].startsWith("\"") && result11[i].endsWith("\"")) {
									list.add(result11[i].replaceAll("\"", ""));
									continue;
								}
								if (!flg11) {
									str11 = result11[i].replaceAll("\"", "");;
									flg11 = true;
									continue;
								}else {
									str11 = str11 + " " + result11[i].replaceAll("\"", "");;
									list.add(str11);
									flg11 = false;
									continue;
								}
							}
							if(flg11) {
								str11 = str11 + " " + result11[i];
								continue;
							}
							list.add(result11[i]);
						}
					}
					
					//////////////////////////
					List<String> listOr = new ArrayList<>();

					StringBuffer str1 = new StringBuffer();
					if(!list.get(0).equals("or")) {
						str1.append(list.get(0));
					}
					Boolean flg = false;
					if (list.size() > 1) {
						for (int i = 1; i < list.size(); i++) {
							list.get(i).trim();
							if ("or".equals(list.get(i))) {
								if (i == list.size() - 1) {
									listOr.add(str1.toString());
									break;
								}
								str1.append(" " + list.get(i));
								flg = true;
							} else {
								if (flg) {
									str1.append(" " + list.get(i));
									flg = false;
									if (i != list.size() - 1) {
										continue;
									}
								}
								listOr.add(str1.toString());
								str1 = new StringBuffer();
								str1.append(list.get(i));
								if(!"or".equals(list.get(i)) && i == list.size() - 1) {
									listOr.add(str1.toString());
									break;
								}

							}
						}
					}else {
						listOr.add(str1.toString());
						str1 = new StringBuffer();
					}

					for (String str : listOr) {
						if (!str.equals("") && !str.equals(" ")) {
							resultList = results.stream().filter(cocktailPredicate(str, filterCriteria,fromPage))
									.collect(Collectors.toList());
						}
					}
				} else {
					resultList = results.stream().filter(cocktailPredicate(keyword, filterCriteria,fromPage))
							.collect(Collectors.toList());
				}	
			} else {
				resultList = results.stream().filter(cocktailPredicate(keyword, filterCriteria,fromPage))
						.collect(Collectors.toList());
			}
		}
		
		for(Cocktail c : resultList) {
			Set<CocktailBrand> cocktailBrandSet = c.getBrandSet();
			Set<CocktailCategory> cocktailCategorySet = c.getCategorySet();
			Set<CocktailSeasonalThemed> cocktailSeasonalThemed = c.getSeasonalThemedSet();
			Set<CocktailGlassStyle> cocktailGlassStyle = c.getGlassStypleSet();
			Set<CocktailOutletType> cocktailOutletType = c.getOutletTypeSet();
			Set<CocktailFlavorProfile> cocktailFlavorProfile = c.getFlavorProfileSet();
			
			Set<CocktailCategory> cocktailCategoryTreeSet = new TreeSet<>(Comparator.comparing(o -> (o.getCategoryString())));
			Set<CocktailSeasonalThemed> cocktailSeasonalThemedTreeSet = new TreeSet<>(Comparator.comparing(o -> (o.getSeasonalThemedString())));
			Set<CocktailGlassStyle> cocktailGlassStyleTreeSet = new TreeSet<>(Comparator.comparing(o -> (o.getGlassStyleString())));
			Set<CocktailOutletType> cocktailOutletTypeTreeSet = new TreeSet<>(Comparator.comparing(o -> (o.getOutletTypeString())));
			Set<CocktailFlavorProfile> cocktailFlavorProfileTreeSet = new TreeSet<>(Comparator.comparing(o -> (o.getFlavorProfileString())));
			Set<CocktailBrand> cocktailBrandTreeSet = new TreeSet<>(Comparator.comparing(o -> (o.getId())));
			
			cocktailCategoryTreeSet.addAll(cocktailCategorySet);
			cocktailSeasonalThemedTreeSet.addAll(cocktailSeasonalThemed);
			cocktailGlassStyleTreeSet.addAll(cocktailGlassStyle);
			cocktailOutletTypeTreeSet.addAll(cocktailOutletType);
			cocktailFlavorProfileTreeSet.addAll(cocktailFlavorProfile);
			cocktailBrandTreeSet.addAll(cocktailBrandSet);
			
			c.setCategorySet(cocktailCategoryTreeSet);
			c.setSeasonalThemedSet(cocktailSeasonalThemedTreeSet);
			c.setGlassStypleSet(cocktailGlassStyleTreeSet);
			c.setOutletTypeSet(cocktailOutletTypeTreeSet);
			c.setFlavorProfileSet(cocktailFlavorProfileTreeSet);
			c.setBrandSet(cocktailBrandTreeSet);

			 
		}
		
		Comparator<Cocktail> orderBy = null;
		String orderString = null;
		if (fromPage != null && fromPage.equals("lookup")) {
			if (pageRequet.getSort().getOrderFor("Brand") != null) {
				orderString = "Brand";
				orderBy = Comparator.comparing(Cocktail::getBrandName, Comparator.nullsLast(Comparator.reverseOrder()));
			} else if (pageRequet.getSort().getOrderFor("BaseSpiritCategory") != null) {
				orderString = "BaseSpiritCategory";
				orderBy = Comparator.comparing(Cocktail::getBaseSpiritCategoryString, Comparator.nullsLast(Comparator.reverseOrder()));
			} else if (pageRequet.getSort().getOrderFor("BaseSpiritModifier") != null) {
				orderString = "BaseSpiritModifier";
				orderBy = Comparator.comparing(Cocktail::getBaseSpirit,
						Comparator.nullsLast(Comparator.reverseOrder()));
			} else if (pageRequet.getSort().getOrderFor("JuiceLiquids") != null) {
				orderString = "JuiceLiquids";
				orderBy = Comparator.comparing(Cocktail::getJuice,
						Comparator.nullsLast(Comparator.reverseOrder()));
			} else if (pageRequet.getSort().getOrderFor("Sweetener") != null) {
				orderString = "Sweetener";
				orderBy = Comparator.comparing(Cocktail::getSweetener,
						Comparator.nullsLast(Comparator.reverseOrder()));
			} else if (pageRequet.getSort().getOrderFor("Solids") != null) {
				orderString = "Solids";
				orderBy = Comparator.comparing(Cocktail::getSolids,
						Comparator.nullsLast(Comparator.reverseOrder()));
			} else if (pageRequet.getSort().getOrderFor("NumberOfIngredients") != null) {
				orderString = "NumberOfIngredients";
				orderBy = Comparator.comparing(Cocktail::getIngredientsnumber,
						Comparator.nullsLast(Comparator.reverseOrder()));
			} else if (pageRequet.getSort().getOrderFor("DegreeOfDifficulty") != null) {
				orderString = "DegreeOfDifficulty";
				orderBy = Comparator.comparing(Cocktail::getDegreeOfDiff, Comparator.nullsLast(Comparator.reverseOrder()));
			} else if (pageRequet.getSort().getOrderFor("CocktailCategory") != null) {
				orderString = "CocktailCategory";
				orderBy = Comparator.comparing(Cocktail::getCocktailCategory, Comparator.nullsLast(Comparator.reverseOrder()));
			} else if (pageRequet.getSort().getOrderFor("SeasonalThemed") != null) {
				orderString = "SeasonalThemed";
				orderBy = Comparator.comparing(Cocktail::getSeasonalThemed, Comparator.nullsLast(Comparator.reverseOrder()));
			} else if (pageRequet.getSort().getOrderFor("FlavorProfile") != null) {
				orderString = "FlavorProfile";
				orderBy = Comparator.comparing(Cocktail::getFlavorProfile, Comparator.nullsLast(Comparator.reverseOrder()));
			} else if (pageRequet.getSort().getOrderFor("CocktailGlassStyle") != null) {
				orderString = "CocktailGlassStyle";
				orderBy = Comparator.comparing(Cocktail::getGlassStyple, Comparator.nullsLast(Comparator.reverseOrder()));
			} else if (pageRequet.getSort().getOrderFor("NameOfCocktail") != null) {
				orderString = "NameOfCocktail";
				orderBy = Comparator.comparing(Cocktail::getName, Comparator.nullsLast(Comparator.reverseOrder()));
			} else if (pageRequet.getSort().getOrderFor("OutletType") != null) {
				orderString = "OutletType";
				orderBy = Comparator.comparing(Cocktail::getOutletType, Comparator.nullsLast(Comparator.reverseOrder()));
			} else if (pageRequet.getSort().getOrderFor("name") != null) {
				orderString = "name";
				orderBy = Comparator.comparing(Cocktail::getName, Comparator.nullsLast(Comparator.reverseOrder()));
			} else {
				orderString = "Supplier";
				orderBy = Comparator.comparing(Cocktail::getSupplierName, Comparator.nullsLast(Comparator.reverseOrder()));
			} 	
		} else {
			if (pageRequet.getSort().getOrderFor("name") != null) {
				orderString = "name";
				orderBy = Comparator.comparing(Cocktail::getName, Collator.getInstance(Locale.ENGLISH)).reversed();
			} else if (pageRequet.getSort().getOrderFor("createDate") != null) {
				orderString = "createDate";
				orderBy = Comparator.comparing(Cocktail::getCreateDate, Comparator.nullsLast(Comparator.reverseOrder()));
			} else if (pageRequet.getSort().getOrderFor("mixologistName") != null) {
				orderString = "mixologistName";
				orderBy = Comparator.comparing(Cocktail::getMixologistName,
						Comparator.nullsLast(Comparator.reverseOrder()));
			} else if (pageRequet.getSort().getOrderFor("supplierGroup.name") != null) {
				orderString = "supplierGroup.name";
				orderBy = Comparator.comparing(Cocktail::getSupplierName, Comparator.nullsLast(Comparator.reverseOrder()));
			} else if (pageRequet.getSort().getOrderFor("deletedBrand") != null) {
				orderString = "deletedBrand";
				orderBy = Comparator.comparing(Cocktail::getDeletedBrand, Comparator.nullsLast(Comparator.reverseOrder()));
			} else {
				orderString = "brandName";
				orderBy = Comparator.comparing(Cocktail::getBrandName, Comparator.nullsLast(Comparator.reverseOrder()));
			}
		}
		List<Brand> brands = brandRepository.findByDeletedIs(deletedId);
		List<Integer> brandPkIds = brands.stream().map(Brand :: getId).collect(Collectors.toList());
		for (Cocktail cocktail : resultList) {
			Set<String> stringSet = new HashSet<>();
			if (brandPkIds.contains(cocktail.getBrandPkId())) {
				stringSet.add(cocktail.getBrandName());
			}
			cocktail.getBrandSet().forEach(c->{
				if (brandPkIds.contains(c.getBrandPkId())) {
					stringSet.add(c.getBrandName());
				}
			});
			cocktail.setDeletedBrand(String.join(",", stringSet));
		}
		List<Cocktail> resultListTemp = new LinkedList<>();
		if (!pageRequet.getSort().getOrderFor(orderString).getDirection().equals(Direction.DESC)) {
			resultListTemp.addAll(resultList.stream().sorted(orderBy.reversed()).collect(Collectors.toList()));
		} else {
			resultListTemp.addAll(resultList.stream().sorted(orderBy).collect(Collectors.toList()));
		}
		resultListTemp = resultListTemp.stream().filter(cocktail -> cocktail.getArchiveFlag() == 0).collect(Collectors.toList());
		// the Filter List size
		int resultSize = resultListTemp.size();
		resultListTemp = PageUtil.buildPage(resultListTemp, pageRequet.getPageNumber(), pageRequet.getPageSize());
		return new PageImpl<>(resultListTemp, pageRequet, resultSize);
	}

	private List<Integer> findByGroupFromParents(int pkId, Group group, List<Integer> groupIds) {
		group = groupRepository.findById(pkId).get();
		groupIds.add(pkId);
		if (group.getParentPkId() != 0) {
			return findByGroupFromParents(group.getParentPkId(), group, groupIds);
		} else {
			return groupIds;
		}
	}
	
	private static String getRegex(String keyword) {
		keyword = keyword.trim();
		keyword = keyword.replace("(", "\\(").replace(")", "\\)");
		String regex;
		String metaToken = "#S=B@2#";
		String regexQuoted = "(\")(.*)(\")";
		// String regexAnd = "\\s+";
		String regexOr = "\\s+(or|oR|OR|Or)\\s+";
		Matcher matcherQuoted = Pattern.compile(regexQuoted).matcher(keyword);
		List<String> quotedTermList = new ArrayList<>();
		while (matcherQuoted.find()) {
			quotedTermList.add(matcherQuoted.group());
		}
		regex = keyword.replaceAll(regexQuoted, metaToken).replaceAll(regexOr, "|");
		// .replaceAll(regexAnd, "(.*)");
		for (int i = 0; i < quotedTermList.size(); i++) {
			regex = regex.replaceFirst(metaToken, quotedTermList.get(i));
		}
		regex = regex.replaceAll("\"", "")/*.replaceAll("\\(|\\)", metaToken).replaceAll(metaToken, "\\)\\(")*/;
		regex = "(" + regex + ")";

		logger.info("Regular expression generated from keyword [" + keyword + "] :" + regex);

		return regex;
	}

	public Predicate<Cocktail> cocktailPredicate(String keyword, List<FilterCriteria> filterCriteria,String fromPage) {
		String regex = StringUtil.isNotEmpty(keyword) ? getRegex(keyword) : "";
		return new Predicate<Cocktail>() {

			@Override
			public boolean test(Cocktail c) {
				SimpleDateFormat sf = new SimpleDateFormat("MM/dd/yyyy");
				String createDate = sf.format(c.getCreateDate());
				boolean match = false;
				if (fromPage != null) {
					match = StringUtil.isAllEmpty(keyword) ? true
							:Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(c.getDegreeOfDiff() + "").find()
								|| Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
										.matcher(c.getIngredientsnumber() + "").find() // Number of ingredients
								|| Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
										.matcher(c.getBaseSpiritCategory().name()).find()
									|| Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(c.getName()).find()
									|| Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(c.getBrandName()).find()
									|| Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
											.matcher(c.getSupplierName() == null ? "" : c.getSupplierName()).find()
											
//									|| Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(createDate == null ? "" : createDate).find()
//									|| Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(c.getMixologistName()).find()
//									|| c.getBrandSet().stream().anyMatch(brandSetPredicate(regex))
									|| Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(c.getBaseSpirit()).find()
									|| Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(c.getJuice()).find()
									|| Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(c.getSweetener()).find()
									|| Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(c.getSolids()).find()
									
									|| c.getCategorySet().stream().anyMatch(cocktailCategorySetPredicate(regex))
									|| c.getSeasonalThemedSet().stream().anyMatch(seasonalThemedSetPredicate(regex))
									|| c.getFlavorProfileSet().stream().anyMatch(flavorProfileSetPredicate(regex))
									|| c.getGlassStypleSet().stream().anyMatch(glassStyleSetPredicate(regex))
									|| c.getOutletTypeSet().stream().anyMatch(outletTypeSetPredicate(regex));
				} else {
					match = StringUtil.isAllEmpty(keyword) ? true
							:Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(c.getName()).find()
									|| Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(c.getBrandName() == null ? "" : c.getBrandName()).find()
									|| Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
											.matcher(c.getSupplierName() == null ? "" : c.getSupplierName()).find()
									|| Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
											.matcher(createDate == null ? "" : createDate).find()
					||(StringUtil.isAllEmpty(c.getMixologistName())?false:Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(c.getMixologistName()).find());
				}
				 

				if (filterCriteria != null && !filterCriteria.isEmpty()) {
					for (FilterCriteria criteria : filterCriteria) {
						String criteriaValue = (criteria.getValue() + "").toUpperCase();
						switch (criteria.getKey()) {
						case DegreeOfDifficulty:
							match = match && (String.valueOf(c.getDegreeOfDiff()).matches(criteriaValue.replaceAll("_", "|")));
							break;
						case NumberOfIngredients:
							match = match && String.valueOf(c.getIngredientsnumber()).contains(criteriaValue);
							break;
						case BaseSpiritCategory:
//							match = match && c.getBaseSpiritCategory().toString().toUpperCase().contains(criteriaValue); // old strategy : illegibility matching
							match = match && c.getBaseSpiritCategory().toString().toUpperCase().equals(criteriaValue); // change query strategy to perfect matching
							break;
						case NameOfCocktail:
							match = match && c.getName().toUpperCase().contains(criteriaValue);
							break;
						case Brand:
							match = match && c.getBrandName().toUpperCase().contains((criteriaValue));
							break;
						case Supplier:
							match = match && c.getSupplierGroup() == null ? false : c.getSupplierGroup().getName().toUpperCase().contains((criteriaValue));
							break;
						case BaseSpiritModifier:
							match = match && (StringUtils.isEmpty(c.getBaseSpirit()) ? false : c.getBaseSpirit().toUpperCase().contains((criteriaValue)));
							break;
						case JuiceLiquids:
							match = match && (StringUtils.isEmpty(c.getJuice()) ? false : c.getJuice().toUpperCase().contains((criteriaValue)));
							break;
						case Sweetener:
							match = match && (StringUtils.isEmpty(c.getSweetener()) ? false : c.getSweetener().toUpperCase().contains((criteriaValue)));
							break;
						/*case Solids:
							match = match
									&& c.getBrandSet().stream().anyMatch(brandSetPredicate(criteriaValue));
							break;*/
						case Solids:
							match = match && (StringUtils.isEmpty(c.getSolids()) ? false : c.getSolids().toUpperCase().contains((criteriaValue)));
							break;	
						case CocktailCategory:
//							match = match && c.getCategorySet().stream().anyMatch(cocktailCategorySetPredicate(criteriaValue)); // old strategy : illegibility matching
							match = match && c.getCategorySet().stream().anyMatch(cocktailCategorySetPredicate4PerfectMatching(criteriaValue)); // change query strategy to perfect matching
							break;
						case SeasonalThemed:
//							match = match && c.getSeasonalThemedSet().stream().anyMatch(seasonalThemedSetPredicate(criteriaValue)); // old strategy : illegibility matching
							match = match && c.getSeasonalThemedSet().stream().anyMatch(seasonalThemedSetPredicate4PerfectMatching(criteriaValue)); // change query strategy to perfect matching
							break;
						case FlavorProfile:
//							match = match && c.getFlavorProfileSet().stream().anyMatch(flavorProfileSetPredicate(criteriaValue)); // old strategy : illegibility matching
							match = match && c.getFlavorProfileSet().stream().anyMatch(flavorProfileSetPredicate4PerfectMatching(criteriaValue)); // change query strategy to perfect matching
							break;
						case CocktailGlassStyle:
//							match = match && c.getGlassStypleSet().stream().anyMatch(glassStyleSetPredicate(criteriaValue)); // old strategy : illegibility matching
							match = match && c.getGlassStypleSet().stream().anyMatch(glassStyleSetPredicate4PerfectMatching(criteriaValue)); // change query strategy to perfect matching
							break;
						case OutletType:
//							match = match && c.getOutletTypeSet().stream().anyMatch(outletTypeSetPredicate(criteriaValue)); // old strategy : illegibility matching
							match = match && c.getOutletTypeSet().stream().anyMatch(outletTypeSetPredicate4PerfectMatching(criteriaValue)); // change query strategy to perfect matching
							break;
						default:
							break;
						}
					}
				}

				return match;
			}
		};
	}

	public Predicate<CocktailBrand> brandSetPredicate(String regex) {
		return new Predicate<CocktailBrand>() {
			@Override
			public boolean test(CocktailBrand cocktailBrand) {
				return Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(cocktailBrand.getBrandName()).find();
			}
		};
	}

	public Predicate<CocktailCategory> cocktailCategorySetPredicate(String regex) {
		return new Predicate<CocktailCategory>() {
			@Override
			public boolean test(CocktailCategory cocktailCategory) {
				return Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
						.matcher(cocktailCategory.getCategory().toString()).find();
			}
		};
	}
	
	// Method for Cocktail Category filter
	public Predicate<CocktailCategory> cocktailCategorySetPredicate4PerfectMatching(String queryCondition) {
		return new Predicate<CocktailCategory>() {
			@Override
			public boolean test(CocktailCategory cocktailCategory) {
				boolean conditionMatch = false;
				if(StringUtils.isBlank(queryCondition)) { // if query condition is null(include space), get all
					conditionMatch = true;
				} else { // otherwise
					Category category = cocktailCategory.getCategory(); // if category is null, no data matched
					if(category != null) {
						String categoryStrUpper = category.toString().toUpperCase();
						conditionMatch = categoryStrUpper.equals(queryCondition.toUpperCase()); // case insensitive equals() check for perfect matching
					}
				}
				return conditionMatch;
			}
		};
	}

	public Predicate<CocktailSeasonalThemed> seasonalThemedSetPredicate(String regex) {
		return new Predicate<CocktailSeasonalThemed>() {
			@Override
			public boolean test(CocktailSeasonalThemed seasonalThemed) {
				return Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
						.matcher(seasonalThemed.getSeasonalThemed().toString()).find();
			}
		};
	}
	
	// Method for Seasonal/Themed filter
	public Predicate<CocktailSeasonalThemed> seasonalThemedSetPredicate4PerfectMatching(String queryCondition) {
		return new Predicate<CocktailSeasonalThemed>() {
			@Override
			public boolean test(CocktailSeasonalThemed seasonalThemed) {
				boolean conditionMatch = false;
				if(StringUtils.isBlank(queryCondition)) { // if query condition is null(include space), get all
					conditionMatch = true;
				} else { // otherwise
					SeasonalThemed st = seasonalThemed.getSeasonalThemed(); // if SeasonalThemed is null, no data matched
					if(st != null) {
						String stStrUpper = st.toString().toUpperCase();
						conditionMatch = stStrUpper.equals(queryCondition.toUpperCase()); // case insensitive equals() check for perfect matching
					}
				}
				return conditionMatch;
			}
		};
	}

	public Predicate<CocktailFlavorProfile> flavorProfileSetPredicate(String regex) {
		return new Predicate<CocktailFlavorProfile>() {
			@Override
			public boolean test(CocktailFlavorProfile flavorProfile) {
				return Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(flavorProfile.getFlavorProfile().toString())
						.find();
			}
		};
	}
	
	// Method for Flavor Profile filter
	public Predicate<CocktailFlavorProfile> flavorProfileSetPredicate4PerfectMatching(String queryCondition) {
		return new Predicate<CocktailFlavorProfile>() {
			@Override
			public boolean test(CocktailFlavorProfile flavorProfile) {
				boolean conditionMatch = false;
				if(StringUtils.isBlank(queryCondition)) { // if query condition is null(include space), get all
					conditionMatch = true;
				} else { // otherwise
					FlavorProfile fp = flavorProfile.getFlavorProfile(); // if FlavorProfile is null, no data matched
					if(fp != null) {
						String fpStrUpper = fp.toString().toUpperCase();
						conditionMatch = fpStrUpper.equals(queryCondition.toUpperCase()); // case insensitive equals() check for perfect matching
					}
				}
				return conditionMatch;
			}
		};
	}

	public Predicate<CocktailGlassStyle> glassStyleSetPredicate(String regex) {
		return new Predicate<CocktailGlassStyle>() {
			@Override
			public boolean test(CocktailGlassStyle glassStyle) {
				return Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(glassStyle.getGlassStyle().toString())
						.find();
			}
		};
	}
	
	// Method for Cocktail Glass Style filter
	public Predicate<CocktailGlassStyle> glassStyleSetPredicate4PerfectMatching(String queryCondition) {
		return new Predicate<CocktailGlassStyle>() {
			@Override
			public boolean test(CocktailGlassStyle glassStyle) {
				boolean conditionMatch = false;
				if(StringUtils.isBlank(queryCondition)) { // if query condition is null(include space), get all
					conditionMatch = true;
				} else { // otherwise
					GlassStyle gs = glassStyle.getGlassStyle(); // if GlassStyle is null, no data matched
					if(gs != null) {
						String gsStrUpper = gs.toString().toUpperCase();
						conditionMatch = gsStrUpper.equals(queryCondition.toUpperCase()); // case insensitive equals() check for perfect matching
					}
				}
				return conditionMatch;
			}
		};
	}

	public Predicate<CocktailOutletType> outletTypeSetPredicate(String regex) {
		return new Predicate<CocktailOutletType>() {
			@Override
			public boolean test(CocktailOutletType outletType) {
				return Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(outletType.getOutletType().toString())
						.find();
			}
		};
	}
	
	// Method for Outlet Type filter
	public Predicate<CocktailOutletType> outletTypeSetPredicate4PerfectMatching(String queryCondition) {
		return new Predicate<CocktailOutletType>() {
			@Override
			public boolean test(CocktailOutletType outletType) {
				boolean conditionMatch = false;
				if(StringUtils.isBlank(queryCondition)) { // if query condition is null(include space), get all
					conditionMatch = true;
				} else { // otherwise
					OutletType ot = outletType.getOutletType(); // if OutletType is null, no data matched
					if(ot != null) {
						String otStrUpper = ot.toString().toUpperCase();
						conditionMatch = otStrUpper.equals(queryCondition.toUpperCase()); // case insensitive equals() check for perfect matching
					}
				}
				return conditionMatch;
			}
		};
	}

	public int getDeletedBrandCocktailNum() {
		List<Cocktail> cocktails = new ArrayList<>();
		if (UserContextHolder.getContext().isAdmin()) {
			cocktails = (List<Cocktail>) cocktailRepository.findAll();
		} else {
			cocktails = getCocktailsByRoles();
		}
		cocktails = cocktails.stream().filter(c -> c.getArchiveFlag() == 0).collect(Collectors.toList());
		List<Brand> brands = brandRepository.findByDeletedIs(deletedId);
		List<Integer> brandPkIds = brands.stream().map(Brand :: getId).collect(Collectors.toList());
		cocktails = cocktails.stream().filter(new Predicate<Cocktail>() {
            @Override
            public boolean test(Cocktail c) {
            	if (brandPkIds.contains(c.getBrandPkId())) {
					return true;
				}
            	for (CocktailBrand cocktailBrand : c.getBrandSet()) {
            		if (brandPkIds.contains(cocktailBrand.getBrand().getId())) {
						return true;
					}
				}
            	return false;
            }}).collect(Collectors.toList());
		return cocktails.size();
	}
	
	public List<Cocktail> getCocktailsByRoles() {
		List<Cocktail> results = new ArrayList<>();
		long userId = UserContextHolder.getContext().getUserId();
		List<Integer> salesGroupIds = new ArrayList<>();
		List<Integer> supplierGroupIds = new ArrayList<>();
		List<Integer> salesGroupIdSet = new ArrayList<>();
		List<Integer> supplierGroupIdSet = new ArrayList<>();
		List<GroupUser> groupUsers = groupUserRepository.findByUserIdNaiveQuery(userId);
		groupUsers.forEach(gu -> {
			if (gu.getGroup().getType().equals(GroupType.sales)) {
				salesGroupIds.add(gu.getGroup().getPkId());
			} else {
				supplierGroupIds.add(gu.getGroup().getPkId());
			}
		});
		Set<String> roles = UserContextHolder.getContext().getAuthorities();
		List<String> isSales = new ArrayList<>();
		List<String> isMixologist = new ArrayList<>();
		List<String> isSupplier = new ArrayList<>();
		roles.forEach(r -> {
			if (r.contains("SALES") || r.contains("SALESADMIN") || r.contains("MANAGER")) {
				isSales.add(r);
			} else if (UserContextHolder.getContext().isMixologist()) {
				isMixologist.add(r);
			} else {
				isSupplier.add(r);
			}
		});
		if (salesGroupIds.size() > 0 && salesGroupIds != null) {
			salesGroupIds.forEach(s -> {
				salesGroupIdSet.addAll(findByGroupFromParents(s, null, new ArrayList<>()));
			});
		}
		if (supplierGroupIds.size() > 0 && supplierGroupIds != null) {
			supplierGroupIds.forEach(s -> {
				supplierGroupIdSet.addAll(findByGroupFromParents(s, null, new ArrayList<>()));
			});
		}
		if (isSales.size() > 0 && isSales != null) {
			if (salesGroupIdSet.size() > 0 && salesGroupIdSet != null) {
				results.addAll(cocktailRepository.findBySalesNativeQuery(salesGroupIdSet));
			}
		} else if (isMixologist.size() > 0 && isMixologist != null) {
			if (salesGroupIdSet.size() > 0 && salesGroupIdSet != null) {
				results.addAll(cocktailRepository.findBySalesMixologistNativeQuery(salesGroupIdSet,userId));
			} else if (supplierGroupIdSet.size() > 0 && supplierGroupIdSet != null) {
				results.addAll(cocktailRepository.findBySupplierMixologistNativeQuery(supplierGroupIds));
			}
		} else {
			if (supplierGroupIdSet.size() > 0 && supplierGroupIdSet != null) {
				results.addAll(cocktailRepository.findBySupplierNativeQuery(supplierGroupIdSet));
			}
		}
		
		return results;
	}
}
