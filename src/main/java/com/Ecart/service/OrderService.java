package com.Ecart.service;

import com.Ecart.dto.OrderDto;
import com.Ecart.dto.OrderItemDto;
import com.Ecart.model.*;
import com.Ecart.repository.CartItemRepository;
import com.Ecart.repository.CustomerRepository;
import com.Ecart.repository.OrderRepository;
import com.Ecart.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final CartItemRepository cartItemRepository; // ✅ Add this
    private final ModelMapper modelMapper;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository,
                        CustomerRepository customerRepository, CartItemRepository cartItemRepository,
                        ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.cartItemRepository = cartItemRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public OrderDto createOrder(OrderDto orderDto) {
        if (orderDto == null || orderDto.getCustomerId() == null
                || orderDto.getItems() == null || orderDto.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer and at least one order item are required");
        }
        Customer customer = customerRepository.findById(orderDto.getCustomerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        Map<Long, Integer> requestedQuantities = new LinkedHashMap<>();
        for (OrderItemDto itemDto : orderDto.getItems()) {
            if (itemDto == null || itemDto.getProductId() == null || itemDto.getQuantity() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Each order item needs a product and positive quantity");
            }
            try {
                requestedQuantities.merge(itemDto.getProductId(), itemDto.getQuantity(), Math::addExact);
            } catch (ArithmeticException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requested quantity is too large", e);
            }
        }

        List<Long> productIds = new ArrayList<>(requestedQuantities.keySet());
        productIds.sort(Long::compareTo);
        List<OrderItem> items = new ArrayList<>();
        double totalAmount = 0;
        for (Long productId : productIds) {
            Product product = productRepository.findByIdForUpdate(productId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
            int quantity = requestedQuantities.get(productId);
            if (!product.isActive()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Product is not available");
            }
            if (product.getQuantity() < quantity) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Insufficient stock for product " + product.getName());
            }
            if (!Double.isFinite(product.getPrice()) || product.getPrice() < 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Product has an invalid price");
            }

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setPrice(product.getPrice());
            items.add(item);
            totalAmount += product.getPrice() * quantity;
            if (!Double.isFinite(totalAmount)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Order total is invalid");
            }
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setItems(items);
        order.setTotalAmount(totalAmount);
        order.setPaymentMethod(orderDto.getPaymentMethod());
        order.setStatus("CONFIRMED");
        order.setOrderDate(LocalDateTime.now());

        // Update product stock
        items.forEach(item -> item.getProduct().setQuantity(
                item.getProduct().getQuantity() - item.getQuantity()));

        Order savedOrder = orderRepository.save(order);
        cartItemRepository.deleteByCustomerEmail(customer.getEmail());

        return modelMapper.map(savedOrder, OrderDto.class);
    }

	
	
    public List<Order> getalluser(){
    	return orderRepository.findAll();
    }
    public List<OrderDto> getCustomerOrders(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        return orderRepository.findByCustomer(customer)
                .stream()
                .map(order -> modelMapper.map(order, OrderDto.class))
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findByIdForUpdate(orderId)
                .filter(o -> "CONFIRMED".equals(o.getStatus()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.CONFLICT, "Order not found or cannot be cancelled"));
        
        order.setStatus("CANCELLED");
        List<OrderItem> items = order.getItems().stream()
                .sorted((first, second) -> Long.compare(
                        first.getProduct().getId(), second.getProduct().getId()))
                .toList();
        for (OrderItem item : items) {
            Product product = productRepository.findByIdForUpdate(item.getProduct().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT,
                            "A product in this order no longer exists"));
            product.setQuantity(Math.addExact(product.getQuantity(), item.getQuantity()));
        }
    }

    public Optional<Order> findById(Long orderId) {
        return orderRepository.findById(orderId);
    }
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order);
    }

    
      }