package com.terry.webapp.features.auth.db;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {

	Role findByRole(String role);
	void deleteByRole(String role);
	
}
