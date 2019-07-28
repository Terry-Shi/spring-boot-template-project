package com.service.webapp.features.auth.db;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import com.fasterxml.jackson.annotation.JsonFormat;


/**
 *
 * 用户与角色关联表
 * @author Terry
 *
 */
@Entity
public class UserRoles {
	// 自增ID，无业务含义
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    @Column(nullable = false)
    protected String userId;
    
    @Column(nullable = false)
    protected String roleId;

    @Column(nullable = false,  columnDefinition = "Timestamp DEFAULT CURRENT_TIMESTAMP")
    @Generated(GenerationTime.INSERT)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    protected Timestamp createTime;
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRole(String roleId) {
        this.roleId = roleId;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
}
