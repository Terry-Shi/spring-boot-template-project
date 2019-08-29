package com.service.webapp.features.auth.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.service.webapp.common.exception.AppException;
import com.service.webapp.features.auth.bean.RoleBean;
import com.service.webapp.features.auth.db.Role;
import com.service.webapp.features.auth.db.RoleRepository;
import com.service.webapp.features.auth.db.UserRolesRepository;

@Service
public class RoleService {
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private UserRolesRepository userRolesRepository;
	
	public void createRole(Role role) {
		Role roles = roleRepository.findByRole(role.getRole());
        if (roles == null) {
            roleRepository.save(role);
        } else {
            throw new AppException("Role already exists!");
        }
	}
	
	public void updateRole(Role request) {
		Role oldRole = roleRepository.findByRole(request.getRole());
        if (oldRole != null) {
        	oldRole.setDescription(request.getDescription());
            roleRepository.save(oldRole);
        } else {
            throw new AppException("Role is not exists!");
        }
	}
	
	public void deleteByRole(String role) {
		roleRepository.deleteByRole(role);
	}
	
	public List<Role> list() {
		List<Role> roles = roleRepository.findAll();
		return roles;
	}
	
	public List<RoleBean> listWithUsername(String username) {
		List<RoleBean> roles = userRolesRepository.findRoleByUsername(username);
		return roles;
	}
}
