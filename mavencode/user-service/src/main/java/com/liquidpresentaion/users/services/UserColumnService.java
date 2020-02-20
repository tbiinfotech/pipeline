package com.liquidpresentaion.users.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.liquidpresentaion.users.model.UserColumn;
import com.liquidpresentaion.users.repository.UserColumnRepository;

@Service
public class UserColumnService {

	@Autowired
	private UserColumnRepository userColumnRepository;

	public List<UserColumn> findByUserPkid(int userId) {
		List<UserColumn> userColumns = this.userColumnRepository.findByUserPkidOrderByColumnIndexAsc(userId);
		if (CollectionUtils.isEmpty(userColumns)) {
			this.saveDefaults(userId);
		}
		return this.userColumnRepository.findByUserPkidOrderByColumnIndexAsc(userId);
	}

	public void addUserColumn(UserColumn userColumn) {
		List<UserColumn> columns = this.findByUserPkid(userColumn.getUserPkid());
		columns.forEach(c->{
			if (c.getColumnName().equals(userColumn.getColumnName())) {
				return;
			}
		});
		int maxIndex = columns.get(columns.size()-1).getColumnIndex();
		if (userColumn.getColumnIndex() == null) {
			userColumn.setColumnIndex(maxIndex + 1);
			this.userColumnRepository.save(userColumn);
		} else {
			int index = userColumn.getColumnIndex();
			for ( ;index <= maxIndex; index++) {
				columns.get(index).setColumnIndex(index+1);
			}
			//update
			for (UserColumn u : columns) {
				this.userColumnRepository.save(u);
			}
			//add
			this.userColumnRepository.save(userColumn);
		}
	}

	public void removeUserColumn(UserColumn userColumn) {
		Optional<UserColumn> userIndex = this.userColumnRepository.findById(userColumn.getPkId());
		if (!userIndex.isPresent()) {
			return;
		}
		int index = userIndex.get().getColumnIndex();
		int userPkId = userIndex.get().getUserPkid();
		this.userColumnRepository.delete(userColumn);
		List<UserColumn> columns = this.findByUserPkid(userPkId);
		int maxIndex = columns.get(columns.size()-1).getColumnIndex();
		for (;index < maxIndex; index++) {
			columns.get(index).setColumnIndex(index);
		}
		for (UserColumn u : columns) {
			this.userColumnRepository.save(u);
		}
	}

	@Transactional
	public void resetDefault(int userId) {
		this.userColumnRepository.deleteByUserPkid(userId);
		this.saveDefaults(userId);
	}
	
	public void saveDefaults(int userId){
		List<UserColumn> defaults = new ArrayList<>();
		UserColumn userColumn = new UserColumn(userId, "CocktailImage", 0);
		defaults.add(userColumn);
		userColumn = new UserColumn(userId, "Brand", 1);
		defaults.add(userColumn);
		userColumn = new UserColumn(userId, "BaseSpiritCategory", 2);
		defaults.add(userColumn);
		userColumn = new UserColumn(userId, "BaseSpiritModifier", 3);
		defaults.add(userColumn);
		userColumn = new UserColumn(userId, "JuiceLiquids", 4);
		defaults.add(userColumn);
		userColumn = new UserColumn(userId, "Sweetener", 5);
		defaults.add(userColumn);
		userColumn = new UserColumn(userId, "Solids", 6);
		defaults.add(userColumn);
		userColumn = new UserColumn(userId, "NumberOfIngredients", 7);
		defaults.add(userColumn);
		userColumn = new UserColumn(userId, "DegreeOfDifficulty", 8);
		defaults.add(userColumn);
		userColumn = new UserColumn(userId, "CocktailCategory", 9);
		defaults.add(userColumn);
		
		this.userColumnRepository.saveAll(defaults);
	}

	@Transactional
	public void updateUserColumn(List<UserColumn> columns) {
		Optional<UserColumn> opt;
		UserColumn persist;
		for (UserColumn userColumn : columns) {
			opt = userColumnRepository.findById(userColumn.getPkId());
			if (opt.isPresent()) {
				persist = opt.get();
				persist.setColumnIndex(userColumn.getColumnIndex());
				this.userColumnRepository.save(persist);
			}
		}
	}
}
