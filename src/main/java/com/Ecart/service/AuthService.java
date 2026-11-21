package com.Ecart.service;

import com.Ecart.dto.AuthResponse;
import com.Ecart.dto.LoginRequest;
import com.Ecart.dto.RegisterRequest;
import com.Ecart.model.Customer;
import com.Ecart.model.Role;
import com.Ecart.repository.CustomerRepository;
import com.Ecart.security.JwtService;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    
    
    
     public AuthService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
	 		AuthenticationManager authenticationManager, ModelMapper modelMapper) {
	 	super();
	 	this.customerRepository = customerRepository;
	 	this.passwordEncoder = passwordEncoder;
	 	this.jwtService = jwtService;
	 	this.authenticationManager = authenticationManager;
	 	this.modelMapper = modelMapper;
	 }

	public AuthResponse register(RegisterRequest request) {
		if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already registered");
        }
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setAddress1(request.getAddress1());
        customer.setAddress2(request.getAddress2());
        customer.setCity(request.getCity());
        customer.setState(request.getState());
        customer.setCountry(request.getCountry());
        customer.setZipCode(request.getZipCode());
        customer.setRole(Role.CUSTOMER);
        
        customerRepository.save(customer);
        
        AuthResponse response = new AuthResponse();
        response.setUserId(customer.getId());
        response.setEmail(customer.getEmail());
        response.setName(customer.getName());
        response.setRole(customer.getRole().name());
        return response;
    }
    
    public AuthResponse login(LoginRequest request) {
    	
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        
        System.out.println(request.getEmail());
        Customer customer = customerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!customer.isActive()) {
            throw new RuntimeException("User account is blocked.");
        }

        System.out.println(customer.getEmail());
        
        String jwtToken = jwtService.generateToken(customer);
        System.out.println("jwttoken");
        AuthResponse response = new AuthResponse();
        response.setToken(jwtToken);
        response.setEmail(customer.getEmail());
        response.setName(customer.getName());
        response.setUserId(customer.getId());
        response.setRole(customer.getRole().name());
        System.out.println(response.getToken());
        System.out.println(response.getUserId());
        return response;
    }
    public Customer authenticate(LoginRequest request) {
        Optional<Customer> customerOpt = customerRepository.findByEmail(request.getEmail());
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (encoder.matches(request.getPassword(), customer.getPassword())) {
                return customer;
            }
            }
        return null;
    }
	
	
}