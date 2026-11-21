package com.Ecart.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Ecart.model.AdminLoginRequest;
import com.Ecart.service.AdminService;

import jakarta.servlet.http.HttpSession;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/admin-login")
    public ResponseEntity<String> login(@RequestBody AdminLoginRequest adminLoginRequest, HttpSession session) {
        String result = adminService.login(adminLoginRequest.getEmail(), adminLoginRequest.getPassword(), session);
        if (result.contains("successful")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(401).body(result);
        }
    }

    @PostMapping("/admin-logout")
    public ResponseEntity<String> logout(HttpSession session) {
        String result = adminService.logout(session);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/check-admin")
    public ResponseEntity<Map<String, Boolean>> checkAdminSession(HttpSession session) {
        boolean isAuthenticated = adminService.isAdminAuthenticated(session);
        return ResponseEntity.ok(Map.of("authenticated", isAuthenticated));
    }

    @GetMapping("/getall")
    public List<AdminLoginRequest> getAll() {
        return adminService.getAll();
    }
}
