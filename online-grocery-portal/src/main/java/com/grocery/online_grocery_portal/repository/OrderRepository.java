package com.grocery.online_grocery_portal.repository;

import com.grocery.online_grocery_portal.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    // Default history
    List<Order> findByCustomer_CustomerID(int customerId);

    // Sort by order date descending (newest first)
    List<Order> findByCustomer_CustomerIDOrderByOrderDateDesc(int customerId);

    // Sort by order date ascending (oldest first)
    List<Order> findByCustomer_CustomerIDOrderByOrderDateAsc(int customerId);

    // Sort by dollar amount descending (highest total first)
    List<Order> findByCustomer_CustomerIDOrderByTotalAmountDesc(int customerId);
}