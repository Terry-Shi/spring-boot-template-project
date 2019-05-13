package com.terry.webapp.features.auth.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.terry.webapp.features.auth.db.User;


@JsonIgnoreProperties(value = { "roles", "password" })
public class UserWithRoles extends User{
	
	private List<RoleBean> roleBean;

	public List<RoleBean> getRoleBean() {
		return roleBean;
	}

	public void setRoleBean(List<RoleBean> roleBean) {
		this.roleBean = roleBean;
	}
	
}
