package com.Ecart.service;

import com.Ecart.dto.RegisterRequest;
import com.Ecart.model.Customer;
import com.Ecart.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.List;

import lombok.*;


@Service
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    
     public CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
	 	super();
	 	this.customerRepository = customerRepository;
	 	this.passwordEncoder = passwordEncoder;
	 }

    public List<Customer> getAllCustomer()
    {
    	return customerRepository.findAll();
    }
    
	public RegisterRequest getCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Customer not found"));
        
        return toRegisterRequest(customer);
    }
    
    @Transactional
    public RegisterRequest updateCustomer(Long id, RegisterRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Customer not found"));
        
        updateCustomerFields(customer, request);
        return toRegisterRequest(customerRepository.save(customer));
    }
    
    private void updateCustomerFields(Customer customer, RegisterRequest request) {
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            customer.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setAddress1(request.getAddress1());
        customer.setAddress2(request.getAddress2());
        customer.setCity(request.getCity());
        customer.setState(request.getState());
        customer.setCountry(request.getCountry());
        customer.setZipCode(request.getZipCode());
    }
    

    private RegisterRequest toRegisterRequest(Customer customer) {
        return RegisterRequest.builder()
                .name(customer.getName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .address1(customer.getAddress1())
                .address2(customer.getAddress2())
                .city(customer.getCity())
                .state(customer.getState())
                .country(customer.getCountry())
                .zipCode(customer.getZipCode())
                .build();
    }
                          }