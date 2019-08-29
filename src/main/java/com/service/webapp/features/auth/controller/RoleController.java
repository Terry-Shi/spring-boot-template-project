package com.service.webapp.features.auth.controller;

import java.util.List;

import javax.validation.Valid;

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

import com.service.webapp.common.response.BaseResponse;
import com.service.webapp.features.auth.bean.RoleBean;
import com.service.webapp.features.auth.db.Role;
import com.service.webapp.features.auth.db.UserRoles;
import com.service.webapp.features.auth.service.RoleService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/v1/roles")
@Api(value = "RoleController", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class RoleController {

	@Autowired
	private RoleService roleService;
	
	/**
     * create a role
     * @param request
     * @return
     */
    @PostMapping(value="/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "create new role.", notes ="create new role" , response = BaseResponse.class)
    public ResponseEntity<BaseResponse> createRole(@Valid @RequestBody Role role) {
        roleService.createRole(role);
        BaseResponse response = new BaseResponse(200, "role created");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /**
     * update a role
     * @param request
     * @return
     */
    @PutMapping(value="/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "update role info.", notes ="update role info" , response = BaseResponse.class)
    public ResponseEntity<BaseResponse> updateRole(@Valid @RequestBody Role request) {
        roleService.updateRole(request);
        BaseResponse response = new BaseResponse(200, "role updated");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /**
     * delete a role by roleName
     * @param roleName
     * @return
     */
    @DeleteMapping(value="/byname/{roleName}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "delete role info.", notes ="delete role info" , response = BaseResponse.class)
    public ResponseEntity<BaseResponse> deleteByName(@PathVariable("roleName") String roleName) {
    	roleService.deleteByRole(roleName);
        BaseResponse response = new BaseResponse(200, "role deleted");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * list all the roles
     * @return
     */
    @GetMapping(value="/list", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "query role list.", notes ="query role list" , response = Role.class)
    public ResponseEntity<List<Role>> list() {
        List<Role> roles = roleService.list();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }
    
    @GetMapping(value="/list-by-userid/{username}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "query role list with given userId.", notes ="query role list with given userId" , response = UserRoles.class)
    public ResponseEntity<List<RoleBean>> listWithUsername(@PathVariable("username")String username) {
    	List<RoleBean> roles = roleService.listWithUsername(username);
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }
}
