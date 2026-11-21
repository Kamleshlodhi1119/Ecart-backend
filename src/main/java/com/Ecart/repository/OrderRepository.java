package com.Ecart.repository;

import com.Ecart.model.Customer;
import com.Ecart.model.Order;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomer(Customer customer);

    List<Order> findByStatus(String status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Order o where o.id = :id")
    Optional<Order> findByIdForUpdate(@Param("id") Long id);

    // 🔹 Get top-selling products with total quantity sold
    @Query(value = "SELECT p.name AS product, SUM(oi.quantity) AS totalSold " +
                   "FROM orders_table o " +
                   "JOIN order_items oi ON o.id = oi.order_id " +
                   "JOIN product p ON oi.product_id = p.id " +
                   "GROUP BY p.name " +
                   "ORDER BY totalSold DESC " +
                   "LIMIT 5", nativeQuery = true)
    List<Map<String, Object>> findTopSellingProducts();

    // 🔹 Get monthly sales totals (total revenue per month)
    @Query(value = "SELECT MONTH(o.order_date) AS month, SUM(o.total_amount) AS totalSales " +
                   "FROM orders_table o " +
                   "GROUP BY MONTH(o.order_date)", nativeQuery = true)
    List<Map<String, Object>> getMonthlySales();
}
