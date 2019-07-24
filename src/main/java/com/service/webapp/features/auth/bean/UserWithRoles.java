package com.service.webapp.features.auth.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.service.webapp.features.auth.db.User;


@JsonIgnoreProperties(value = { "createTime", "roles", "password" })
public class UserWithRoles extends User{
	
	private List<RoleBean> roles;

	public List<RoleBean> getRoles() {
		return roles;
	}

	public void setRoles(List<RoleBean> roles) {
		this.roles = roles;
	}
	
}
