package com.liquidpresentaion.cocktailservice.model.audit;

import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditorAwareImpl implements AuditorAware<Integer> {

	@Override
	public Optional<Integer> getCurrentAuditor() {
		 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		 Map user = (Map) authentication.getPrincipal();
		return Optional.of(Integer.valueOf(user.get("userId").toString()));
	}
}
