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

    public List<UserRoles> findByUserId(String userId);

    @Transactional
    public void deleteByUserId(String userId);

    public List<UserRoles> findByServiceName(String serviceName);
    @Transactional
    public void deleteByUserIdAndServiceName(String userId, String serviceName);
    @Transactional
    public void deleteByUserIdAndServiceNameAndRole(String userId, String serviceName, String role);
    
    public List<UserRoles> findByUserIdAndServiceName(String userId, String serviceName);
    
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
