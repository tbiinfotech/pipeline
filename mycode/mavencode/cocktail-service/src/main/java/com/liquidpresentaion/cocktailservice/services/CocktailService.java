package com.liquidpresentaion.cocktailservice.services;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.liquidpresentaion.cocktailservice.constants.Category;
import com.liquidpresentaion.cocktailservice.constants.FlavorProfile;
import com.liquidpresentaion.cocktailservice.constants.GlassStyle;
import com.liquidpresentaion.cocktailservice.constants.OutletType;
import com.liquidpresentaion.cocktailservice.constants.SeasonalThemed;
import com.liquidpresentaion.cocktailservice.context.UserContextHolder;
import com.liquidpresentaion.cocktailservice.model.Brand;
import com.liquidpresentaion.cocktailservice.model.BrandGroup;
import com.liquidpresentaion.cocktailservice.model.Cocktail;
import com.liquidpresentaion.cocktailservice.model.CocktailBrand;
import com.liquidpresentaion.cocktailservice.model.CocktailCategory;
import com.liquidpresentaion.cocktailservice.model.CocktailFlavorProfile;
import com.liquidpresentaion.cocktailservice.model.CocktailGlassStyle;
import com.liquidpresentaion.cocktailservice.model.CocktailGroup;
import com.liquidpresentaion.cocktailservice.model.CocktailOutletType;
import com.liquidpresentaion.cocktailservice.model.CocktailSeasonalThemed;
import com.liquidpresentaion.cocktailservice.model.Group;
import com.liquidpresentaion.cocktailservice.model.GroupUser;
import com.liquidpresentaion.cocktailservice.model.PresentationCocktail;
import com.liquidpresentaion.cocktailservice.repository.BrandGroupRepository;
import com.liquidpresentaion.cocktailservice.repository.BrandRepository;
import com.liquidpresentaion.cocktailservice.repository.CocktailGroupRepository;
import com.liquidpresentaion.cocktailservice.repository.CocktailRepository;
import com.liquidpresentaion.cocktailservice.repository.GroupRepository;
import com.liquidpresentaion.cocktailservice.repository.GroupUserRepository;
import com.liquidpresentaion.cocktailservice.repository.PresentationCocktailRepository;
import com.liquidpresentaion.cocktailservice.repository.PresentationRepository;
import com.liquidpresentaion.cocktailservice.specification.FilterCriteria;
import com.liquidpresentation.common.BaseSpiritCategory;
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
    private BrandGroupRepository brandGroupRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private PresentationRepository presentationRepository;

    @Autowired
    private PresentationCocktailRepository presentationCocktailRepository;

    @PersistenceContext
    private EntityManager entityManger;

    public Page<Cocktail> findAll(PageRequest pageRequet, String fromPage) {
        // return cocktailRepository.findAll(pageRequet);
        if (UserContextHolder.getContext().isAdmin()) {
            return this.filterCocktails(null, null, null, null, false, fromPage, pageRequet);
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
                        !UserContextHolder.getContext().isMixologist(), fromPage, pageRequet);
            }
        }
    }

    public Cocktail saveCocktail(Cocktail newCocktail) {
        List<CocktailGroup> cocktailGroups = cocktailGroupRepository.findByCocktailIdEquals(newCocktail.getId());
        List<Integer> Ids = new ArrayList<>();
        for (CocktailGroup group : cocktailGroups) {
            Ids.add(group.getGroupId());
        }
        List<Integer> presentationIds = new ArrayList<>();
        List<PresentationCocktail> presentationCocktail = presentationCocktailRepository.findByCocktailIdIs(newCocktail.getId());
        for (PresentationCocktail pc : presentationCocktail) {
            presentationIds.add(pc.getPresenttationId());
        }
        newCocktail.setGroups(IterableUtils.toList(groupRepository.findAllById(Ids)));
        newCocktail.setPresentations(IterableUtils.toList(presentationRepository.findAllById(presentationIds)));
        newCocktail.getCategorySet().forEach(x -> x.setCocktail(newCocktail));
        newCocktail.getFlavorProfileSet().forEach(x -> x.setCocktail(newCocktail));
        newCocktail.getOutletTypeSet().forEach(x -> x.setCocktail(newCocktail));
        newCocktail.getSeasonalThemedSet().forEach(x -> x.setCocktail(newCocktail));
        newCocktail.getGlassStypleSet().forEach(x -> x.setCocktail(newCocktail));
        // Set<CocktailBrand> cocktailBrand = new HashSet<>();
        // cocktailBrand = newCocktail.getBrandSet();
        // int a = 0;
        // for (CocktailBrand c : cocktailBrand) {
        // c.setIndex(a);
        // a++;
        // }
        newCocktail.getBrandSet().forEach(x -> x.setCocktail(newCocktail));
        newCocktail.setArchiveFlag(0);

        // TODO - add default group set based on mixologist roles
        // for (CocktailGroup iterable_element : newCocktail.getGroupSet()) {
        // iterable_element.setCocktail(newCocktail);
        // }
        Long userId = UserContextHolder.getContext().getUserId();
        if (newCocktail.getId() != 0) {
            newCocktail.setUpdatePkId(userId.intValue());
        } else {
            newCocktail.setCreatePkId(userId.intValue());
        }

        // List<CocktailGroup> cocktailGroups =
        // cocktailGroupRepository.findByCocktailId(newCocktail.getId());
        // cocktailGroups.forEach(res->{
        //
        // });
        Cocktail cocktail = cocktailRepository.save(newCocktail);
        List<CocktailGroup> accessGroups = new ArrayList<>();
        if (!UserContextHolder.getContext().isAdmin()) {
            if (UserContextHolder.getContext().isMixologist()) {
                List<Long> salesGroupIds = new ArrayList<>();
                List<Long> supplierGroupIds = new ArrayList<>();
                List<GroupUser> groupUsers = groupUserRepository.findByUserIdNaiveQuery(userId);
                groupUsers.forEach(gu -> {
                    if (gu.getGroup().getType().equals(GroupType.sales)) {
                        salesGroupIds.add((long) gu.getGroup().getPkId());
                    } else {
                        supplierGroupIds.add((long) gu.getGroup().getPkId());
                    }
                });
                if (salesGroupIds.size() > 0) {
                    for (Long groupId : salesGroupIds) {
                        if (!cocktailGroupRepository.existsByCocktailIdAndGroupId(cocktail.getId(),
                                groupId.intValue())) {
                            accessGroups.add(new CocktailGroup(cocktail.getId(), groupId.intValue(), null));
                        }
                    }
                } else {
                    Integer brandId = cocktail.getBrandPkId();
                    List<Long> groupIds = new ArrayList<>();
                    List<BrandGroup> brandGroups = brandGroupRepository.findByBdgBrandPkId(brandId);
                    brandGroups.forEach(b -> {
                        groupIds.add(b.getBdgGroupPkId());
                    });

                    for (Long groupId : groupIds) {
                        if (!cocktailGroupRepository.existsByCocktailIdAndGroupId(cocktail.getId(),
                                groupId.intValue())) {
                            accessGroups.add(new CocktailGroup(cocktail.getId(), groupId.intValue(), 1));
                        }
                    }
                }
            }
            // accessGroups.forEach(res->{
            if (accessGroups.size() > 0) {
                cocktailGroupRepository.saveAll(accessGroups);
            }
            // });

        }
        return cocktail;
    }

    public void updateCocktail(Cocktail cocktail) {
        Optional<Cocktail> opt = cocktailRepository.findById(cocktail.getId());
        if (opt.isPresent()) {
            Cocktail persist = opt.get();
            cocktail.setPublished(persist.isPublished());
            cocktailRepository.save(cocktail);
        }
    }

    public void deleteCocktail(Cocktail cocktail) {
        cocktailRepository.delete(cocktail);
        cocktailGroupRepository.deleteByCocktailId(cocktail.getId());
    }

    public Cocktail getCocktail(int cocktailId) {
        Cocktail cocktail = cocktailRepository.findById(cocktailId).get();
        List<CocktailBrand> l = new ArrayList<>();
        List<CocktailBrand> list = new ArrayList<CocktailBrand>(cocktail.getBrandSet());
        Set<CocktailBrand> cocktailBrand = new LinkedHashSet<>();
        Comparator<CocktailBrand> orderBy = Comparator.comparing(CocktailBrand::getIndex);
        l = list.stream().sorted(orderBy).collect(Collectors.toList());
        for (int i = 0; i < l.size(); i++) {
            cocktailBrand.add(l.get(i));
        }
        cocktail.setBrandSet(cocktailBrand);
        if (cocktail.getBrandPkId() != null) {
            Optional<Brand> optionalBrand = brandRepository.findById(cocktail.getBrandPkId());
            if (optionalBrand.isPresent()) {
                Brand brand = optionalBrand.get();
                cocktail.setDefaultImage(brand.getDefaultImage() == null ? "" : brand.getDefaultImage());
            }
        }
        return cocktail;
    }

    public Page<Cocktail> findByKeyword(String fromPage, String keyword, String filter, PageRequest pageRequet) {

        List<FilterCriteria> filterCriteria = new ArrayList<FilterCriteria>();
        if (filter != null) {
            filter = filter.replace(":,", ": ,");
            filter = filter.replace(": ,,", ":,");
            Pattern pattern = Pattern.compile("(.+?)(:|<|>)(.+?),");
            Matcher matcher = pattern.matcher(filter + ",");
            while (matcher.find()) {
                if (StringUtils.isNotBlank(matcher.group(3))) {
                    filterCriteria.add(new FilterCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
                }
            }
        }

        if (UserContextHolder.getContext().isAdmin()) {
            return this.filterCocktails(keyword, filterCriteria, null, null, false, fromPage, pageRequet);
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

    public void addAccessGroup(int cocktailId, Group group) {
        Optional<Cocktail> optional = cocktailRepository.findById(cocktailId);
        if (optional.isPresent()) {
            Cocktail cocktail = optional.get();
            cocktail.addAccessGroup(groupRepository.findById(group.getPkId()).get());
            cocktailRepository.save(cocktail);
        }
    }

    public void deleteAccessGroup(int cocktailId, Group group) {
        Optional<Cocktail> optional = cocktailRepository.findById(cocktailId);
        if (optional.isPresent()) {
            Cocktail cocktail = optional.get();
            cocktail.removeAccessGroup(group);
            cocktailRepository.save(cocktail);
        }
    }

    public void publishCocktail(int cocktailId, Cocktail pCocktail) {
        Optional<Cocktail> optional = cocktailRepository.findById(cocktailId);
        if (optional.isPresent()) {
            Cocktail cocktail = optional.get();
            cocktail.setPublished(pCocktail.isPublished());
            cocktailRepository.save(cocktail);
        }
    }

    public void duplicateCocktail(int cocktailId, Cocktail cocktail) {
        Optional<Cocktail> optional = cocktailRepository.findById(cocktailId);
        if (optional.isPresent()) {
            Cocktail persist = optional.get();
            Cocktail copycat = new Cocktail();

            BeanUtils.copyProperties(persist, copycat, "id");
            copycat.setName(cocktail.getName());
            copycat.setPublished(false);

            copycat.setCategorySet(persist.getCategorySet().stream().collect(Collectors.toSet()));
            copycat.setFlavorProfileSet(persist.getFlavorProfileSet().stream().collect(Collectors.toSet()));
            copycat.setOutletTypeSet(persist.getOutletTypeSet().stream().collect(Collectors.toSet()));
            copycat.setSeasonalThemedSet(persist.getSeasonalThemedSet().stream().collect(Collectors.toSet()));
            copycat.setGlassStypleSet(persist.getGlassStypleSet().stream().collect(Collectors.toSet()));
            copycat.setBrandSet(persist.getBrandSet().stream().collect(Collectors.toSet()));
            copycat.setGroups(new ArrayList<>());
            copycat.setPresentations(new ArrayList<>());

            this.saveCocktail(copycat);
        }
    }

    @SuppressWarnings("unchecked")
    private Page<Cocktail> filterCocktails(String keyword, List<FilterCriteria> filterCriteria,
                                           List<Integer> cocktailsIdListFromSalesGroups, List<Integer> supplierGroupIdList,
                                           boolean searchPublishedOnly, String fromPage, PageRequest pageRequet) {
        List<Cocktail> results = new ArrayList<>();
        List<Cocktail> resultList = new ArrayList<>();
        if (UserContextHolder.getContext().isAdmin()) {
            if (StringUtils.isNotBlank(keyword) || !CollectionUtils.isEmpty(filterCriteria)) {
                List<Map<String, String>> listOr = StringUtils.isNotBlank(keyword) ? splitKeyword(keyword) : null;
                String sqlQueryCriteria = getQueryCriteria(listOr, filterCriteria);
                resultList = entityManger.createNativeQuery("SELECT " + "ct.*, "
                        + "string_agg(cb.cb_brand_name, ',') as cb_brand_name, "
                        + "string_agg(cc.cc_category,',') as cc_category, "
                        + "string_agg(cs_seasonalthemed,',') as cs_seasonalthemed, "
                        + "string_agg(cf.cf_flavorprofile,',') as cf_flavorprofile, "
                        + "string_agg(cg.cg_glassstyle,',') as cg_glassstyle, "
                        + "string_agg(co.co_outlettype,',') as co_outlettype, "
                        + "string_agg(g.g_name,',') as g_name , "
                        + "string_agg ( cb_juice.cb_brand_name, ',') as ct_juice, "
                        + "string_agg ( cb_base.cb_brand_name, ',') as ct_base, "
                        + "string_agg ( cb_sweetener.cb_brand_name, ',') as ct_sweetener, "
                        + "string_agg ( cb_solids.cb_brand_name, ',') as cb_solids " + "FROM lp_cocktail ct "
                        + "left JOIN lp_cocktail_brand cb on ct.pk_id = cb.cb_cocktail_pkid "
                        + "LEFT JOIN lp_cocktail_category cc on ct.pk_id = cc.cc_cocktail_pkid "
                        + "LEFT JOIN lp_cocktail_seasonalthemed cs on ct.pk_id = cs.cs_cocktail_pkid "
                        + "LEFT JOIN lp_cocktail_flavorprofile cf  on ct.pk_id = cf.cf_cocktail_pkid "
                        + "LEFT JOIN lp_cocktail_glassstyle cg on ct.pk_id = cg.cg_cocktail_pkid "
                        + "LEFT JOIN lp_cocktail_outlettype co on ct.pk_id = co.co_cocktail_pkid "
                        + "LEFT JOIN lp_group g on ct.ct_supplier_pkid = g.pk_id "
                        + "LEFT JOIN (SELECT DISTINCT cb_brand_name,cb_cocktail_pkid,cb_category FROM lp_cocktail_brand WHERE cb_category = 'JUICE' ORDER BY cb_brand_name) cb_juice ON cb_juice.cb_cocktail_pkid = ct.pk_id "
                        + "LEFT JOIN (SELECT DISTINCT cb_brand_name,cb_cocktail_pkid,cb_category FROM lp_cocktail_brand WHERE cb_category = 'BASESPIRIT' ORDER BY cb_brand_name) cb_base ON cb_base.cb_cocktail_pkid = ct.pk_id "
                        + "LEFT JOIN (SELECT DISTINCT cb_brand_name,cb_cocktail_pkid,cb_category FROM lp_cocktail_brand WHERE cb_category = 'SWEETENER' ORDER BY cb_brand_name) cb_sweetener ON cb_sweetener.cb_cocktail_pkid = ct.pk_id "
                        + "LEFT JOIN (SELECT DISTINCT cb_brand_name,cb_cocktail_pkid,cb_category FROM lp_cocktail_brand WHERE cb_category = 'SOLIDS' ORDER BY cb_brand_name) cb_solids ON cb_solids.cb_cocktail_pkid = ct.pk_id "
                        + "WHERE " + sqlQueryCriteria, Cocktail.class).getResultList();
            } else {
                resultList = Lists.newArrayList(cocktailRepository.findAll());
            }
        } else {
            results = getCocktailsByRoles();
            if (keyword != null && !keyword.equals("")) {
                if (fromPage != null) {
                    Map<String, List<String>> keyMap = getkeyMap(keyword);
                    resultList = results.stream().filter(cocktailPredicate(keyMap, filterCriteria, fromPage))
                            .collect(Collectors.toList());
                } else {
                    resultList = results.stream()
                            .filter(cocktailPredicate(Lists.newArrayList(keyword), filterCriteria, fromPage))
                            .collect(Collectors.toList());
                }
            } else {
                resultList = results.stream()
                        .filter(cocktailPredicate(Lists.newArrayList(keyword), filterCriteria, fromPage))
                        .collect(Collectors.toList());
            }
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
        if (fromPage == null) {
            List<Brand> brands = brandRepository.findByDeletedIs(deletedId);
            List<Integer> brandPkIds = brands.stream().map(Brand::getId).collect(Collectors.toList());
            for (Cocktail cocktail : resultList) {
                Set<String> stringSet = new HashSet<>();
                if (brandPkIds.contains(cocktail.getBrandPkId())) {
                    stringSet.add(cocktail.getBrandName());
                }
                cocktail.getBrandSet().forEach(c -> {
                    if (brandPkIds.contains(c.getBrandPkId())) {
                        stringSet.add(c.getBrandName());
                    }
                });
                cocktail.setDeletedBrand(String.join(",", stringSet));
            }
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

    public Predicate<Cocktail> cocktailPredicate(List<String> keywords, List<FilterCriteria> filterCriteria,String fromPage) {
        return new Predicate<Cocktail>() {

            @Override
            public boolean test(Cocktail c) {
                SimpleDateFormat sf = new SimpleDateFormat("MM/dd/yyyy");
                String createDate = sf.format(c.getCreateDate());
                boolean match = false;
                if (CollectionUtils.isEmpty(keywords)) {
                    match = true;
                } else {
                    for (String keyword : keywords) {
                        match = getCocktailMatch(c, keyword, fromPage, createDate);
                        if (match) {
                            break;
                        }
                    }
                }

                if (filterCriteria != null && !filterCriteria.isEmpty()) {
                    match = getFilterCriteriaMatch(c, filterCriteria, match);
                }

                return match;
            }
        };
    }
    public Predicate<Cocktail> cocktailPredicate(Map<String,List<String>> keyMap, List<FilterCriteria> filterCriteria,String fromPage) {
        return new Predicate<Cocktail>() {

            @Override
            public boolean test(Cocktail c) {
                SimpleDateFormat sf = new SimpleDateFormat("MM/dd/yyyy");
                String createDate = sf.format(c.getCreateDate());
                boolean match = false;
                if (CollectionUtils.isEmpty(keyMap.get("and")) && CollectionUtils.isEmpty(keyMap.get("or"))) {
                    match = true;
                } else {
                    boolean andMatch = true;
                    boolean orMatch = false;
                    if (!CollectionUtils.isEmpty(keyMap.get("and"))) {
                        for (String keyWord : keyMap.get("and")) {
                            if(!getCocktailMatch(c, keyWord, fromPage, createDate)) {
                                andMatch = false;
                                break;
                            }
                        }
                    }
                    if (andMatch && !CollectionUtils.isEmpty(keyMap.get("or"))) {
                        for (String keyWord : keyMap.get("or")) {
                            if(getCocktailMatch(c, keyWord, fromPage, createDate)) {
                                orMatch = true;
                                break;
                            }
                        }
                    } else {
                        orMatch = true;
                    }
                    if (andMatch && orMatch) {
                        match = true;
                    }
                }

                if (filterCriteria != null && !filterCriteria.isEmpty()) {
                    match = getFilterCriteriaMatch(c, filterCriteria, match);
                }

                return match;
            }
        };
    }

    public boolean getCocktailMatch (Cocktail c, String keyWord, String fromPage, String createDate) {
        boolean match = false;
        String regex = StringUtil.isNotEmpty(keyWord) ? getRegex(keyWord) : "";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        if (fromPage != null) {
            match = StringUtil.isAllEmpty(keyWord) ? true
                    : pattern.matcher(c.getDegreeOfDiff() + "").find()
                    || pattern.matcher(c.getIngredientsnumber() + "").find() // Number of ingredients
                    || pattern.matcher(c.getBaseSpiritCategory().name()).find()
                    || pattern.matcher(c.getName() == null ? "" : c.getName()).find()
                    || pattern.matcher(c.getBrandName() == null ? "" : c.getBrandName()).find()
                    || pattern.matcher(c.getSupplierName() == null ? "" : c.getSupplierName()).find()
                    || pattern.matcher(c.getBaseSpirit()).find()
                    || pattern.matcher(c.getJuice()).find()
                    || pattern.matcher(c.getSweetener()).find()
                    || pattern.matcher(c.getSolids()).find()

                    || c.getCategorySet().stream().anyMatch(cocktailCategorySetPredicate(regex))
                    || c.getSeasonalThemedSet().stream().anyMatch(seasonalThemedSetPredicate(regex))
                    || c.getFlavorProfileSet().stream().anyMatch(flavorProfileSetPredicate(regex))
                    || c.getGlassStypleSet().stream().anyMatch(glassStyleSetPredicate(regex))
                    || c.getOutletTypeSet().stream().anyMatch(outletTypeSetPredicate(regex));
        } else {
            match = StringUtil.isAllEmpty(keyWord) ? true
                    : pattern.matcher(c.getName() == null ? "" : c.getName()).find()
                    || pattern.matcher(c.getBrandName() == null ? "" : c.getBrandName()).find()
                    || pattern.matcher(c.getSupplierName() == null ? "" : c.getSupplierName()).find()
                    || pattern.matcher(createDate == null ? "" : createDate).find()
                    || (StringUtil.isAllEmpty(c.getMixologistName()) ? false
                    : pattern.matcher(c.getMixologistName()).find());
        }

        return match;

    }
    public boolean getFilterCriteriaMatch (Cocktail c, List<FilterCriteria> filterCriteria, boolean match) {
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
//				match = match && c.getBaseSpiritCategory().toString().toUpperCase().contains(criteriaValue); // old strategy : illegibility matching
                    match = match && c.getBaseSpiritCategory() == null ? false : c.getBaseSpiritCategory().toString().toUpperCase().equals(criteriaValue); // change query strategy to perfect matching
                    break;
                case NameOfCocktail:
                    match = match && c.getName().toUpperCase().contains(criteriaValue);
                    break;
                case Brand:
                    match = match && StringUtils.isEmpty(c.getBrandName()) ? false : c.getBrandName().toUpperCase().contains((criteriaValue));
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
//				match = match && c.getCategorySet().stream().anyMatch(cocktailCategorySetPredicate(criteriaValue)); // old strategy : illegibility matching
                    match = match && c.getCategorySet().stream().anyMatch(cocktailCategorySetPredicate4PerfectMatching(criteriaValue)); // change query strategy to perfect matching
                    break;
                case SeasonalThemed:
//				match = match && c.getSeasonalThemedSet().stream().anyMatch(seasonalThemedSetPredicate(criteriaValue)); // old strategy : illegibility matching
                    match = match && c.getSeasonalThemedSet().stream().anyMatch(seasonalThemedSetPredicate4PerfectMatching(criteriaValue)); // change query strategy to perfect matching
                    break;
                case FlavorProfile:
//				match = match && c.getFlavorProfileSet().stream().anyMatch(flavorProfileSetPredicate(criteriaValue)); // old strategy : illegibility matching
                    match = match && c.getFlavorProfileSet().stream().anyMatch(flavorProfileSetPredicate4PerfectMatching(criteriaValue)); // change query strategy to perfect matching
                    break;
                case CocktailGlassStyle:
//				match = match && c.getGlassStypleSet().stream().anyMatch(glassStyleSetPredicate(criteriaValue)); // old strategy : illegibility matching
                    match = match && c.getGlassStypleSet().stream().anyMatch(glassStyleSetPredicate4PerfectMatching(criteriaValue)); // change query strategy to perfect matching
                    break;
                case OutletType:
//				match = match && c.getOutletTypeSet().stream().anyMatch(outletTypeSetPredicate(criteriaValue)); // old strategy : illegibility matching
                    match = match && c.getOutletTypeSet().stream().anyMatch(outletTypeSetPredicate4PerfectMatching(criteriaValue)); // change query strategy to perfect matching
                    break;
                default:
                    break;
            }
        }
        return match;
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
                if (StringUtils.isBlank(queryCondition)) { // if query condition is null(include space), get all
                    conditionMatch = true;
                } else { // otherwise
                    Category category = cocktailCategory.getCategory(); // if category is null, no data matched
                    if (category != null) {
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
                if (StringUtils.isBlank(queryCondition)) { // if query condition is null(include space), get all
                    conditionMatch = true;
                } else { // otherwise
                    SeasonalThemed st = seasonalThemed.getSeasonalThemed(); // if SeasonalThemed is null, no data matched
                    if (st != null) {
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
                if (StringUtils.isBlank(queryCondition)) { // if query condition is null(include space), get all
                    conditionMatch = true;
                } else { // otherwise
                    FlavorProfile fp = flavorProfile.getFlavorProfile(); // if FlavorProfile is null, no data matched
                    if (fp != null) {
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
                if (StringUtils.isBlank(queryCondition)) { // if query condition is null(include space), get all
                    conditionMatch = true;
                } else { // otherwise
                    GlassStyle gs = glassStyle.getGlassStyle(); // if GlassStyle is null, no data matched
                    if (gs != null) {
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
                if (StringUtils.isBlank(queryCondition)) { // if query condition is null(include space), get all
                    conditionMatch = true;
                } else { // otherwise
                    OutletType ot = outletType.getOutletType(); // if OutletType is null, no data matched
                    if (ot != null) {
                        String otStrUpper = ot.toString().toUpperCase();
                        conditionMatch = otStrUpper.equals(queryCondition.toUpperCase()); // case insensitive equals() check for perfect matching
                    }
                }
                return conditionMatch;
            }
        };
    }

    public boolean validateCocktail(Integer cocktailId, String cocktailName) {
        if (cocktailId != null) {
            Optional<Cocktail> cocktail = cocktailRepository.findById(cocktailId);
            String name = cocktail.get().getName();
            Cocktail cName = cocktailRepository.findByName(cocktailName);
            if (cName != null) {
                if (cName.getName().equals(name)) {
                    return true;
                }
                return false;
            }
            return true;
        } else {
            Cocktail cocktail = cocktailRepository.findByName(cocktailName);
            if (cocktail != null) {
                return false;
            } else {
                return true;
            }
        }
    }

    @Transactional
    public int editCocktailWithDeletedBrand(Cocktail newCocktail) {
        try {
            int cocktailPkId = newCocktail.getId();
            newCocktail.setId(0);
            Cocktail cocktail = saveCocktail(newCocktail);
            if (cocktail != null) {
                Optional<Cocktail> opt = cocktailRepository.findById(cocktailPkId);
                if (opt.isPresent()) {
                    Cocktail persist = opt.get();
                    persist.setArchiveFlag(1);
                    cocktailRepository.save(persist);
                }
            }
            return cocktail.getId();
        } catch (Exception ex) {
            throw new RuntimeException();
        }
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
        List<Integer> brandPkIds = brands.stream().map(Brand::getId).collect(Collectors.toList());
        cocktails = cocktails.stream().filter(new Predicate<Cocktail>() {
            @Override
            public boolean test(Cocktail c) {
                if (brandPkIds.contains(c.getBrandPkId())) {
                    return true;
                }
                if (!CollectionUtils.isEmpty(c.getBrandSet())) {
                    for (CocktailBrand cocktailBrand : c.getBrandSet()) {
                        if (cocktailBrand.getBrand() != null) {
                            if (brandPkIds.contains(cocktailBrand.getBrand().getId())) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        }).collect(Collectors.toList());
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
                results.addAll(cocktailRepository.findBySalesMixologistNativeQuery(salesGroupIdSet, userId));
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

    public Page<Cocktail> getCocktailList(PageRequest pageRequet) {
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
                    !UserContextHolder.getContext().isMixologist(), null, pageRequet);
        }
    }

    private List<String> splitKeywordByRoles(String keyword) {
        String key1 = keyword.trim();
        String[] result11 = key1.split(" ");
        List<String> list = new ArrayList<>();
        String str11 = "";
        Boolean flg11 = false;
        for (int i = 0; i <= result11.length - 1; i++) {
            if (!result11[i].isEmpty() || !result11[i].equals("")) {
                if (result11[i].indexOf("\"") > -1) {
                    if (result11[i].startsWith("\"") && result11[i].endsWith("\"")) {
                        list.add(result11[i].replaceAll("\"", ""));
                        continue;
                    }
                    if (!flg11) {
                        str11 = result11[i].replaceAll("\"", "");
                        ;
                        flg11 = true;
                        continue;
                    } else {
                        str11 = str11 + " " + result11[i].replaceAll("\"", "");
                        ;
                        list.add(str11);
                        flg11 = false;
                        continue;
                    }
                }
                if (flg11) {
                    str11 = str11 + " " + result11[i];
                    continue;
                }
                list.add(result11[i]);
            }
        }

        List<String> listOr = new ArrayList<>();

        StringBuffer str1 = new StringBuffer();
        if (!list.get(0).equals("or")) {
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
                    if (!"or".equals(list.get(i)) && i == list.size() - 1) {
                        listOr.add(str1.toString());
                        break;
                    }

                }
            }
        } else {
            listOr.add(str1.toString());
            str1 = new StringBuffer();
        }
        return listOr;
    }

    private List<Map<String, String>> splitKeyword(String keyword) {
        String key1 = keyword.trim();
        String[] result11 = key1.split(" ");
        List<String> list = new ArrayList<>();
        String str11 = "";
        Boolean flg11 = false;
        for (int i = 0; i <= result11.length - 1; i++) {
            if (!result11[i].isEmpty() || !result11[i].equals("")) {
                if (result11[i].indexOf("\"") > -1) {
                    if (result11[i].startsWith("\"") && result11[i].endsWith("\"")) {
                        list.add(result11[i].replaceAll("\"", ""));
                        continue;
                    }
                    if (!flg11) {
                        str11 = result11[i].replaceAll("\"", "");
                        ;
                        flg11 = true;
                        continue;
                    } else {
                        str11 = str11 + " " + result11[i].replaceAll("\"", "");
                        ;
                        list.add(str11);
                        flg11 = false;
                        continue;
                    }
                }
                if (flg11) {
                    str11 = str11 + " " + result11[i];
                    continue;
                }
                list.add(result11[i]);
            }
        }

        StringBuffer str1 = new StringBuffer();
        List<Map<String, String>> resultList = new ArrayList<>();
        String type = "and";

        Map<String, String> resultMap = new HashMap<String, String>();
        if (!list.get(0).equalsIgnoreCase("or") && !list.get(0).equalsIgnoreCase("and")) {
            resultMap.put("key", list.get(0));
            resultMap.put("type", type);
            resultList.add(resultMap);
        }
        for (int i = 1; i < list.size(); i++) {
            Map<String, String> tempMap = new HashMap<String, String>();
            if (!list.get(i).equalsIgnoreCase("or") && !list.get(i).equalsIgnoreCase("and")) {
                str1.append(list.get(i));
                tempMap.put("key", list.get(i));
                tempMap.put("type", type);
                resultList.add(tempMap);
                continue;
            } else if (list.get(i).equalsIgnoreCase("or")) {
                type = "or";
            } else if (list.get(0).equalsIgnoreCase("and")) {
                type = "and";
            }
        }
        return resultList;
		/*Boolean flg = false;
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
		return listOr;*/
    }

    private String getQueryCriteria(List<Map<String, String>> listOr, List<FilterCriteria> filterCriteria) {
        StringBuffer queryCriteria = new StringBuffer();
        if (!CollectionUtils.isEmpty(listOr)) {
            for (int i = 0; i < listOr.size(); i++) {
                String upperStr = listOr.get(i).get("key").toUpperCase();
                if (i != 0) {
                    queryCriteria.append(listOr.get(i).get("type"));
                }
                queryCriteria.append(" ( UPPER(ct_name) like '%" + upperStr + "%'");

                if (StringUtils.isNumeric(upperStr)) {
                    queryCriteria.append(" or ct.ct_diff_degree = " + Integer.valueOf(upperStr));
                    queryCriteria.append(" or ct.ct_ingredientsnumber = " + Integer.valueOf(upperStr));
                }

                queryCriteria.append(" or UPPER(ct.ct_brand_name) like '%" + upperStr + "%'");
                queryCriteria.append(" or UPPER(ct.ct_base_spirit_category) like '%" + upperStr + "%'");
                queryCriteria.append(" or UPPER(cb.cb_brand_name) like '%" + upperStr + "%'");
                queryCriteria.append(" or UPPER(cc.cc_category) like '%" + upperStr + "%'");
                queryCriteria.append(" or UPPER(cf.cf_flavorprofile)  like '%" + upperStr + "%'");
                queryCriteria.append(" or UPPER(cs.cs_seasonalthemed) like '%" + upperStr + "%'");
                queryCriteria.append(" or UPPER(cg.cg_glassstyle) like '%" + upperStr + "%'");
                queryCriteria.append(" or UPPER(co.co_outlettype) like '%" + upperStr + "%'");
                queryCriteria.append(" or strpos( UPPER(g.g_name), '" + upperStr + "') >0 ) ");
            }
        }
        if (!CollectionUtils.isEmpty(filterCriteria)) {
            for (FilterCriteria criteria : filterCriteria) {
                if (!queryCriteria.toString().equals("")) {
                    queryCriteria.append(" and ");
                }
                String criteriaValue = (criteria.getValue() + "").toUpperCase();
                String upperStr = criteriaValue.toUpperCase();
                switch (criteria.getKey()) {
                    case DegreeOfDifficulty:
                        queryCriteria.append(" ct.ct_diff_degree IN (" + criteriaValue.replaceAll("_", ",") + ")");
                        break;
                    case NumberOfIngredients:
                        queryCriteria.append(" ct.ct_ingredientsnumber = " + criteriaValue);
                        break;
                    case BaseSpiritCategory:
                        queryCriteria.append(" ct.ct_base_spirit_category = \'" + BaseSpiritCategory.getBaseSpiritCategory(criteria.getValue().toString()) + "\'");
                        break;
                    case NameOfCocktail:
                        queryCriteria.append(" UPPER(ct_name) like '%" + upperStr + "%'");
                        break;
                    case Brand:
                        queryCriteria.append(" UPPER(ct.ct_brand_name) like '%" + upperStr + "%'");
                        break;
                    case Supplier:
                        queryCriteria.append(" UPPER(g.g_name) like '%" + upperStr + "%'");
                        break;
                    case BaseSpiritModifier:
                        queryCriteria.append(" UPPER(cb_base.cb_brand_name) like '%" + upperStr + "%'");
                        break;
                    case JuiceLiquids:
                        queryCriteria.append(" UPPER(cb_juice.cb_brand_name) like '%" + upperStr + "%'");
                        break;
                    case Sweetener:
                        queryCriteria.append(" UPPER(cb_sweetener.cb_brand_name) like '%" + upperStr + "%'");
                        break;
                    case Solids:
                        queryCriteria.append(" UPPER(cb_solids.cb_brand_name) like '%" + upperStr + "%'");
                        break;
                    case CocktailCategory:
                        queryCriteria.append(" cc.cc_category = \'" + Category.getCategory(criteria.getValue().toString()) + "\'");
                        break;
                    case SeasonalThemed:
                        queryCriteria.append(" cs.cs_seasonalthemed = \'" + SeasonalThemed.getSeasonalThemed(criteria.getValue().toString()) + "\'");
                        break;
                    case FlavorProfile:
                        queryCriteria.append(" cf.cf_flavorprofile = \'" + FlavorProfile.getFlavorProfile(criteria.getValue().toString()) + "\'");
                        break;
                    case CocktailGlassStyle:
                        queryCriteria.append(" cg.cg_glassstyle = \'" + GlassStyle.getGlassStyle(criteria.getValue().toString()) + "\'");
                        break;
                    case OutletType:
                        queryCriteria.append(" co.co_outlettype = \'" + OutletType.getOutletType(criteria.getValue().toString()) + "\'");
                        break;
                    default:
                        break;
                }
            }
        }
        queryCriteria.append(" GROUP BY ct.pk_id ");
        return queryCriteria.toString();
    }

    public List<String> getUnUserCocktailImages() throws IOException {
        List<String> resultList = new ArrayList<>();
//		  List<Cocktail> cocktailsList = entityManger.createNativeQuery("SELECT * FROM lp_cocktail",Cocktail.class).getResultList();
        List<Cocktail> cocktailsList = (List<Cocktail>) cocktailRepository.findAll();
        List<String> imageList = cocktailsList.stream().map(Cocktail::getImage).collect(Collectors.toList());
        for (String image : imageList) {
            if (StringUtils.isAllBlank(image)) {
                continue;
            }
            Integer RESPONSE_CODE = 0;
            URL url = new URL(image);
            HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
            RESPONSE_CODE = urlcon.getResponseCode();
            if (RESPONSE_CODE == HttpURLConnection.HTTP_OK) {
                continue;
            } else {
                System.out.println(image);
                resultList.add(image);
            }
        }
        return resultList;
    }

    private Map<String, List<String>> getkeyMap(String keyword) {
        Map<String, List<String>> result = new HashMap<>();
        if (StringUtils.isEmpty(keyword)) {
            return result;
        }
        result.put("and", new ArrayList<>());
        result.put("or", new ArrayList<>());
        String key1 = keyword.trim();
        String[] result11 = key1.split(" ");
        List<String> list = new ArrayList<>();
        String str11 = "";
        Boolean flg11 = false;
        for (int i = 0; i <= result11.length - 1; i++) {
            if (!result11[i].isEmpty() || !result11[i].equals("")) {
                if (result11[i].indexOf("\"") > -1) {
                    if (result11[i].startsWith("\"") && result11[i].endsWith("\"")) {
                        list.add(result11[i].replaceAll("\"", ""));
                        continue;
                    }
                    if (!flg11) {
                        str11 = result11[i].replaceAll("\"", "");
                        ;
                        flg11 = true;
                        continue;
                    } else {
                        str11 = str11 + " " + result11[i].replaceAll("\"", "");
                        ;
                        list.add(str11);
                        flg11 = false;
                        continue;
                    }
                }
                if (flg11) {
                    str11 = str11 + " " + result11[i];
                    continue;
                }
                list.add(result11[i]);
            }
        }
        for (int i = 0; i < list.size(); i++) {
            String bfStr = "";
            String afStr = "";
            String str = list.get(i);
            if (i != 0) {
                bfStr = list.get(i - 1);
            }
            if (i != list.size() - 1) {
                afStr = list.get(i + 1);
            }
            if (!str.equalsIgnoreCase("or") && !str.equalsIgnoreCase("and")) {
                if (bfStr.equalsIgnoreCase("or") || afStr.equalsIgnoreCase("or")) {
                    result.get("or").add(str);
                } else {
                    result.get("and").add(str);
                }
            }
        }
        return result;
    }
}
