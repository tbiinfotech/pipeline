package com.liquidpresentaion.authenticationservice.security;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter{

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Override
	@Bean
	public UserDetailsService userDetailsServiceBean() {
		return new LpUserDetailsService();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authProvider());
	}
	
	@Bean
	public DaoAuthenticationProvider authProvider() {
	    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
	    authProvider.setUserDetailsService(userDetailsServiceBean());
	    authProvider.setPasswordEncoder(passwordEncoder());
	    return authProvider;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder(){
//		PasswordEncoder encoder = new BCryptPasswordEncoder(12);
//		return encoder;
		
		return new PasswordEncoder() {
	        @Override
	        public String encode(CharSequence charSequence) {
	            return getMd5(charSequence.toString());
	        }

	        @Override
	        public boolean matches(CharSequence charSequence, String s) {
	            return getMd5(charSequence.toString()).equals(s);
	        }
	        
	        public synchronized String getMd5(String input) {
	        	try {
	        		MessageDigest md = MessageDigest.getInstance("MD5");
	        		byte[] messageDigest = md.digest(input.getBytes());
	        		BigInteger number = new BigInteger(1, messageDigest);
	        		String hashtext = number.toString(16);
	        		// Now we need to zero pad it if you actually want the full 32 chars.
	        		while (hashtext.length() < 32) {
	        			hashtext = "0" + hashtext;
	        		}
	        		return hashtext;
	        	}
	        	catch (NoSuchAlgorithmException e) {
	        		throw new RuntimeException(e);
	        	}
	        }
	    };
	}
	
	
	
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		http.authorizeRequests()
//	      .antMatchers("/hello").access("hasRole('ROLE_ADMIN')")  
//	      .anyRequest().permitAll()
//	      .and()
//	        .formLogin().loginPage("/login")
//	        .usernameParameter("username").passwordParameter("password")
//	      .and()
//	        .logout().logoutSuccessUrl("/login?logout") 
//	       .and()
//	       .exceptionHandling().accessDeniedPage("/403")
//	      .and()
//	        .csrf();
//	}

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
