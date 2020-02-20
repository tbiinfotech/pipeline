package com.liquidpresentaion.users.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.liquidpresentaion.users.exceptions.DuplicateEntityException;
import com.liquidpresentaion.users.model.Domain;
import com.liquidpresentaion.users.repository.DomainRepository;

@Service
public class DomainService {

	@Autowired
	private DomainRepository domainRepository;
	
	public Page<Domain> findAll(PageRequest pageRequet){
		return domainRepository.findAll(pageRequet);
	}

	public Page<Domain> findByNameContainingIgnoreCase(String name, PageRequest pageRequet){
		return domainRepository.findByNameContainingIgnoreCase(name, pageRequet);
	}
	
	public void saveDomain(Domain newDomain){
		this.validateDuplicate(newDomain.getName());
		domainRepository.save(newDomain);
	}
	
	public void updateDomain(Domain domain){
		this.validateDuplicate(domain.getName(), domain.getPkId());
		domainRepository.save(domain);
	}
	
	public void deleteDomain(Domain domain){
		domainRepository.delete(domain);
	}
	
	public Domain getDomain(int domainId){
		return domainRepository.findById(domainId).get();
	}
	
	private void validateDuplicate(String name){
		if (domainRepository.existsByNameIgnoreCase(name)) {
			throw new DuplicateEntityException("There already is a domain named '" + name + "'!");
		}
	}
	
	private void validateDuplicate(String name, int pkId){
		if (domainRepository.existsByNameIgnoreCaseAndPkIdIsNot(name, pkId)) {
			throw new DuplicateEntityException("There already is a domain named '" + name + "'!");
		}
	}
}
