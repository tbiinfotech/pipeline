package com.liquidpresentation.common.utils;

import static com.liquidpresentation.common.Constants.CONTEXT_AUTH_TOKEN;
import static com.liquidpresentation.common.Constants.CONTEXT_CORRELATION_ID;
import static com.liquidpresentation.common.Constants.CONTEXT_ORG_ID;
import static com.liquidpresentation.common.Constants.CONTEXT_USER_NAME;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserContextFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(UserContextFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        UserContextHolder.getContext().setCorrelationId(  httpServletRequest.getHeader(CONTEXT_CORRELATION_ID));
        UserContextHolder.getContext().setUserName(httpServletRequest.getHeader(CONTEXT_USER_NAME));
//        UserContextHolder.getContext().setUserId(Long.parseLong(httpServletRequest.getHeader(CONTEXT_USER_ID)));
        UserContextHolder.getContext().setUserId(0);
        UserContextHolder.getContext().setAuthToken(httpServletRequest.getHeader(CONTEXT_AUTH_TOKEN));
        UserContextHolder.getContext().setOrgId(httpServletRequest.getHeader(CONTEXT_ORG_ID));

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Map user = (Map) authentication.getPrincipal();
		UserContextHolder.getContext().setUserId(Long.parseLong(user.get("userId").toString()));
		UserContextHolder.getContext().setAuthorities(authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()));

        logger.debug("Special Routes Service Incoming Correlation id: {}", UserContextHolder.getContext().getCorrelationId());

        filterChain.doFilter(httpServletRequest, servletResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}
