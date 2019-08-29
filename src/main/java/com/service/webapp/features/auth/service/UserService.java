package com.service.webapp.features.auth.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.service.webapp.common.exception.AppException;
import com.service.webapp.common.exception.RestapiException;
import com.service.webapp.features.auth.bean.LoginResponse;
import com.service.webapp.features.auth.bean.RoleBean;
import com.service.webapp.features.auth.bean.UserWithRoles;
import com.service.webapp.features.auth.db.User;
import com.service.webapp.features.auth.db.UserRepository;
import com.service.webapp.features.auth.db.UserRolesRepository;
import com.service.webapp.security.AppSecurityConfig;
import com.service.webapp.security.SimpleSaltHash;
import com.service.webapp.security.token.Token;
import com.service.webapp.security.token.TokenManager;

@Service
public class UserService {
	
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserRolesRepository userRolesRepository;
    
    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private AppSecurityConfig appSecurityConfig;
	
    
    public LoginResponse login(String username, String password) {
        User user  = userRepository.findByUsername(username);
        // user 不存在
        if (user == null) {
        	throw new RestapiException(HttpStatus.UNAUTHORIZED.value(), "用户不存在。");
        }
        // 检查密码是否匹配
        if (SimpleSaltHash.getMd5Hash(password, SimpleSaltHash.salt).equals(user.getPassword())) { 
        	// 生成Token
            // token 的有效时间可以配置
            Instant accessTokenExpiredTime = Instant.now().plus(appSecurityConfig.getTokenExpiredSeconds(), ChronoUnit.SECONDS);
            Instant refreshTokenExpiredTime = Instant.now().plus(appSecurityConfig.getRefreshTokenExpiredSeconds(), ChronoUnit.SECONDS);
            String token = tokenManager.generateToken(username, Date.from(accessTokenExpiredTime));
            String refreshToken = tokenManager.generateToken(username, Date.from(refreshTokenExpiredTime));
            List<RoleBean>  roleList = userRolesRepository.findRoleByUsername(username);
            String sysRoles = "";
            if (roleList != null && roleList.size()>0) {
                List<String> roles = roleList.stream().map(role -> role.getRoleName()).collect(Collectors.toList());
                sysRoles = String.join(",",  roles);
            }
            LoginResponse loginResponse = new LoginResponse.Builder().statusCode(200).token(token)
                    .refreshToken(refreshToken).userId(username).roles(sysRoles).build();
            return loginResponse;
        } else {
        	throw new RestapiException(HttpStatus.UNAUTHORIZED.value(), "密码错误。");
        }
    }
    
    public LoginResponse refreshToken(String refreshToken) {
    	// check if refresh token is valid
        Token decodedToken = tokenManager.decodeToken(refreshToken);
        if (decodedToken != null && decodedToken.isValid()) {
            // 超时
            if (decodedToken.getExpirationTime().getTime() < System.currentTimeMillis()) {
                logger.info("refresh token failed, The refreshToken is expired: " + decodedToken.getExpirationTime());
                throw new AppException("refresh token failed, refreshtoken is expired.");
            }
            String username = decodedToken.getUsername();
            // token 的有效时间可以配置
            Instant accessTokenExpiredTime = Instant.now().plus(appSecurityConfig.getTokenExpiredSeconds(), ChronoUnit.SECONDS);
            Instant refreshTokenExpiredTime = Instant.now().plus(appSecurityConfig.getRefreshTokenExpiredSeconds(), ChronoUnit.SECONDS);
            String newAccessToken = tokenManager.generateToken(username, Date.from(accessTokenExpiredTime));
            String newRefreshToken = tokenManager.generateToken(username, Date.from(refreshTokenExpiredTime));
            List<RoleBean>  roleList = userRolesRepository.findRoleByUsername(username);
            String sysRoles = "";
            if (roleList != null && roleList.size()>0) {
                List<String> roles = roleList.stream().map(role -> role.getRoleName()).collect(Collectors.toList());
            	sysRoles = String.join(",", roles);
            }
            LoginResponse loginResponse = new LoginResponse.Builder().statusCode(200).token(newAccessToken)
                    .refreshToken(newRefreshToken).userId(username).roles(sysRoles).build();            
			return loginResponse;
        } else {
            logger.info("refresh token failed, refreshtoken is not valid, token is:" + decodedToken);
            throw new AppException("refresh token failed, refreshtoken is not valid, token is:" + decodedToken);
        }
    }
    
    public void createUser(User user) {
    	User users = userRepository.findByUsername(user.getUsername());
        if (users == null) {
        	user.setPassword(SimpleSaltHash.getMd5Hash(user.getPassword(), SimpleSaltHash.salt));
            userRepository.save(user);
        } else {
            throw new AppException("User already exists!");
        }
    }
    
    public void changePassword(User request) {
    	User users = userRepository.findByUsername(request.getUsername());
        if (users != null) {
            if (!StringUtils.isBlank(request.getPassword())) {
                request.setPassword(SimpleSaltHash.getMd5Hash(request.getPassword(), SimpleSaltHash.salt));
            } else {
                request.setPassword(users.getPassword());
            }
            userRepository.save(request);
        } else {
            throw new AppException("User not exists!");
        }
    }
    
    @Transactional
    public void deleteUserById(Long id) {
    	userRepository.deleteById(id);
        userRolesRepository.deleteByUserId(id);
    }
    
    public List<UserWithRoles> listWithRoles() {
    	List<UserWithRoles> list = new ArrayList<UserWithRoles>();
        try {
            List<User> users = userRepository.findAll();
            for (User user: users) {
            	UserWithRoles userWithRoles = new UserWithRoles();
                List<RoleBean> roleBean = userRolesRepository.findRoleByUsername(user.getUsername());
                userWithRoles.setId(user.getId());
                userWithRoles.setUsername(user.getUsername());
                userWithRoles.setDisplayname(user.getDisplayname());
                userWithRoles.setRoles(roleBean);
                list.add(userWithRoles);
            }
        } catch (Exception e) {
            throw new AppException("list user failed: " + e.getMessage());
        }
        return list;
    }
    
    
}
