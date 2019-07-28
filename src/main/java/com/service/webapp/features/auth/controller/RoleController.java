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

import com.service.webapp.common.exception.AppException;
import com.service.webapp.common.response.BaseResponse;
import com.service.webapp.features.auth.db.Role;
import com.service.webapp.features.auth.db.RoleRepository;
import com.service.webapp.features.auth.db.UserRoles;
import com.service.webapp.features.auth.db.UserRolesRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/v1/roles")
@Api(value = "RoleController", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class RoleController {

	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private UserRolesRepository userRolesRepository;
	
	/**
     * create a role
     * @param request
     * @return
     */
    @PostMapping(value="/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "create new role.", notes ="create new role" , response = BaseResponse.class)
    public ResponseEntity<BaseResponse> add(@Valid @RequestBody Role role) {
        Role roles = roleRepository.findByRole(role.getRole());
        if (roles == null) {
            roleRepository.save(role);
        } else {
            throw new AppException("Role already exists!");
        }
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
    public ResponseEntity<BaseResponse> update(@Valid @RequestBody Role request) {
        Role oldRole = roleRepository.findByRole(request.getRole());
        if (oldRole != null) {
        	oldRole.setDescription(request.getDescription());
            roleRepository.save(oldRole);
        } else {
            throw new AppException("Role is not exists!");
        }
        BaseResponse response = new BaseResponse(200, "role updated");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /**
     * delete a role by roleId
     * @param roleId
     * @return
     */
    @DeleteMapping(value="/{roleId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "delete role info.", notes ="delete role info" , response = BaseResponse.class)
    public ResponseEntity<BaseResponse> delete(@PathVariable("roleId") String roleId) {
        try {
            roleRepository.deleteById(roleId);
            BaseResponse response = new BaseResponse(200, "role deleted");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            throw new AppException("delete role failed: " + e.getMessage());
        }
    }
    
    /**
     * delete a role by roleName
     * @param roleName
     * @return
     */
    @DeleteMapping(value="/byname/{roleName}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "delete role info.", notes ="delete role info" , response = BaseResponse.class)
    public ResponseEntity<BaseResponse> deleteByName(@PathVariable("roleName") String roleName) {
        try {
            roleRepository.deleteByRole(roleName);
            BaseResponse response = new BaseResponse(200, "role deleted");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            throw new AppException("delete role failed: " + e.getMessage());
        }
    }

    /**
     * list all the roles
     * @return
     */
    @GetMapping(value="/list", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "query role list.", notes ="query role list" , response = Role.class)
    public ResponseEntity<List<Role>> list() {
        try {
            List<Role> roles = roleRepository.findAll();
            return new ResponseEntity<>(roles, HttpStatus.OK);
        } catch (Exception e) {
            throw new AppException("list role failed: " + e.getMessage());
        }
    }
    
    @GetMapping(value="/list-by-userid/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "query role list with given userId.", notes ="query role list with given userId" , response = UserRoles.class)
    public ResponseEntity<List<UserRoles>> listWithUsername(@PathVariable("")Long userId) {
        try {
            List<UserRoles> roles = userRolesRepository.findByUsername(userId);
            return new ResponseEntity<>(roles, HttpStatus.OK);
        } catch (Exception e) {
            throw new AppException("list role with given userId failed: " + e.getMessage());
        }
    }
}
