package com.Ecart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Ecart.model.AdminLoginRequest;

import jakarta.validation.constraints.Email;

public interface AdminRepository  extends JpaRepository<AdminLoginRequest, String> {
	public AdminLoginRequest findByEmail(String email);

}
 