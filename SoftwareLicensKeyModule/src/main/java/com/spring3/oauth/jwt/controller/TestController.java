package com.spring3.oauth.jwt.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/softwarekey")
public class TestController {

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/softwarellllllll")
    public String gello(){
        return "jsdfjslfjs";
    }
}
