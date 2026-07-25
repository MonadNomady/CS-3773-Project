package com.grocery.online_grocery_portal.service;

import com.grocery.online_grocery_portal.model.Customer;
import com.grocery.online_grocery_portal.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public Customer getCustomerById(int customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));
    }

    @Transactional
    public Customer addAddress(int customerId, String address) {
        Customer customer = getCustomerById(customerId);

        customer.addAddress(address);

        return customerRepository.save(customer);
    }

    @Transactional
    public Customer removeAddress(int customerId, String address) {
        Customer customer = getCustomerById(customerId);

        boolean removed = customer.removeAddress(address);
        if (!removed) {
            throw new IllegalArgumentException("Address not found in customer profile");
        }

        return customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public List<String> getDeliveryAddresses(int customerId) {
        Customer customer = getCustomerById(customerId);
        return customer.getDeliveryAddresses();
    }
}