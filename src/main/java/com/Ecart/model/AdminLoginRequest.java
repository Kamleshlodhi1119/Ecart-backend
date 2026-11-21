package com.Ecart.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class AdminLoginRequest {
	@Id
    private String email;
    private String password;

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AdminLoginRequest() {
        super();
    }

    public AdminLoginRequest(String email, String password) {
        super();
        this.email = email;
        this.password = password;
    }

}
