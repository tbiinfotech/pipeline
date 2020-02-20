package com.liquidpresentaion.users.controllers;

import java.util.Comparator;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.liquidpresentaion.users.model.Domain;
import com.liquidpresentaion.users.services.DomainService;
import com.liquidpresentation.common.utils.PageUtil;

@RestController
@RequestMapping("internal/v1/domains")
public class DomainServiceController {

	@Autowired
	private DomainService domainService;
	
	@RequestMapping(method = RequestMethod.GET)
	public Page<Domain> getDomains(@RequestParam(name = "name", defaultValue = "")String name,
									@RequestParam(name = "page", defaultValue = "0") int page, 
									@RequestParam(name = "size", defaultValue = "25") int size,
									@RequestParam(name = "property", defaultValue = "name") String property,
									@RequestParam(name = "asc", defaultValue = "true") boolean asc){
		
		PageRequest pageRequest;
		if (name != null) {
			name = name.trim();
		}
		if("name".equals(property)) {
			pageRequest = PageUtil.buildPageRequest(page, size);
		}else {
			pageRequest = PageUtil.buildPageRequest(page, size, property, asc);
		}
		Page<Domain> pageResult ;		
		if ("".equals(name)) {
			pageResult = domainService.findAll(pageRequest);
		} else {
			pageResult = domainService.findByNameContainingIgnoreCase(name, pageRequest);
		}
		if("name".equals(property)) {
			Comparator<Domain> byName = Comparator.comparing(Domain::getName);
			if(asc) {
				pageResult = new PageImpl<>(pageResult.getContent().stream().sorted(byName).collect(Collectors.toList()), pageResult.getPageable(),pageResult.getTotalElements());
			}else {
				pageResult = new PageImpl<>(pageResult.getContent().stream().sorted(byName.reversed()).collect(Collectors.toList()), pageResult.getPageable(),pageResult.getTotalElements());
			}
		}
		return pageResult;
	}
	
	@RequestMapping(value = "/{domainId}", method = RequestMethod.GET)
	public Domain getDomain(@PathVariable("domainId") int domainId){
		return domainService.getDomain(domainId);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public void saveDomain(@Valid @RequestBody Domain domain){
		domainService.saveDomain(domain);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public void updateDomain(@Valid @RequestBody Domain domain){
		domainService.updateDomain(domain);
	}
	
	@RequestMapping(method = RequestMethod.DELETE)
	public void deleteDomain(@RequestBody Domain domain){
		domainService.deleteDomain(domain);
	}
}
