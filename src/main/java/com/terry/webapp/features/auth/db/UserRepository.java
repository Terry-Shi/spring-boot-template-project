package com.terry.webapp.features.auth.db;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Terry
 *
 */
public interface UserRepository extends JpaRepository<User, String> {

    
    User findByUsername(String username);
    void deleteByUsername(String username);
    
}
