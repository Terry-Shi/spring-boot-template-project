package com.terry.webapp.filter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.terry.webapp.common.AppContext;
import com.terry.webapp.config.AppProperties;
import com.terry.webapp.security.token.Token;

import java.io.IOException;

import javax.annotation.Priority;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;


/**
 * @author 
 */
@Component
@Order(1)
public class AuthenticationFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
//    @Autowired
//    private AuthxManagement authxManagement;
    
    @Autowired
	private AppProperties appProperties;
    
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        //long start = System.currentTimeMillis();
        checkAuth(servletRequest);
        filterChain.doFilter(servletRequest,servletResponse);
        //System.out.println("Execute cost="+(System.currentTimeMillis() - start));
    }
    
    private void checkAuth(ServletRequest servletRequest) {
    	/**
         * Handle authentication switch
         */
        if (!appProperties.getAuthentication().isEnable()) {
            return;
        }
        /**
         * Handle OPTION requests without bearer token
         */
        HttpServletRequest req = (HttpServletRequest)servletRequest;
        if (req.getMethod().equalsIgnoreCase("OPTIONS")) {
            return;
        }
        /**
         * Handle Anonymous Access
         */
//        String uri = req.getRequestURI();
//        if (this.authxManagement.checkAnomynousAccess(uri)) {
//            logger.info("uri {} is allowed for anomynous access", uri);
//            return;
//        }
        /**
         * Extract token from bearer token
         */
//        String bearerToken = req.getHeader(AuthxConstant.AUTHORIZATION);
//        Token token = this.authxManagement.decodeBearerToken(bearerToken);
//        TokenUtil.verifyToken(token);
//        AppContext.setLevel(token.getLevel());
//        AppContext.setName(token.getName());
//        AppContext.setRole(token.getRole());
//        AppContext.setUserId(token.getUserId());
//        AppContext.setOrganization(token.getOrganization());
//        AppContext.setOrganizationCode(token.getOrganizationCode());
//        AppContext.setStatus(token.getStatus());
    }

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}
}
