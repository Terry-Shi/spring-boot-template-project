package com.service.webapp.features.auth.controller;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.service.webapp.common.exception.AppException;
import com.service.webapp.common.response.BaseResponse;
import com.service.webapp.features.auth.bean.LoginRequest;
import com.service.webapp.features.auth.bean.LoginResponse;
import com.service.webapp.features.auth.bean.RefreshTokenRequest;
import com.service.webapp.features.auth.bean.RoleBean;
import com.service.webapp.features.auth.bean.UserWithRoles;
import com.service.webapp.features.auth.db.RoleRepository;
import com.service.webapp.features.auth.db.User;
import com.service.webapp.features.auth.db.UserRepository;
import com.service.webapp.features.auth.db.UserRolesRepository;
import com.service.webapp.security.AppSecurityConfig;
import com.service.webapp.security.SimpleSaltHash;
import com.service.webapp.security.token.Token;
import com.service.webapp.security.token.TokenManager;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/v1/users")
@Api(value = "UserContorller", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class UserContorller {
    private static final Logger logger = LoggerFactory.getLogger(UserContorller.class);

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserRolesRepository userRolesRepository;
    
    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private AppSecurityConfig appSecurityConfig;
    
    /**
     * 输入数据=用户名+密码 ---> 验证用户名+密码 ---> 生成accessToken和refreshToken ---> 返回
     * http://localhost:8809/api/v1/user/login
     * @param request LoginRequest
     * @return LoginResponse
     */
    @PostMapping(value="login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "Login.", notes ="user login API" , response = LoginResponse.class)
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        User user  = userRepository.findByUsername(request.getUsername());
        if (user == null) {
        	// user不存在
            LoginResponse loginResponse = new LoginResponse.Builder().statusCode(401).token("").refreshToken("").build();
			return new ResponseEntity<>(loginResponse, HttpStatus.UNAUTHORIZED);
        } else {
        	// 检查密码是否匹配
            String psw = user.getPassword();
            if (SimpleSaltHash.getMd5Hash(request.getPassword(), SimpleSaltHash.salt).equals(psw)) { 
            	// 生成Token
                // token 的有效时间可以配置
                Instant accessTokenExpiredTime = Instant.now().plus(appSecurityConfig.getTokenExpiredSeconds(), ChronoUnit.SECONDS);
                Instant refreshTokenExpiredTime = Instant.now().plus(appSecurityConfig.getRefreshTokenExpiredSeconds(), ChronoUnit.SECONDS);
                String token = tokenManager.generateToken(request.getUsername(), Date.from(accessTokenExpiredTime));
                String refreshToken = tokenManager.generateToken(request.getUsername(), Date.from(refreshTokenExpiredTime));
                List<RoleBean>  roleList = userRolesRepository.findRoleByUsername(request.getUsername());
                String sysRoles = "";
                if (roleList != null && roleList.size()>0) {
                    List<String> roles = roleList.stream().map(role -> role.getRoleName()).collect(Collectors.toList());
                    sysRoles = String.join(",",  roles);
                }
                LoginResponse loginResponse = new LoginResponse.Builder().statusCode(200).token(token)
                        .refreshToken(refreshToken).userId(request.getUsername()).roles(sysRoles).build();
				return new ResponseEntity<>(loginResponse, HttpStatus.OK);
            } else {
                // 密码错误
                LoginResponse loginResponse = new LoginResponse.Builder().statusCode(401).token("").refreshToken("").build();
				return new ResponseEntity<>(loginResponse, HttpStatus.UNAUTHORIZED);
            }
        }
    }

    /**
     * 输入数据=accessToken和refreshToken ---> 验证refreshToken ---> 重新生成一套accessToken和refreshToken ---> 返回
     * http://localhost:8809/api/v1/user/refreshtoken
     * @param request
     * @return
     */
    @PostMapping(value="refreshtoken", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "refreshtoken.", notes ="return refreshtoken token" , response = LoginResponse.class)
    public ResponseEntity<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        // check if refresh token is valid
        Token decodedToken = tokenManager.decodeToken(request.getRefreshToken());
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
            String token = tokenManager.generateToken(username, Date.from(accessTokenExpiredTime));
            String refreshToken = tokenManager.generateToken(username, Date.from(refreshTokenExpiredTime));
            List<RoleBean>  roleList = userRolesRepository.findRoleByUsername(username);
            String sysRoles = "";
            if (roleList != null && roleList.size()>0) {
                List<String> roles = roleList.stream().map(role -> role.getRoleName()).collect(Collectors.toList());
            	sysRoles = String.join(",", roles);
            }
            LoginResponse loginResponse = new LoginResponse.Builder().statusCode(200).token(token)
                    .refreshToken(refreshToken).userId(username).roles(sysRoles).build();            
			return new ResponseEntity<>(loginResponse, HttpStatus.OK);
        } else {
            logger.info("refresh token failed, refreshtoken is not valid, token is:" + decodedToken);
            throw new AppException("refresh token failed, refreshtoken is not valid, token is:" + decodedToken);
        }
    }
    
    /**
     * create a user
     * 输入User--->检查是否已经存在同名用户---> 密码加密---> 保存新用户
     * @param request
     * @return
     */
    @PostMapping(value="/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "create new user.", notes ="create new user" , response = BaseResponse.class)
    public ResponseEntity<BaseResponse> addUser(@Valid @RequestBody User user) {
        User users = userRepository.findByUsername(user.getUsername());
        if (users == null) {
        	user.setPassword(SimpleSaltHash.getMd5Hash(user.getPassword(), SimpleSaltHash.salt));
            userRepository.save(user);
        } else {
            throw new AppException("User already exists!");
        }
        BaseResponse response = new BaseResponse(200, "user created");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /**
     * update a user (更改密码)
     * TODO: 是否要提供旧密码比对？
     * @param request
     * @return
     */
    @PutMapping(value="/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "update user password.", notes ="update user password" , response = BaseResponse.class)
    public ResponseEntity<BaseResponse> update(@Valid @RequestBody User request) {
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
        BaseResponse response = new BaseResponse(200, "user updated");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /**
     * delete a user by userId
     * 联动删除UserRole表的数据
     * @param userId
     * @return
     */
    @DeleteMapping(value="/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "delete user info.", notes ="delete user info" , response = BaseResponse.class)
    public ResponseEntity<BaseResponse> delete(@PathVariable("id") Long id) {
        try {
            userRepository.deleteById(id);
            userRolesRepository.deleteByUserId(id);
            BaseResponse response = new BaseResponse(200, "user deleted");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            throw new AppException("delete user failed: " + e.getMessage());
        }
    }

    /**
     * list all the users
     * @return
     */
    @GetMapping(value="/list", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "query user list with roles.", notes ="query user list with roles." , response = List.class)
    public ResponseEntity<List<UserWithRoles>> list() {
    	List<UserWithRoles> list = new ArrayList<UserWithRoles>();
        try {
            List<User> users = userRepository.findAll();
            for (User user: users) {
            	UserWithRoles userWithRoles = new UserWithRoles();
                List<RoleBean> roleBean = userRolesRepository.findByUserId(user.getId());
                userWithRoles.setId(user.getId());
                userWithRoles.setUsername(user.getUsername());
                userWithRoles.setDisplayname(user.getDisplayname());
                userWithRoles.setRoles(roleBean);
                list.add(userWithRoles);
            }
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception e) {
            throw new AppException("list user failed: " + e.getMessage());
        }
    }

}
