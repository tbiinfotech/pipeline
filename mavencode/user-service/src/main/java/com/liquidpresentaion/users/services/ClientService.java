package com.liquidpresentaion.users.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.liquidpresentaion.users.exceptions.DuplicateEntityException;
import com.liquidpresentaion.users.model.Client;
import com.liquidpresentaion.users.repository.ClientRepository;

@Service
public class ClientService {

	@Autowired
	private ClientRepository clientRepository;
	
	public Page<Client> findAll(PageRequest pageRequet){
		return clientRepository.findAll(pageRequet);
	}

	public Page<Client> findByNameContainingIgnoreCase(String name, PageRequest pageRequet){
		return clientRepository.findByNameContainingIgnoreCase(name, pageRequet);
	}
	
	public void saveClient(Client newClient){
		this.validateDuplicate(newClient.getName());
		clientRepository.save(newClient);
	}
	
	public void updateClient(Client client){
		this.validateDuplicate(client.getName(), client.getId());
		clientRepository.save(client);
	}
	
	public void deleteClient(Client client){
		clientRepository.delete(client);
	}
	
	public Client getClient(int clientId){
		return clientRepository.findById(clientId).get();
	}
	
	private void validateDuplicate(String name){
		if (clientRepository.existsByName(name)) {
			throw new DuplicateEntityException("There already is a client named '" + name + "'!");
		}
	}
	
	private void validateDuplicate(String name, int id){
		if (clientRepository.existsByNameAndIdIsNot(name, id)) {
			throw new DuplicateEntityException("There already is a client named '" + name + "'!");
		}
	}
}
