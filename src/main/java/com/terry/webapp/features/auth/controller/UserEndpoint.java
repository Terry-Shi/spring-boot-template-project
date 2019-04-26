package com.terry.webapp.features.auth.controller;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.terry.webapp.common.exception.AppException;
import com.terry.webapp.common.response.BaseResponse;
import com.terry.webapp.features.auth.bean.LoginRequest;
import com.terry.webapp.features.auth.bean.LoginResponse;
import com.terry.webapp.features.auth.bean.RefreshTokenRequest;
import com.terry.webapp.features.auth.db.UserRolesRepository;
import com.terry.webapp.features.auth.db.User;
import com.terry.webapp.features.auth.db.UserRepository;
import com.terry.webapp.features.auth.db.UserRoles;
import com.terry.webapp.util.SimpleSaltHash;
import com.terry.webapp.util.token.Token;
import com.terry.webapp.util.token.TokenManager;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/v1/users")
@Api(value = "UserContorller", produces = "application/json")
public class UserEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(UserEndpoint.class);

    @Value("${security.token-expire-time}")
    public long tokenExpireTime;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserRolesRepository userRolesRepository;
    
    @Autowired
    private TokenManager tokenManager;

    /**
     * 输入数据=用户名+密码 ---> 验证用户名+密码 ---> 生成accessToken和refreshToken ---> 返回
     * http://localhost:8809/api/v1/user/login
     * @param request
     * @return
     */
    @PostMapping(value="login", produces = "application/json; charset=utf-8")
    @ApiOperation(value = "Login.", notes ="user login API" , response = LoginResponse.class)
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        List<User> result = userRepository.findByUserId(request.getUserId());
        if (result.size() == 0 ) {
        	// userId 不存在
            LoginResponse loginResponse = new LoginResponse.Builder().statusCode(401).token("").refreshToken("").build();
            return new ResponseEntity<>(
            		loginResponse, 
            	      HttpStatus.UNAUTHORIZED);
        } else {
            String psw = result.get(0).getPassword();
            // 密码匹配
            if (SimpleSaltHash.getMd5Hash(request.getPassword(), SimpleSaltHash.salt).equals(psw)) { 
                // token 的有效时间可以配置
                Instant expiredTime = Instant.now().plus(tokenExpireTime, ChronoUnit.MINUTES);
                Instant refreshTokenExpiredTime = Instant.now().plus(tokenExpireTime*2, ChronoUnit.MINUTES);
                String token = tokenManager.generateToken(request.getUserId(), Date.from(expiredTime));
                String refreshToken = tokenManager.generateToken(request.getUserId(), Date.from(refreshTokenExpiredTime));
                List<UserRoles>  roleList = userRolesRepository.findByUserIdAndServiceName(request.getUserId(), "SYS");
                String sysRoles = "";
                if (roleList != null && roleList.size()>0) {
                    for (UserRoles userRoles : roleList) {
                        sysRoles = sysRoles + "," + userRoles.getRole();
                    }
                    sysRoles = sysRoles.substring(1);
                }
                LoginResponse loginResponse = new LoginResponse.Builder().statusCode(200).token(token)
                        .refreshToken(refreshToken).userId(request.getUserId()).sysRoles(sysRoles).build();
                return new ResponseEntity<>(
                		loginResponse, 
                	      HttpStatus.OK);
            } else {
                // 密码错误
                LoginResponse loginResponse = new LoginResponse.Builder().statusCode(401).token("").refreshToken("").build();
                return new ResponseEntity<>(
                		loginResponse, 
                	      HttpStatus.UNAUTHORIZED);
            }
        }
    }

    /**
     * 输入数据=accessToken和refreshToken ---> 验证refreshToken ---> 重新生成一套accessToken和refreshToken ---> 返回
     * http://localhost:8809/api/v1/user/refreshtoken
     * @param request
     * @return
     */

    @PostMapping(value="refreshtoken", produces = "application/json; charset=utf-8")
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
            String userId = decodedToken.getUserId();
            // token 的有效时间可以配置
            Instant expiredTime = Instant.now().plus(tokenExpireTime, ChronoUnit.MINUTES);
            Instant refreshTokenExpiredTime = Instant.now().plus(tokenExpireTime*2, ChronoUnit.MINUTES);
            String token = tokenManager.generateToken(userId, Date.from(expiredTime));
            String refreshToken = tokenManager.generateToken(userId, Date.from(refreshTokenExpiredTime));
            List<UserRoles>  roleList = userRolesRepository.findByUserIdAndServiceName(userId, "SYS");
            String sysRoles = "";
            if (roleList != null && roleList.size()>0) {
                for (UserRoles userRoles : roleList) {
                    sysRoles = sysRoles + "," + userRoles.getRole();
                }
                sysRoles = sysRoles.substring(1);
            }
            LoginResponse loginResponse = new LoginResponse.Builder().statusCode(200).token(token)
                    .refreshToken(refreshToken).userId(userId).sysRoles(sysRoles).build();            
			return new ResponseEntity<>(loginResponse, HttpStatus.OK);
        } else {
            logger.info("refresh token failed, refreshtoken is not valid.");
            throw new AppException("refresh token failed, refreshtoken is not valid.");
        }
    }
    
    /**
     * create a user
     * @param request
     * @return
     */
    @PostMapping(value="/", produces = "application/json; charset=utf-8")
    @ApiOperation(value = "create new user.", notes ="create new user" , response = BaseResponse.class)
    public ResponseEntity<BaseResponse> add(@Valid @RequestBody User user) {
        try {
            List<User> users = userRepository.findByUserId(user.getUserId());
            if (users.size() == 0) {
            	user.setPassword(SimpleSaltHash.getMd5Hash(user.getPassword(), SimpleSaltHash.salt));
                userRepository.save(user);
            } else {
                throw new AppException("User already exists!");
            }
            BaseResponse response = new BaseResponse(200, "user created");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException("create new user failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * update a user
     * @param request
     * @return
     */
    @PutMapping(value="/", produces = "application/json; charset=utf-8")
    @ApiOperation(value = "update user info.", notes ="update user info" , response = BaseResponse.class)
    public ResponseEntity<BaseResponse> update(@Valid @RequestBody User request) {
        try {
            List<User> users = userRepository.findByUserId(request.getUserId());
            if (users.size() == 1) {
                if (!StringUtils.isBlank(request.getPassword())) {
                    request.setPassword(SimpleSaltHash.getMd5Hash(request.getPassword(), SimpleSaltHash.salt));
                } else {
                    request.setPassword(users.get(0).getPassword());
                }
                userRepository.save(request);
            } else {
                throw new AppException("User not exists!");
            }
            BaseResponse response = new BaseResponse(200, "user updated");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException("update user failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * delete a user by userId
     * @param userId
     * @return
     */
    @DeleteMapping(value="/{userId}", produces = "application/json; charset=utf-8")
    @ApiOperation(value = "delete user info.", notes ="delete user info" , response = BaseResponse.class)
    public ResponseEntity<BaseResponse> delete(@PathVariable("userId") String userId) {
        try {
            userRepository.delete(userId);
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
    @GetMapping(value="/list", produces = "application/json; charset=utf-8")
    @ApiOperation(value = "query user list.", notes ="query user list" , response = List.class)
    public ResponseEntity<List<User>> list() {
        try {
            List<User> users = userRepository.findAll();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            throw new AppException("list user failed: " + e.getMessage());
        }
    }

//    /**
//     * list all the users with SYS role
//     * @return
//     */
//    @Path("/list-with-sysroles")
//    @GET
//    @Consumes(MediaType.APPLICATION_JSON)
//    public List<UserWithSysRoles> listWithSysRoles() {
//        try {
//            List<UserWithSysRoles> ret = new ArrayList<UserWithSysRoles>();
//            List<User> users = userRepository.findAll();
//            for (User user : users) {
//                List<UserRoles> userRoles = userRolesRepository.findByUserIdAndServiceName(user.getUserId(), "SYS");
//                UserWithSysRoles userWithSysRoles = new UserWithSysRoles();
//                userWithSysRoles.from(user);
//                String oneUserSysRoles = "";
//                for (UserRoles roles : userRoles) {
//                    oneUserSysRoles = oneUserSysRoles + "," +roles.getRole();
//                }
//                if (!oneUserSysRoles.equals("")) {
//                    oneUserSysRoles = oneUserSysRoles.substring(1);
//                }
//                userWithSysRoles.setSysRoles(oneUserSysRoles);
//                ret.add(userWithSysRoles);
//            }
//            return ret;
//        } catch (Exception e) {
//            throw new AppException("list user failed: " + e.getMessage());
//        }
//    }
}
