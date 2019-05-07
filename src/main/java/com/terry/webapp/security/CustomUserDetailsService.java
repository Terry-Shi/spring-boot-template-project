package com.terry.webapp.security;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.terry.webapp.features.auth.db.User;
import com.terry.webapp.features.auth.db.UserRepository;
import com.terry.webapp.features.auth.db.UserRoles;
import com.terry.webapp.features.auth.db.UserRolesRepository;

@Component
public class CustomUserDetailsService implements UserDetailsService {
	
    private UserRepository users;
    private UserRolesRepository userRoles;

    public CustomUserDetailsService(UserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
    	User user = users.findByUsername(username);
    	if (user == null) {
    		throw new UsernameNotFoundException("Username: " + username + " not found");
    	}
    	List<UserRoles> userRoleList = userRoles.findByUsername(username);
    	userRoleList.stream().map(role -> role.getRole()).collect(Collectors.toList());
    	return user;
    }
}
