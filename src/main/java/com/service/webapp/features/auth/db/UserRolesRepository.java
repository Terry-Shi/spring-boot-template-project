package com.service.webapp.features.auth.db;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.service.webapp.features.auth.bean.RoleBean;


/**
 *
 * @author Terry
 *
 */
public interface UserRolesRepository extends JpaRepository<UserRoles, Long> {

    public List<UserRoles> findByUserId(Long userid);

    @Transactional
    public void deleteByUserId(Long userid);
   
    @Transactional
    public void deleteByUserIdAndRoleId(Long userid, Long roleid);
    
    // NativeQuery 返回记录映射到自定义POJO
    //role_name 或者 roleName都能被正确转换成结果集的对象
    @Query(value = "select r1.id as id, r1.role as role_name,r1.description as description "
    		       + " from User_Roles as u1 left join Role as r1 on u1.role_id = r1.Id "
    		       + " where u1.user_id = :userId", nativeQuery = true)
    List<RoleBean> findRoleByUserId(@Param("userId")Long userId);
    
    @Query(value = "select r1.id as id, r1.role as role_name,r1.description as description "
		       + " from User_Roles as u1  "
    		   + " join user as user1 on u1.user_id = user1.id "
    		   + " left join Role as r1 on u1.role_id = r1.Id "
		       + " where user.username = :username", nativeQuery = true)
    List<RoleBean> findRoleByUsername(@Param("username")String username);
}
