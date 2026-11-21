package com.Ecart.service;

import com.Ecart.dto.OrderDto;
import com.Ecart.dto.OrderItemDto;
import com.Ecart.model.Customer;
import com.Ecart.model.Order;
import com.Ecart.model.Product;
import com.Ecart.repository.CartItemRepository;
import com.Ecart.repository.CustomerRepository;
import com.Ecart.repository.OrderRepository;
import com.Ecart.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import org.mockito.ArgumentCaptor;

class OrderServiceTest {

    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private CustomerRepository customerRepository;
    private CartItemRepository cartItemRepository;
    private ModelMapper modelMapper;
    private OrderService orderService;
    private Product product;
    private Customer customer;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        productRepository = mock(ProductRepository.class);
        customerRepository = mock(CustomerRepository.class);
        cartItemRepository = mock(CartItemRepository.class);
        modelMapper = mock(ModelMapper.class);
        orderService = new OrderService(
                orderRepository, productRepository, customerRepository, cartItemRepository, modelMapper);

        customer = new Customer();
        customer.setId(3L);
        customer.setEmail("customer@example.com");
        product = new Product();
        product.setId(7L);
        product.setName("Chair");
        product.setPrice(25.5);
        product.setQuantity(5);
        product.setActive(true);

        when(customerRepository.findById(3L)).thenReturn(Optional.of(customer));
        when(productRepository.findByIdForUpdate(7L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(modelMapper.map(any(Order.class), eq(OrderDto.class))).thenReturn(new OrderDto());
    }

    @Test
    void checkoutUsesDatabasePriceAndDeductsStock() {
        OrderDto request = orderRequest(2, 0.01, 1, 9999);

        orderService.createOrder(request);

        assertEquals(2, product.getQuantity());
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals(76.5, orderCaptor.getValue().getTotalAmount());
        assertEquals(1, orderCaptor.getValue().getItems().size());
        assertEquals(3, orderCaptor.getValue().getItems().get(0).getQuantity());
        assertEquals(25.5, orderCaptor.getValue().getItems().get(0).getPrice());
        verify(cartItemRepository).deleteByCustomerEmail("customer@example.com");
    }

    @Test
    void checkoutRejectsInsufficientStockWithoutSavingAnOrder() {
        OrderDto request = orderRequest(6, 25.5);

        assertThrows(ResponseStatusException.class, () -> orderService.createOrder(request));

        assertEquals(5, product.getQuantity());
        verify(orderRepository, never()).save(any(Order.class));
        verify(cartItemRepository, never()).deleteByCustomerEmail(any());
    }

    @Test
    void checkoutRejectsNonPositiveQuantities() {
        OrderDto request = orderRequest(0, 25.5);

        assertThrows(ResponseStatusException.class, () -> orderService.createOrder(request));

        verify(productRepository, never()).findByIdForUpdate(any());
    }

    @Test
    void cancellingConfirmedOrderRestoresStockOnlyOnce() {
        Order order = new Order();
        order.setStatus("CONFIRMED");
        com.Ecart.model.OrderItem item = new com.Ecart.model.OrderItem();
        item.setProduct(product);
        item.setQuantity(2);
        order.setItems(List.of(item));
        when(orderRepository.findByIdForUpdate(15L)).thenReturn(Optional.of(order));

        orderService.cancelOrder(15L);
        assertEquals(7, product.getQuantity());

        assertThrows(ResponseStatusException.class, () -> orderService.cancelOrder(15L));
        assertEquals(7, product.getQuantity());
    }

    private OrderDto orderRequest(int quantity, double clientPrice) {
        return orderRequest(quantity, clientPrice, 0, 0);
    }

    private OrderDto orderRequest(int quantity, double clientPrice, int duplicateQuantity, double duplicatePrice) {
        OrderItemDto item = new OrderItemDto();
        item.setProductId(7L);
        item.setQuantity(quantity);
        item.setPrice(clientPrice);

        OrderDto request = new OrderDto();
        request.setCustomerId(3L);
        if (duplicateQuantity > 0) {
            OrderItemDto duplicate = new OrderItemDto();
            duplicate.setProductId(7L);
            duplicate.setQuantity(duplicateQuantity);
            duplicate.setPrice(duplicatePrice);
            request.setItems(List.of(item, duplicate));
        } else {
            request.setItems(List.of(item));
        }
        request.setPaymentMethod("CARD");
        return request;
    }
}
