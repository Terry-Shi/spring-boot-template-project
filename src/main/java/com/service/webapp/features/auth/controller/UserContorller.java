package com.service.webapp.features.auth.controller;

import java.util.List;

import javax.validation.Valid;

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
import com.service.webapp.features.auth.bean.UserWithRoles;
import com.service.webapp.features.auth.db.User;
import com.service.webapp.features.auth.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/v1/users")
@Api(value = "UserContorller", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class UserContorller {
    private static final Logger logger = LoggerFactory.getLogger(UserContorller.class);

    @Autowired
    private UserService userService;
    
    /**
     * 输入数据=用户名+密码 ---> 验证用户名+密码 ---> 生成accessToken和refreshToken ---> 返回
     * http://localhost:8809/api/v1/user/login
     * @param request LoginRequest
     * @return LoginResponse
     */
    @PostMapping(value="login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "Login.", notes ="user login API" , response = LoginResponse.class)
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = userService.login(request.getUsername(), request.getPassword());
		return new ResponseEntity<>(loginResponse, HttpStatus.OK);
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
    	LoginResponse loginResponse = userService.refreshToken(request.getRefreshToken());            
		return new ResponseEntity<>(loginResponse, HttpStatus.OK);
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
        userService.createUser(user);
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
    public ResponseEntity<BaseResponse> changePassword(@Valid @RequestBody User request) {
        userService.changePassword(request);
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
            userService.deleteUserById(id);
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
    public ResponseEntity<List<UserWithRoles>> listWithRoles() {
    	List<UserWithRoles> list = userService.listWithRoles();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

}
