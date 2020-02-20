package com.liquidpresentation.ingredientservice.context;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.liquidpresentation.common.Role;

@Component
public class UserContext {

    private String correlationId= new String();
    private String authToken= new String();
    private String userEmail = new String();
    private String orgId = new String();
    private long userId;
    private long distributorId;
    private Set<String> authorities = new HashSet();

    public String getCorrelationId() { return correlationId;}
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
    
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getDistributorId() {
		return distributorId;
	}
	public void setDistributorId(long distributorId) {
		this.distributorId = distributorId;
	}
	public Set<String> getAuthorities() {
		return authorities;
	}
	public void setAuthorities(Collection<String> authorities) {
		this.authorities.clear();
		this.authorities.addAll(authorities);
	}
	public boolean isAdmin(){
		boolean isAdmin = false;
		for (String authority : authorities) {
			if (authority.endsWith(Role.ADMINISTRATOR.name())) {
				isAdmin = true;
			}
		}
		return isAdmin;
	}
}