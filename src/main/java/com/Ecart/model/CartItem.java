package com.Ecart.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cart_item_table")
public class CartItem {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Unique identifier for the cart item
    private Long productId; // ID of the product
    private Long customerId; // ID of the customer
    private String customerEmail; // Email of the customer
    private int quantity; // Quantity of the product
    private String name; // Name of the product
    private String description; // Description of the product
    private double price; // Price of the product
    private String category; // Category of the product

    // Constructors
    public CartItem() {}

    public CartItem(Long productId, Long customerId, String customerEmail, int quantity, String name, String description, double price, String category) {
        this.productId = productId;
        this.customerId = customerId;
        this.customerEmail = customerEmail;
        this.quantity = quantity;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    
	@Override
	public String toString() {
		return "CartItem [id=" + id + ", productId=" + productId + ", customerId=" + customerId + ", customerEmail="
				+ customerEmail + ", quantity=" + quantity + ", name=" + name + ", description=" + description
				+ ", price=" + price + ", category=" + category + "]";
	}
    
}