package com.Ecart.controller;

import com.Ecart.dto.RegisterRequest;
import com.Ecart.model.Customer;
import com.Ecart.service.CustomerService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/auth/customers")
public class CustomerController {

    private final CustomerService customerService;
     public CustomerController(CustomerService customerService) {
	 	super();
	 	this.customerService = customerService;
	 }

    @GetMapping("/{id}")
    public ResponseEntity<RegisterRequest> getCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomer(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<RegisterRequest> updateCustomer(
            @PathVariable Long id,
            @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    @GetMapping("/getallUser")
    public List<Customer> getAllUsers() {
        return customerService.getAllCustomer();
    }

    @GetMapping("/me")
    public ResponseEntity<?> getLoggedInCustomer(HttpSession session) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return ResponseEntity.status(401).body("No customer logged in");
        }
        return ResponseEntity.ok(customer);
    }
}
