package com.spring3.oauth.jwt.controllers;

import com.spring3.oauth.jwt.dtos.AddSoftwareRequst;
import com.spring3.oauth.jwt.dtos.ApiResponse;
import com.spring3.oauth.jwt.dtos.SignupRequest;
import com.spring3.oauth.jwt.models.Softwares;
import com.spring3.oauth.jwt.models.UserInfo;
import com.spring3.oauth.jwt.services.SoftwareService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/software")
public class SoftwareController {

    @Autowired
    SoftwareService softwareService;

    @GetMapping("/testme")
    public  String testme(){
        return "Hello";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/addSoftware")
    public ResponseEntity<ApiResponse> saveSoftware(@RequestBody AddSoftwareRequst addSoftwareRequst) {
        ApiResponse apiResponse = null;
        int statusCode = 0;
        Softwares userResponse = null;
        try {
            Softwares softwares = new Softwares(0L, addSoftwareRequst.getSoftwareName(), addSoftwareRequst.getSoftwareURL(), null);
            userResponse = softwareService.addSoftware(softwares);
            statusCode = HttpStatus.OK.value();
            apiResponse = new ApiResponse(statusCode, "Success", userResponse);
            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            apiResponse = new ApiResponse(statusCode, "Failed:"+e, userResponse);
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
