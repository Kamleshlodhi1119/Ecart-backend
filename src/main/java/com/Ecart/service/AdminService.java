package com.Ecart.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Ecart.model.AdminLoginRequest;
import com.Ecart.repository.AdminRepository;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Use PasswordEncoder bean from config

    @PostConstruct
    public void init() {
        // Insert default admin only if not present
        if (adminRepository.findByEmail("admin123@gmail.com") == null) {
            String encodedPassword = passwordEncoder.encode("password123");
            adminRepository.save(new AdminLoginRequest("admin123@gmail.com", encodedPassword));
        }
    }

    public String login(String email, String password, HttpSession session) {
        if (session.getAttribute("admin") != null) {
            return "An admin is already logged in!";
        }

        Optional<AdminLoginRequest> adminOpt = Optional.ofNullable(adminRepository.findByEmail(email));
        if (adminOpt.isPresent() && passwordEncoder.matches(password, adminOpt.get().getPassword())) {
            session.setAttribute("admin", email);
            return "Login successful!";
        }

        return "Invalid credentials!";
    }

    public String logout(HttpSession session) {
        if (session.getAttribute("admin") != null) {
            session.invalidate();
            return "Logout successful!";
        }
        return "No admin is currently logged in!";
    }

    public List<AdminLoginRequest> getAll() {
        return adminRepository.findAll();
    }

    public boolean isAdminAuthenticated(HttpSession session) {
        return session.getAttribute("admin") != null;
    }
}
