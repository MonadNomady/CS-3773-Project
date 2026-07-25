package com.grocery.online_grocery_portal.service;

import com.grocery.online_grocery_portal.model.Customer;
import com.grocery.online_grocery_portal.model.ShoppingCart;
import com.grocery.online_grocery_portal.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class AuthService {

    private final CustomerRepository customerRepository;

    public AuthService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Customer register(Customer customer) {
        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        ShoppingCart cart = new ShoppingCart();
        customer.setShoppingCart(cart);

        // TODO: Password hashing should be applied here
        return customerRepository.save(customer);
    }

    @Transactional
    public Customer login(String email, String password) {
        Optional<Customer> customerOpt = customerRepository.findByEmail(email);

        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            // TODO: Replace plain-text check
            if (customer.getPassword().equals(password)) {
                return customer;
            }
        }
        throw new IllegalArgumentException("Invalid email or password");
    }
}