package com.liquidpresentaion.managementservice;

import java.util.Collections;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.client.RestTemplate;

import com.liquidpresentaion.managementservice.context.UserContextInterceptor;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableResourceServer
public class ManagementServiceApplication {

	@LoadBalanced
	@Bean
	public RestTemplate getRestTemplate(RestTemplateBuilder builder){
		RestTemplate restTemplate = builder.build();
		List interceptors = restTemplate.getInterceptors();
		
		if (interceptors == null) {
			restTemplate.setInterceptors(Collections.singletonList(new UserContextInterceptor()));
		} else {
			interceptors.add(new UserContextInterceptor());
			restTemplate.setInterceptors(interceptors);
		}
		
		return restTemplate;
	}
	
//	@Bean
//	public CorsFilter corsFilter() {
//
//	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//	    CorsConfiguration config = new CorsConfiguration();
//	    config.setAllowCredentials(true); 
//	    config.addAllowedOrigin(CORS_ALLOWED_ORIGIN);
//	    config.addAllowedHeader(CORS_ALLOWED_HEADER);
//	    config.addAllowedMethod(CORS_ALLOWED_METHOD_GET);
//	    config.addAllowedMethod(CORS_ALLOWED_METHOD_PUT);
//	    config.addAllowedMethod(CORS_ALLOWED_METHOD_POST);
//	    config.addAllowedMethod(CORS_ALLOWED_METHOD_DELETE);
//	    source.registerCorsConfiguration(CORS_CONFIGRATION, config);
//	    return new CorsFilter(source);
//	}

	//redeploy mgnt service on Aug 7th
	public static void main(String[] args) {
		SpringApplication.run(ManagementServiceApplication.class, args);
	}
}
