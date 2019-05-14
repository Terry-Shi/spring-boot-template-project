package com.terry.webapp.features.auth.db;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.terry.webapp.features.auth.bean.UserWithRoles;

/**
 *
 * @author Terry
 *
 */
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
    void deleteByUsername(String username);
    
    //List<UserWithRoles> findUserWithRoles(String username);
    
    @Query(value = "select new com.terry.webapp.features.auth.bean.UserWithRoles(u1.username as username,u1.displayname as displayname) "
    		+ " from User as u1 "
    		+"  left join UserRoles as ur1 on u1.id = ur1.user_Id "
    		+ "where u1.username = :username ",
            countQuery = "select count(uil.id) from User as uil where uil.username = :username ", nativeQuery = true)
    Page<UserWithRoles> queryUserByUsername(@Param("username")String username, Pageable pageable);

}
