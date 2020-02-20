package com.liquidpresentation.common.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class UserContext {

    private String correlationId= new String();
    private String authToken= new String();
    private String userName = new String();
    private String orgId = new String();
    private long userId;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
	public Set<String> getAuthorities() {
		return authorities;
	}
	public void setAuthorities(Collection<String> authorities) {
		this.authorities.clear();
		this.authorities.addAll(authorities);
	}
	public boolean isAdmin(){
		boolean isAdmin = false;
		
		return isAdmin;
	}
}