package com.spring3.oauth.jwt.controllers;

import com.spring3.oauth.jwt.dtos.*;
import com.spring3.oauth.jwt.exceptions.UserNotFoundException;
import com.spring3.oauth.jwt.models.UserInfo;
import com.spring3.oauth.jwt.models.UserRole;
import com.spring3.oauth.jwt.services.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    private UserService userService;
    ModelMapper modelMapper = new ModelMapper();
   /* @Autowired
    SoftwareService softwareService;

    @Autowired
    LicenseKeyService licenseKeyService;*/

    @Autowired
    private JwtService jwtService;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/getSubAgentList/{userId}")
    public ResponseEntity<ApiResponse> getSubAgentList(@PathVariable String userId){
        int statusCode = 0;
        ApiResponse response = null;
        try {
            statusCode = HttpStatus.OK.value();
            SubAgentListResponse userInfoList = userService.getSubAgentList(userId);
            response = new ApiResponse<>(HttpStatus.OK.value(), "SUCCESS", userInfoList);
        } catch(Exception e) {
            statusCode = HttpStatus.UNAUTHORIZED.value();
            response = new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "FAILURE", null);
        }
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(statusCode));
    }
    @PostMapping(value = "/signup")
    public ResponseEntity<ApiResponse> saveUser(@RequestBody SignupRequest userRequest, HttpServletRequest httpServletRequest) {
        ApiResponse apiResponse = null;
        int statusCode = 0;
        UserInfo userResponse = null;
        try {
            userResponse = userService.saveUser(userRequest, httpServletRequest);
            statusCode = HttpStatus.OK.value();
            apiResponse = new ApiResponse(statusCode, "Success", userResponse);
            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            apiResponse = new ApiResponse(statusCode, "Failed:"+e, userResponse);
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/updateUserProfile")
    public  ResponseEntity<ApiResponse> updateAgentProfile(@RequestBody UserInfo userInfo) {
        int statusCode = 0;
        ApiResponse response = null;

        String msg = null;
        if (userInfo.getId() <= 0) {
            msg = "User ID is invalid";
            statusCode = HttpStatus.UNAUTHORIZED.value();
            response = new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "FAILURE: "+msg, null);
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(statusCode));
        }

        try {
            statusCode = HttpStatus.OK.value();
            UserInfo agentProfile = userService.updateAgentInfo(userInfo);
            response = new ApiResponse<>(HttpStatus.OK.value(), "SUCCESS", agentProfile);
        } catch(Exception e) {
            statusCode = HttpStatus.UNAUTHORIZED.value();
            response = new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "FAILURE", null);
        }
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(statusCode));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UserInfo>> getAllUsers() {
        List<UserInfo> userResponses = userService.getAllUser();
        if (userResponses.isEmpty()) {
            throw new UserNotFoundException("No users found.");
        }
        return new ResponseEntity<>(userResponses, HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/getUserDetailById/{userId}")
    public ResponseEntity<ApiResponse> getUserProfileDetails(@PathVariable String userId) {
        int statusCode = 0;
        ApiResponse response = null;
        String msg = "";
        try {
            statusCode = HttpStatus.OK.value();
            Optional<UserInfo> userInfo = userService.getAgentProfile(userId);
            statusCode = HttpStatus.OK.value();
            msg = "SUCCESS";
            if (userInfo.isEmpty()) {
                msg = "FAILURE: User Not Found";
                statusCode = HttpStatus.NOT_FOUND.value();
            }
            response = new ApiResponse<>(statusCode, msg, userInfo);
        } catch(Exception e) {

            statusCode = HttpStatus.UNAUTHORIZED.value();
            response = new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "FAILURE", null);
        }
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(statusCode));
    }

    @PostMapping("/assignRole")
    public ResponseEntity<ApiResponse> assignRole(@RequestParam List<String> roleIds, @RequestParam String userId) {
        ApiResponse apiResponse = null;
        int statusCode = 0;
        UserInfo userResponse = null;
        try {
            userResponse = userService.assignRole(roleIds, userId);
            statusCode = HttpStatus.OK.value();
            apiResponse = new ApiResponse(statusCode, "Success", userResponse);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception e) {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            apiResponse = new ApiResponse(statusCode, "Success", userResponse);
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/revokRole")
    public ResponseEntity<ApiResponse> revoknRole(@RequestParam List<String> roleIds, @RequestParam String userId) {
        ApiResponse apiResponse = null;
        int statusCode = 0;
        UserInfo userResponse = null;
        try {
            userResponse = userService.revokRole(roleIds, userId);
            statusCode = HttpStatus.OK.value();
            apiResponse = new ApiResponse(statusCode, "Success", userResponse);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception e) {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            apiResponse = new ApiResponse(statusCode, "Success", userResponse);
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/profile")
    public ApiResponse<UserInfo> getUserProfile() {
        UserInfo userResponse = userService.getUser();
        if (userResponse == null) {
            throw new UserNotFoundException("User not found.");
        }
        return new ApiResponse<>(HttpStatus.OK.value(), "Success", userResponse);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/getUserRolesByUserId/{userId}")
    public ResponseEntity<ApiResponse> getUserByUserName(@PathVariable("userId") String userId){
        int statusCode = 0;
        ApiResponse response = null;
        UserRolesResponse urs = null;
        String msg = "";
        try {
            statusCode = HttpStatus.OK.value();
            Optional<UserInfo> userInfo = userService.getAgentProfile(userId);
            statusCode = HttpStatus.OK.value();
            msg = "SUCCESS";
            if (userInfo.isEmpty()) {
                msg = "FAILURE: User Not Found";
                statusCode = HttpStatus.NOT_FOUND.value();
            } else {
            List<String> roles = new ArrayList<>();
                for(UserRole role: userInfo.get().getRoles()) {
                    roles.add(role.getName());
                }
                urs = new UserRolesResponse();
                urs.setUserId(Long.valueOf(userId));
                urs.setRoles(roles);
            }
            response = new ApiResponse<>(statusCode, msg, urs);
        } catch(Exception e) {

            statusCode = HttpStatus.UNAUTHORIZED.value();
            response = new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "FAILURE", null);
        }
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(statusCode));
    }

    @Operation(summary = "Get greeting message", description = "Returns a greeting message")
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return new ResponseEntity<>("Welcome", HttpStatus.OK);  // 200 OK for a successful request
    }

    @Hidden
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/deleteUser")
    public ResponseEntity<ApiResponse> deleteUser(@RequestParam Long userId){
        int statusCode = 0;
        ApiResponse response = null;
        Boolean isDeleted = userService.deleteUser(userId);
        if (!isDeleted) {
            statusCode = HttpStatus.NOT_FOUND.value();
            response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "FAILED", null);
            throw new UserNotFoundException("User not found.");
        } else {
            statusCode = HttpStatus.OK.value();
            response = new ApiResponse<>(HttpStatus.OK.value(), "Success", null);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public Claims validateToken(String token, String secretKey) {
        try {
            // Parse the token and validate its signature
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes()) // Use the same secret key used to sign the token
                    .build()
                    .parseClaimsJws(token) // Throws an exception if invalid
                    .getBody();
        } catch (SignatureException e) {
            throw new RuntimeException("Invalid JWT signature");
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token");
        }
    }


}
