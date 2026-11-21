package com.Ecart.service;

import com.Ecart.repository.CustomerRepository;
import com.Ecart.repository.OrderRepository;
import com.Ecart.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DashboardService {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    
    
     public DashboardService(CustomerRepository customerRepository, ProductRepository productRepository,
	 		OrderRepository orderRepository) {
	 	super();
	 	this.customerRepository = customerRepository;
	 	this.productRepository = productRepository;
	 	this.orderRepository = orderRepository;
	 }

	public Map<String, Object> getDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalCustomers", customerRepository.count());
        summary.put("totalProducts", productRepository.count());
        summary.put("totalOrders", orderRepository.count());
        summary.put("totalRevenue", orderRepository.findAll().stream()
                .mapToDouble(o -> o.getTotalAmount())
                .sum());
        return summary;
    }

    public List<Map<String, Object>> getTopSellingProducts() {
        return orderRepository.findTopSellingProducts();
    }

    public List<Map<String, Object>> getMonthlySales() {
        return orderRepository.getMonthlySales();
    }
}
