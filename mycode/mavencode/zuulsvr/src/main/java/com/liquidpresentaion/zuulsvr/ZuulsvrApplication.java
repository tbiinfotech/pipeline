package com.liquidpresentaion.zuulsvr;

import java.util.Collections;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import com.liquidpresentation.common.utils.UserContextInterceptor;

@SpringBootApplication
//@EnableOAuth2Sso
@EnableZuulProxy
//@EnableResourceServer
@EnableEurekaClient
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class ZuulsvrApplication {
	
	@LoadBalanced
	@Bean
	public RestTemplate getRestTemplate(RestTemplateBuilder builder){
		RestTemplate restTemplate = builder.build();
		List intercepters = restTemplate.getInterceptors();
		if (intercepters == null) {
			restTemplate.setInterceptors(Collections.singletonList(new UserContextInterceptor()));
		} else {
			intercepters.add(new UserContextInterceptor());
			restTemplate.setInterceptors(intercepters);
		}
		
		return restTemplate;
	}

	public static void main(String[] args) {
		SpringApplication.run(ZuulsvrApplication.class, args);
	}
	
	@Bean
    public FilterRegistrationBean corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}
