package io.github.jokoframework.myproject.web.controller;


import io.swagger.annotations.*;
import io.github.jokoframework.security.controller.SecurityConstants;
import io.github.jokoframework.myproject.constants.ApiPaths;
import io.github.jokoframework.myproject.exceptions.UserException;
import io.github.jokoframework.myproject.profile.manager.UserManager;
import io.github.jokoframework.myproject.profile.dto.UserDTO;
import io.github.jokoframework.myproject.web.request.CsvExportRequestDTO;
import io.github.jokoframework.myproject.web.response.HeartBeatResponseDTO;
import io.github.jokoframework.myproject.web.response.UserResponseDTO;
import io.github.jokoframework.myproject.web.response.UsersResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.github.jokoframework.myproject.web.request.CreateUserRequestDTO;
import io.github.jokoframework.myproject.web.request.UpdateUserRequestDTO;
import io.github.jokoframework.myproject.web.response.BaseResponseDTO;

import javax.validation.Valid;
import java.util.List;

/**
 * Users API
 *
 * @author bsandoval
 */
@RestController
public class UserController extends BaseRestController {

    @Autowired
    private UserManager userManager;

    @Override
    @RequestMapping(value = ApiPaths.USERS_HEARTBEAT, method = RequestMethod.GET)
    public ResponseEntity<HeartBeatResponseDTO> getHearbeat() {
        return new ResponseEntity<HeartBeatResponseDTO>(getHeartBeatStatus(), HttpStatus.OK);
    }

    @ApiOperation(value = "Get user's profile",
            notes = "Get user's profile based on username", position = 2)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User profile retrieved"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @RequestMapping(value = ApiPaths.USERS_BY_NAME, method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = SecurityConstants.AUTH_HEADER_NAME, dataType = "String", paramType = "header", required = true, value = "User Access Token"),
            @ApiImplicitParam(name = JOKO_STARTER_KIT_VERSION_HEADER, dataType = "String", paramType = "header", required = false, value = "Version", defaultValue = JOKO_STARTER_KIT_VERSION)
    })
    public ResponseEntity<UserResponseDTO> getUser(@ApiParam("user name") @PathVariable("username") String username)
            throws UserException {
        UserDTO userDTO = userManager.getByUsername(username);
        UserResponseDTO response = new UserResponseDTO(userDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @ApiOperation(value = "Get users",
            notes = "Get all users", position = 2)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Users retrieved"),
            @ApiResponse(code = 404, message = "Users not found")
    })
    @RequestMapping(value = ApiPaths.ROOT_USERS, method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = SecurityConstants.AUTH_HEADER_NAME, dataType = "String", paramType = "header", required = true, value = "User Access Token"),
            @ApiImplicitParam(name = JOKO_STARTER_KIT_VERSION_HEADER, dataType = "String", paramType = "header", required = false, value = "Version", defaultValue = JOKO_STARTER_KIT_VERSION)
    })
    public ResponseEntity<UsersResponseDTO> getUsers() throws UserException {
        List<UserDTO> list = userManager.findAll();
        UsersResponseDTO response = new UsersResponseDTO(list);
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @ApiOperation(value = "Export user's list to csv",
            notes = "Get all users and creates a csv file", position = 2)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Csv successfully created"),
            @ApiResponse(code = 404, message = "Users not found")
    })
    @RequestMapping(value = ApiPaths.USERS_CSV, method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = SecurityConstants.AUTH_HEADER_NAME, dataType = "String", paramType = "header", required = true, value = "User Access Token"),
            @ApiImplicitParam(name = JOKO_STARTER_KIT_VERSION_HEADER, dataType = "String", paramType = "header", required = false, value = "Version", defaultValue = JOKO_STARTER_KIT_VERSION)
    })
    public ResponseEntity<UsersResponseDTO> getUsersCsvList(@RequestBody @Valid CsvExportRequestDTO request) throws UserException {
        List<UserDTO> list = userManager.findAll();
        byte[] csv = userManager.exportUsersListToCsv(list, request.getColumns());
        
        UsersResponseDTO response = new UsersResponseDTO(list);
        response.setCsv(csv);
        response.setSuccess(true);
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // ======== CRUD Endpoints ========

    @ApiOperation(value = "Create a new user",
            notes = "Creates a new user. Automatically generates an audit event.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "User created successfully"),
            @ApiResponse(code = 400, message = "Invalid request or username already exists")
    })
    @RequestMapping(value = ApiPaths.ROOT_USERS, method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = SecurityConstants.AUTH_HEADER_NAME, dataType = "String",
                    paramType = "header", required = true, value = "User Access Token"),
            @ApiImplicitParam(name = JOKO_STARTER_KIT_VERSION_HEADER, dataType = "String",
                    paramType = "header", required = false, value = "Version",
                    defaultValue = JOKO_STARTER_KIT_VERSION)
    })
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid CreateUserRequestDTO request)
            throws UserException {
        UserDTO userDTO = userManager.createUser(request);
        UserResponseDTO response = new UserResponseDTO(userDTO);
        response.setSuccess(true);
        response.setUserMessage("User created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Update an existing user",
            notes = "Updates user data. Automatically generates an audit event.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User updated successfully"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @RequestMapping(value = ApiPaths.USERS_BY_ID, method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = SecurityConstants.AUTH_HEADER_NAME, dataType = "String",
                    paramType = "header", required = true, value = "User Access Token"),
            @ApiImplicitParam(name = JOKO_STARTER_KIT_VERSION_HEADER, dataType = "String",
                    paramType = "header", required = false, value = "Version",
                    defaultValue = JOKO_STARTER_KIT_VERSION)
    })
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable("userId") Long userId,
            @RequestBody @Valid UpdateUserRequestDTO request) throws UserException {
        UserDTO userDTO = userManager.updateUser(userId, request);
        UserResponseDTO response = new UserResponseDTO(userDTO);
        response.setSuccess(true);
        response.setUserMessage("User updated successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Delete a user",
            notes = "Permanently deletes a user (hard delete). Automatically generates an audit event.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User deleted successfully"),
            @ApiResponse(code = 404, message = "User not found")
    })
    @RequestMapping(value = ApiPaths.USERS_BY_ID, method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = SecurityConstants.AUTH_HEADER_NAME, dataType = "String",
                    paramType = "header", required = true, value = "User Access Token"),
            @ApiImplicitParam(name = JOKO_STARTER_KIT_VERSION_HEADER, dataType = "String",
                    paramType = "header", required = false, value = "Version",
                    defaultValue = JOKO_STARTER_KIT_VERSION)
    })
    public ResponseEntity<BaseResponseDTO> deleteUser(@PathVariable("userId") Long userId)
            throws UserException {
        userManager.deleteUser(userId);
        BaseResponseDTO response = BaseResponseDTO.ok();
        response.setMessage("User deleted successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}