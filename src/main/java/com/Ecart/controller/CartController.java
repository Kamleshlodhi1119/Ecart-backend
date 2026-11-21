package com.Ecart.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Ecart.dto.UpdateCartQuantityRequest;
import com.Ecart.model.CartItem;
import com.Ecart.service.CartService;

@RestController
@RequestMapping("/cart")
public class CartController {
	@Autowired
    private CartService cartService;
	
	@PostMapping("/add")
	public ResponseEntity<?> addProductToCart(@RequestBody CartItem cartItem) {
	    CartItem addedCartItem = cartService.addProductToCart(
	            cartItem.getProductId(),
	            cartItem.getCustomerId(),
	            cartItem.getCustomerEmail(),
	            cartItem.getQuantity()
	    );

	    return ResponseEntity.ok(addedCartItem);
	}

	
	@GetMapping("/getcart/{customerEmail}")
	public ResponseEntity<List<CartItem>> getAll(@PathVariable String customerEmail) {
	    System.out.println("chal rh");
	    List<CartItem> cartItems = cartService.getCartItemsByCustomerEmail(customerEmail);
	    return ResponseEntity.ok(cartItems);
	}
	
	@PostMapping("/update/quantity")
	public ResponseEntity<?> updateCartQuantity(@RequestBody UpdateCartQuantityRequest request) {
	    CartItem updated = cartService.updateQuantityByProductAndEmail(
	        request.getProductId(),
	        request.getCustomerEmail(),
	        request.getQuantity()
	    );

	    if (request.getQuantity() <= 0) {
	        return ResponseEntity.ok("Item removed from cart as quantity was zero or less.");
	    }

	    if (updated != null) {
	        return ResponseEntity.ok(updated);
	    }

	    return ResponseEntity.status(404).body("Cart item not found for update.");
	}
	
	@DeleteMapping("/delete")
	public ResponseEntity<?> deleteCartItem(
	        @RequestParam Long productId,
	        @RequestParam String customerEmail) {

	    boolean deleted = cartService.deleteCartItem(productId, customerEmail);

	    if (deleted) {
	        return ResponseEntity.ok("Cart item deleted successfully.");
	    } else {
	        return ResponseEntity.status(404).body("Cart item not found.");
	    }
	}
	@DeleteMapping("/clearCart")
	public ResponseEntity<?> deleteAllCartItem(
	                @RequestParam String customerEmail) {

	    boolean deleted = cartService.deleteAllCartItem(customerEmail);

	    if (deleted) {
	        return ResponseEntity.ok("Cart item deleted successfully.");
	    } else {
	        return ResponseEntity.status(404).body("Cart item not found.");
	    }
	}


}