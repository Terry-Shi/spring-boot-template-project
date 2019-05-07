package com.terry.webapp.features.auth.db;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


/**
 *
 * @author Terry
 *
 */
public interface UserRolesRepository extends JpaRepository<UserRoles, Long> {

    public List<UserRoles> findByUsername(String username);

    @Transactional
    public void deleteByUsername(String username);
   
    @Transactional
    public void deleteByUsernameAndRole(String username, String role);
    
    
//    @Query(value = "select e.id, e.user_id, e.role, e.gateway_policy_id, d.service_name, d.http_method, d.url "
//        + " from user_roles e "
//        + " left join gateway_policy d on d.id = e.gateway_policy_id "
//        ,
//        nativeQuery = true)
//    public List<Object[]> findAllData();

//    @Query(value = "select e.id, e.user_id, e.role, e.gateway_policy_id, d.service_name, d.http_method, d.url "
//            + " from user_roles e "
//            + " left join gateway_policy d on d.id = e.gateway_policy_id "
//            + "where e.user_id = ?1 "
//            + "  and d.service_name = ?2 "
//            + "  and d.http_method = ?3 "
//            ,
//            nativeQuery = true)
//    public List<Object[]> findAllDataByUserIdAndServiceName(String userId, String serviceName);

    
}
