package com.Ecart.controller;

import com.Ecart.dto.LoginRequest;
import com.Ecart.dto.RegisterRequest;
import com.Ecart.dto.AuthResponse;
import com.Ecart.service.AuthService;
import com.Ecart.model.Customer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
     public AuthController(AuthService authService) {
 	super();
 	this.authService = authService;
 }
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpSession session, HttpServletRequest httpRequest) {
        session.invalidate(); // destroy old session
        HttpSession newSession = httpRequest.getSession(true); // create a new session

        Customer customer = authService.authenticate(request);
        if (customer != null) {
        	if(customer.isActive()!=false) {
            newSession.setAttribute("customer", customer);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Login successful",
                    "userId", customer.getId(),
                    "email", customer.getEmail(),
                    "name", customer.getName()
            ));
        } else{
        	 return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Account Is blocked Contact With Admin"));
        }
        	}
       
        else {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Invalid email or password!"));
            
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("status", "success", "message", "Logged out successfully!"));
    }

    @GetMapping("/check-session")
    public ResponseEntity<?> checkSession(HttpSession session) {
        Object user = session.getAttribute("customer");
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No active session");
        }
    }

    @GetMapping("/current-customer")
    public ResponseEntity<?> getCurrentCustomer(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return ResponseEntity.status(401).body(Map.of("status", "error", "message", "Not logged in!"));
        }
        return ResponseEntity.ok(customer);
    }
    
    @GetMapping("/check-customer")
    public ResponseEntity<?> getCurrentCustomerCheck(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return ResponseEntity.status(401).body(Map.of("status", "error", "message", "Not logged in!"));
        }
        return ResponseEntity.ok(customer);
    }
}
