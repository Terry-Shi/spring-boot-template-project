package com.terry.webapp.features.auth.db;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;


/**
 *
 * @author Terry
 *
 */
@Entity
public class UserRoles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;
//
//    @Column(nullable = false)
//    protected String userId;
//
//    @Column(nullable = false)
//    protected String roleId;

    @Column(nullable = false,  columnDefinition = "Timestamp DEFAULT CURRENT_TIMESTAMP")
    @Generated(GenerationTime.INSERT)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    protected Timestamp createTime;
    
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	@Fetch(FetchMode.JOIN)
    private User user;
    
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "role_id", insertable = false, updatable = false)
	@Fetch(FetchMode.JOIN)
    private Role role;
    
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

//    public String getUserId() {
//        return userId;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }
//
//    public String getRoleId() {
//        return roleId;
//    }
//
//    public void setRole(String roleId) {
//        this.roleId = roleId;
//    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
    
}
