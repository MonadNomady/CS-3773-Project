package com.grocery.online_grocery_portal.repository;

import com.grocery.online_grocery_portal.model.Product;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    // Get all available products
    List<Product> findByAvailableTrue();

    // Search by name or description
    List<Product> findByAvailableTrueAndNameContainingIgnoreCaseOrAvailableTrueAndDescriptionContainingIgnoreCase(String name, String description);

    // Fetch products with custom sorting
    List<Product> findByAvailableTrue(Sort sort);
}