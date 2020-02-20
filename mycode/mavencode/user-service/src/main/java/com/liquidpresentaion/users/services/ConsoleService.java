package com.liquidpresentaion.users.services;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Service;

import com.liquidpresentaion.users.model.Ingredient;
import com.liquidpresentaion.users.model.User;

@Service
public class ConsoleService {

	@PersistenceContext
	private EntityManager em;
	
	public User findUserById(Integer id) {
	  return em.find(User.class, id);
	}
	
	public Ingredient findIngredientById(Integer id) {
//		CriteriaBuilder builder = em.getCriteriaBuilder();
//		CriteriaQuery<Ingredient> criteria = builder.createQuery(Ingredient.class);
//		Root<Ingredient> in = criteria.from(Ingredient.class);
//		TypedQuery<Ingredient> query = em.createQuery(
//		    criteria.select(in).where(builder.equal(in.get("pkId"), id)));
//		return query.getSingleResult();
		
		
		  return em.find(Ingredient.class, id);
	}
}
