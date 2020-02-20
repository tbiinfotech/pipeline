package com.liquidpresentaion.users.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.liquidpresentaion.users.model.Client;
import com.liquidpresentaion.users.services.ClientService;
import com.liquidpresentation.common.utils.PageUtil;

@RestController
@RequestMapping("internal/v1/clients")
public class ClientServiceController {

	@Autowired
	private ClientService clientService;
	
	@RequestMapping(method = RequestMethod.GET)
	public Page<Client> getClients(@RequestParam(name = "name", defaultValue = "")String name,
									@RequestParam(name = "page", defaultValue = "0") int page, 
									@RequestParam(name = "size", defaultValue = "25") int size,
									@RequestParam(name = "property", defaultValue = "name") String property,
									@RequestParam(name = "asc", defaultValue = "true") boolean asc){
		
		PageRequest pageRequest = PageUtil.buildPageRequest(page, size, property, asc);
		if (name != null) {
			name = name.trim();
		}
		if ("".equals(name)) {
			return clientService.findAll(pageRequest);
		} else {
			return clientService.findByNameContainingIgnoreCase(name, pageRequest);
		}
	}
	
	@RequestMapping(value = "/{clientId}", method = RequestMethod.GET)
	public Client getClient(@PathVariable("clientId") int clientId){
		return clientService.getClient(clientId);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public void saveClient(@RequestBody Client client){
		clientService.saveClient(client);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public void updateClient(@RequestBody Client client){
		clientService.updateClient(client);
	}
	
	@RequestMapping(method = RequestMethod.DELETE)
	public void deleteClient(@RequestBody Client client){
		clientService.deleteClient(client);
	}
}
