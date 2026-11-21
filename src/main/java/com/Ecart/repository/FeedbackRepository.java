package com.Ecart.repository;

import com.Ecart.model.Customer;
import com.Ecart.model.Feedback;
import com.Ecart.model.Product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

	  List<Feedback> findByProduct(Product product);       
    // 🔹 Average rating grouped by product name
    @Query(value = "SELECT p.name AS productName, AVG(f.rating) AS avgRating " +
                   "FROM feedback f " +
                   "JOIN orders_table o ON f.order_id = o.id " +
                   "JOIN customers c ON o.customer_id = c.id " +
                   "JOIN order_items oi ON o.id = oi.order_id " +
                   "JOIN product p ON oi.product_id = p.id " +
                   "GROUP BY p.name", nativeQuery = true)
    List<Map<String, Object>> findAverageRatingPerProduct();
   Optional<Feedback>findByCustomerAndProduct(Customer customer,Product product);
   
   


}
