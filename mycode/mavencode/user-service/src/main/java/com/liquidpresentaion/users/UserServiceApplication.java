package com.liquidpresentaion.users;

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

import com.liquidpresentaion.users.context.UserContextInterceptor;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableResourceServer
public class UserServiceApplication {

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
	
	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}
}
