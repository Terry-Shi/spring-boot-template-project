package com.terry.webapp.features.auth.db;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Terry
 *
 */
public interface UserRepository extends JpaRepository<User, String> {

    List<User> findByUserId(String userId);
    
    List<User> findByUserName(String userName);

}
