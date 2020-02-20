package com.liquidpresentaion.managementservice.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.liquidpresentaion.managementservice.model.Settings;

@Repository
public interface SettingsRepository extends CrudRepository<Settings, Integer>{

}
