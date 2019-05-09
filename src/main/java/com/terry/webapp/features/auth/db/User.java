package com.terry.webapp.features.auth.db;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import com.fasterxml.jackson.annotation.JsonFormat;


/**
 *
 * @author Terry
 *
 */
@Entity
public class User {

	private static final long serialVersionUID = 4882722066708750581L;

//	@Id
//    @GeneratedValue
//    private String userId;
	
	@Id
    @NotEmpty(message = "用户名不能为空")
    @Column(nullable = false, unique=true)
    private String username;

    @NotEmpty(message = "密码不能为空")
    @Column(nullable = false)
    private String password;

    @NotEmpty(message = "显示名不能为空")
    @Column(nullable = false)
    private String displayname;

	@Column(nullable = false,  columnDefinition = "Timestamp DEFAULT CURRENT_TIMESTAMP")
    @Generated(GenerationTime.INSERT)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Timestamp createTime;
    
//	private String roles;
//	
//    public String getRoles() {
//		return roles;
//	}
//
//	public void setRoles(String roles) {
//		this.roles = roles;
//	}

	// 不是表字段
	private List<String> roleList;
    
    public List<String> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<String> roleList) {
		this.roleList = roleList;
	}

//	public String getUserId() {
//        return userId;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
    
    public String getDisplayname() {
		return displayname;
	}

	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

//    @ElementCollection
//	@Override
//	public Collection<? extends GrantedAuthority> getAuthorities() {
//        List<GrantedAuthority> authorities = new ArrayList<>();
//    	if (roleList != null) {
//    		for (String role : roleList) {
//                authorities.add( new SimpleGrantedAuthority( role ) );
//    		}
//    	}
//		return authorities;
//	}
//
//	@Override
//	public boolean isAccountNonExpired() {
//		return true;
//	}
//
//	@Override
//	public boolean isAccountNonLocked() {
//		return true;
//	}
//
//	@Override
//	public boolean isCredentialsNonExpired() {
//		return true;
//	}
//
//	@Override
//	public boolean isEnabled() {
//		return true;
//	}
}
