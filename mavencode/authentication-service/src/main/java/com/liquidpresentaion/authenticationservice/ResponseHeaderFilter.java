package com.liquidpresentaion.authenticationservice;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

//@Slf4j
@Component
@WebFilter(urlPatterns={"/*"}, filterName="responseHeaderFilter")
public class ResponseHeaderFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		
	       HttpServletResponse response = (HttpServletResponse) res;
	        
	        response.setHeader("Access-Control-Allow-Origin","*");
	        response.setHeader("Access-Control-Allow-Credentials", "true");
	        response.setHeader("Access-Control-Allow-Methods", "*");
	        response.setHeader("Access-Control-Max-Age", "3600");
	        response.setHeader("Access-Control-Allow-Headers", "*");
	        response.setHeader("Content-Security-Policy","upgrade-insecure-requests");
	        chain.doFilter(req, res);

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	
}
