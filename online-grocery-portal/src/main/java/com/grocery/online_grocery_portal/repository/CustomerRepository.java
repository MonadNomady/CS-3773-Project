package com.grocery.online_grocery_portal.repository;

import com.grocery.online_grocery_portal.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Optional<Customer> findByEmail(String email);
    boolean existsByEmail(String email);
}