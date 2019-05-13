package com.terry.webapp.features.auth.db;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.terry.webapp.features.auth.bean.RoleBean;


/**
 *
 * @author Terry
 *
 */
public interface UserRolesRepository extends JpaRepository<UserRoles, Long> {

	@Query(value = "")
    public List<UserRoles> findByUserId(String username);

    @Transactional
    public void deleteByUserId(String username);
   
    @Transactional
    public void deleteByUserIdAndRoleId(String username, String role);
    
    @Query(value = "select new com.terry.webapp.features.auth.bean.RoleBean(r1.role as role,r1.description as description) "
    		       + " from UserRoles as u1 left join Role as r1 on u1.id = r1.role_Id "
    		       + " where u1.id = :userId", nativeQuery = true)
    List<RoleBean> findRoleByUserId(@Param("userId")Long userId);
    
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
