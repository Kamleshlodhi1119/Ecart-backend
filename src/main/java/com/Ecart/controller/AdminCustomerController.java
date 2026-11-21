package com.Ecart.controller;

import com.Ecart.model.Customer;
import com.Ecart.repository.CustomerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/customers")
public class AdminCustomerController {

    private final CustomerRepository customerRepository;
    

     public AdminCustomerController(CustomerRepository customerRepository) {
	 	super();
	 	this.customerRepository = customerRepository;
	 }

	@GetMapping("/all")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerRepository.findAll());
    }

    @PutMapping("/toggle-status/{id}")
    public ResponseEntity<?> toggleCustomerStatus(@PathVariable Long id) {
        return customerRepository.findById(id)
                .map(customer -> {
                    customer.setActive(!customer.isActive());
                    customerRepository.save(customer);
                    return ResponseEntity.ok("Customer status toggled successfully.");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return ResponseEntity.ok("Customer deleted successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
