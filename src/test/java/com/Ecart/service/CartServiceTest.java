package com.Ecart.service;

import com.Ecart.model.CartItem;
import com.Ecart.model.Product;
import com.Ecart.repository.CartItemRepository;
import com.Ecart.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CartServiceTest {

    private CartItemRepository cartItemRepository;
    private ProductRepository productRepository;
    private CartService cartService;
    private Product product;

    @BeforeEach
    void setUp() {
        cartItemRepository = mock(CartItemRepository.class);
        productRepository = mock(ProductRepository.class);
        cartService = new CartService(cartItemRepository, productRepository);
        product = new Product();
        product.setId(7L);
        product.setName("Chair");
        product.setDescription("Wooden chair");
        product.setCategory("Home");
        product.setPrice(25.5);
        product.setQuantity(5);
        product.setActive(true);
        when(productRepository.findById(7L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByProductIdAndCustomerEmail(7L, "customer@example.com"))
                .thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void addToCartUsesProductDetailsRatherThanClientSuppliedValues() {
        CartItem added = cartService.addProductToCart(7L, 3L, "customer@example.com", 2);

        assertEquals("Chair", added.getName());
        assertEquals("Wooden chair", added.getDescription());
        assertEquals("Home", added.getCategory());
        assertEquals(25.5, added.getPrice());
        assertEquals(2, added.getQuantity());
    }

    @Test
    void addToCartRejectsQuantityAboveAvailableStock() {
        assertThrows(ResponseStatusException.class,
                () -> cartService.addProductToCart(7L, 3L, "customer@example.com", 6));

        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void clearCartDeletesOnlyTheRequestedCustomersItems() {
        when(cartItemRepository.findByCustomerEmail("customer@example.com"))
                .thenReturn(java.util.List.of(new CartItem()));

        cartService.deleteAllCartItem("customer@example.com");

        verify(cartItemRepository).deleteByCustomerEmail("customer@example.com");
        verify(cartItemRepository, never()).deleteAll();
    }
}
