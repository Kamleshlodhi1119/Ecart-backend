package com.Ecart.service;

import com.Ecart.model.CartItem;
import com.Ecart.model.Product;
import com.Ecart.repository.CartItemRepository;
import com.Ecart.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public CartItem addProductToCart(Long productId, Long customerId, String customerEmail, int quantity) {
        if (productId == null || customerEmail == null || customerEmail.isBlank() || quantity <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Product, customer email, and a positive quantity are required");
        }
        Product product = getActiveProduct(productId);
        CartItem cartItem = cartItemRepository.findByProductIdAndCustomerEmail(productId, customerEmail)
                .orElseGet(() -> new CartItem(productId, customerId, customerEmail, 0,
                        product.getName(), product.getDescription(), product.getPrice(), product.getCategory()));
        long updatedQuantity = (long) cartItem.getQuantity() + quantity;
        if (updatedQuantity > product.getQuantity()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Requested quantity exceeds available stock");
        }
        if (cartItem.getId() == null) {
            cartItem.setCustomerId(customerId);
        }
        updateCartItemFromProduct(cartItem, product, (int) updatedQuantity);
        return cartItemRepository.save(cartItem);
    }

    public List<CartItem> getCartItemsByCustomerEmail(String customerEmail) {
        return cartItemRepository.findByCustomerEmail(customerEmail);
    }
    @Transactional
    public CartItem updateQuantityByProductAndEmail(Long productId, String customerEmail, int quantity) {
        if (productId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is required");
        }
        requireCustomerEmail(customerEmail);
        CartItem item = cartItemRepository.findByProductIdAndCustomerEmail(productId, customerEmail)
                .orElse(null);
        if (item == null) {
            return null;
        }
        if (quantity <= 0) {
            cartItemRepository.delete(item);
            return null;
        }
        Product product = getActiveProduct(productId);
        if (quantity > product.getQuantity()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Requested quantity exceeds available stock");
        }
        updateCartItemFromProduct(item, product, quantity);
        return cartItemRepository.save(item);
    }
    
    @Transactional
    public boolean deleteCartItem(Long productId, String customerEmail) {
        if (productId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is required");
        }
        requireCustomerEmail(customerEmail);
        CartItem item = cartItemRepository.findByProductIdAndCustomerEmail(productId, customerEmail)
                .orElse(null);
        if (item == null) {
            return false;
        }
        cartItemRepository.delete(item);
        return true;
    }
    
    @Transactional
    public boolean deleteAllCartItem(String customerEmail) {
        requireCustomerEmail(customerEmail);
        List<CartItem> items = cartItemRepository.findByCustomerEmail(customerEmail);
        if (items.isEmpty()) {
            return false;
        }
        cartItemRepository.deleteByCustomerEmail(customerEmail);
        return true;
    }

    private Product getActiveProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        if (!product.isActive()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Product is not available");
        }
        return product;
    }

    private void requireCustomerEmail(String customerEmail) {
        if (customerEmail == null || customerEmail.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer email is required");
        }
    }

    private void updateCartItemFromProduct(CartItem item, Product product, int quantity) {
        item.setQuantity(quantity);
        item.setName(product.getName());
        item.setDescription(product.getDescription());
        item.setPrice(product.getPrice());
        item.setCategory(product.getCategory());
    }

}
