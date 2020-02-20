package com.liquidpresentaion.users.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.xmlbeans.impl.xb.xsdschema.impl.PublicImpl;
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

import com.liquidpresentaion.users.model.Distributor;
import com.liquidpresentaion.users.model.DistributorCategory;
import com.liquidpresentaion.users.model.Group;
import com.liquidpresentaion.users.model.SupplierDistributor;
import com.liquidpresentaion.users.services.DistributorService;
import com.liquidpresentation.common.utils.PageUtil;

@RestController
@RequestMapping("internal/v1/distributors")
public class DistributorServiceController {

	@Autowired
	private DistributorService distributorService;

	@RequestMapping(method = RequestMethod.GET)
	public Page<Distributor> getDistributors(@RequestParam(name = "name", defaultValue = "") String name, @RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "25") int size, @RequestParam(name = "property", defaultValue = "name") String property,
			@RequestParam(name = "asc", defaultValue = "true") boolean asc) {
		
		PageRequest pageRequest = PageUtil.buildPageRequest(page, size, property, asc);
		 Page<Distributor> resultsPage;
		 if (name != null) {
				name = name.trim();
			}
		if ("".equals(name)) {
			 resultsPage = distributorService.findAll(pageRequest);
		} else {
			 resultsPage = distributorService.findByNameContainingIgnoreCase(name, pageRequest);
		}
		 return new PageImpl<>(resultsPage.stream().map(p -> new Distributor(p.getId(), p.getName(), p.getTopGroups())).collect(Collectors.toList()), resultsPage.getPageable(),resultsPage.getTotalElements());
	}

	@RequestMapping(value = "/{distributorId}", method = RequestMethod.GET)
	public Distributor getDistributor(@PathVariable("distributorId") int distributorId) {
		return distributorService.getDistributor(distributorId);
	}

	@RequestMapping(method = RequestMethod.POST)
	public void saveDistributor(@RequestBody Distributor distributor) {
		distributorService.saveDistributor(distributor);
	}

	@RequestMapping(method = RequestMethod.PUT)
	public void updateDistributor(@RequestBody Distributor distributor) {
		distributorService.updateDistributor(distributor);
	}

	@RequestMapping(method = RequestMethod.DELETE)
	public void deleteDistributor(@RequestBody Distributor distributor) {
		distributorService.deleteDistributor(distributor);
	}

	@RequestMapping(value = "/{distributorId}/category", method = RequestMethod.POST)
	public void addDistributorCategory(@PathVariable("distributorId") int distributorId, @RequestBody DistributorCategory distributorCategory) {
		distributorService.addDistributorCategory(distributorId, distributorCategory);
	}

	@RequestMapping(value = "/{distributorId}/category", method = RequestMethod.DELETE)
	public void removeDistributorCategory(@PathVariable("distributorId") int distributorId, @RequestBody DistributorCategory distributorCategory) {
		distributorService.removeDistributorCategory(distributorId, distributorCategory);
	}

	@RequestMapping(value = "/{distributorId}/group", method = RequestMethod.POST)
	public void addTopGroup(@PathVariable("distributorId") int distributorId, @RequestBody Group salesGroup) {
		distributorService.addTopGroup(distributorId, salesGroup);
	}

	@RequestMapping(value = "/{distributorId}/group", method = RequestMethod.DELETE)
	public void removeTopGroup(@PathVariable("distributorId") int distributorId, @RequestBody Group salesGroup) {
		distributorService.removeTopGroup(distributorId, salesGroup);
	}
	
	@RequestMapping(value = "/{distributorId}/supplier", method = RequestMethod.POST) 
	public void addDistributorSupplier(@PathVariable("distributorId") int distributorId,@RequestBody SupplierDistributor supplierDistributor) {
		distributorService.addDistributorSupplier(distributorId, supplierDistributor);
	}
	
	@RequestMapping(value = "/{distributorId}/supplier", method = RequestMethod.DELETE)
	public void removeDistributorSupplier(@PathVariable("distributorId") int distributorId,@RequestParam("pkId") Integer pkId) {
		distributorService.removeDistributorSupplier(pkId);
	}
	
	@RequestMapping(value = "/{distributorId}/supplier", method = RequestMethod.GET)
	public List<SupplierDistributor> getDistributorSupplier(@PathVariable("distributorId") int distributorId) {
		return distributorService.getDistributorSupplier(distributorId);
	}
	
	@RequestMapping(value = "/{distributorId}/supplier/exists", method = RequestMethod.GET)
	public Integer existDistributorIdAndDisTributorSupplierId(@PathVariable("distributorId") Integer distributorId,@RequestParam("distributorSupplierId") String distributorSupplierId) {
		return distributorService.existsByDistributorPkIdAndDistSupplierId(distributorId,distributorSupplierId);
	}
}
