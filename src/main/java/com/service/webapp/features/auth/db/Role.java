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

@Entity
public class Role {

	// 自增ID，无业务含义
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	// 权限名称
    @Column(nullable = false, unique = true)
    private String role; // ADMIN, DEVELOPER, 。。。 
    
    // 权限描述
    @Column
    private String description;
  
    @Column(nullable = false,  columnDefinition = "Timestamp DEFAULT CURRENT_TIMESTAMP")
    @Generated(GenerationTime.INSERT)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Timestamp createTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "Role [id=" + id + ", role=" + role + ", description=" + description + ", createTime=" + createTime
				+ "]";
	}
}