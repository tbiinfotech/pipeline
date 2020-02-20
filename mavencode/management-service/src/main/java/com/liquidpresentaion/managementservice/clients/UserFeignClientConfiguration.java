package com.liquidpresentaion.managementservice.clients;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;

import com.liquidpresentation.common.utils.UserContextHolder;

import feign.RequestInterceptor;

public class UserFeignClientConfiguration {
	@Value("${security.oauth2.client.accessTokenUri}")
	private String accessTokenUri;
	@Value("${security.oauth2.client.clientId}")
	private String clientId;
	@Value("${security.oauth2.client.clientSecret}")
	private String clientSecret;
//	@Value("${security.oauth2.client.scope}")
//	private String scope;

	@Bean
	RequestInterceptor oauth2FeignRequestInterceptor() {
		return new OAuth2FeignRequestInterceptor(new DefaultOAuth2ClientContext(), resource());
	}

//	@Bean
//	Logger.Level feignLoggerLevel() {
//		return Logger.Level.FULL;
//	}

	private OAuth2ProtectedResourceDetails resource() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Map user = (Map) authentication.getPrincipal();

		ResourceOwnerPasswordResourceDetails resourceDetails = new ResourceOwnerPasswordResourceDetails();
		resourceDetails.setUsername(user.get("username").toString());
		resourceDetails.setPassword(user.get("password").toString());
		resourceDetails.setAccessTokenUri(accessTokenUri);
		resourceDetails.setClientId(clientId);
		resourceDetails.setClientSecret(clientSecret);
		resourceDetails.setGrantType("password");
//		resourceDetails.setScope(Arrays.asList(scope));
		return resourceDetails;
	}
}
