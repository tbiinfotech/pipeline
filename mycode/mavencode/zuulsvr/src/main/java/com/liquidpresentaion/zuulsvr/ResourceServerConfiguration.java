package com.liquidpresentaion.zuulsvr;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
        .authorizeRequests()
    	.antMatchers("/**").permitAll()
    	.and().httpBasic().disable()
    	.csrf().disable();
	}
}
