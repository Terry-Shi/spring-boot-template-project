package com.terry.webapp.features.auth.db;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Terry
 *
 */
public interface UserRepository extends JpaRepository<User, String> {

    User findByUserId(String userId);
    
    User findByUsername(String username);
    
}
