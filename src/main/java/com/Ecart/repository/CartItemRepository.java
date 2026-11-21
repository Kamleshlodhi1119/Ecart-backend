package com.Ecart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Ecart.model.CartItem;

public interface CartItemRepository  extends JpaRepository<CartItem,Long>{
	public List<CartItem> findByCustomerEmail(String customerEmail);
	java.util.Optional<CartItem> findByProductIdAndCustomerEmail(Long productId, String customerEmail);
	void deleteByProductIdAndCustomerEmail(Long productId, String customerEmail);
	void deleteByCustomerEmail(String customerEmail);


}
