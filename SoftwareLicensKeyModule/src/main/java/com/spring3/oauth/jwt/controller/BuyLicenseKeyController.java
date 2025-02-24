package com.spring3.oauth.jwt.controller;

import com.spring3.oauth.jwt.dto.ApiResponse;
import com.spring3.oauth.jwt.models.BuyLicenseKey;
import com.spring3.oauth.jwt.models.LicenseKey;
import com.spring3.oauth.jwt.services.BuyLicenseKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/buylicensekey")
public class BuyLicenseKeyController {

    @Autowired
    BuyLicenseKeyService buyLicenseKeyService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/getAllBuyLicenseKey")
    public ResponseEntity<ApiResponse> getAllBuyLicenseKey() {
        ApiResponse apiResponse = null;
        List<BuyLicenseKey> buyLicenseKeys = null;
        int statusCode = 0;
        try {
            buyLicenseKeys = buyLicenseKeyService.getAllBuyLicenseKey();
            if (!buyLicenseKeys.isEmpty()) {
                apiResponse = new ApiResponse(HttpStatus.OK.value(), "Success", buyLicenseKeys);
                return new ResponseEntity<>(apiResponse, HttpStatus.OK);
            } else {
                apiResponse = new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Buy License Keys not found", null);
                return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
            }
        } catch (Exception e) {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            apiResponse = new ApiResponse(statusCode, "Failed:" + e, null);
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/byUserId/{id}")
    public ResponseEntity<ApiResponse> getBuyLicenseKey(@PathVariable Long id) {
        ApiResponse apiResponse = null;
        List<BuyLicenseKey> buyLicenseKeys = null;
        int statusCode = 0;
        try {
            buyLicenseKeys = buyLicenseKeyService.getByUserId(id);
            if (!buyLicenseKeys.isEmpty()) {
                apiResponse = new ApiResponse(HttpStatus.OK.value(), "Success", buyLicenseKeys);
                return new ResponseEntity<>(apiResponse, HttpStatus.OK);
            } else {
                apiResponse = new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Buy License Keys not found by user id", null);
                return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
            }
        } catch (Exception e) {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            apiResponse = new ApiResponse(statusCode, "Failed:" + e, null);
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
