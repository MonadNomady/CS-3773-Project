package com.grocery.online_grocery_portal.service;

import com.grocery.online_grocery_portal.model.Customer;
import com.grocery.online_grocery_portal.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.mockito.Mockito.mock;

public class AuthServiceTest {

    private CustomerRepository customerRepository;
    private AuthService authService;
    private Customer customer;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        authService = new AuthService(customerRepository);

        customer = new Customer();
        customer.setName("John Smith");
        customer.setEmail("john@example.com");
        customer.setPassword("password123");
        customer.addAddress("1 UTSA Cir");
    }

    @Test
    void register_WithUnusedEmail_ShouldSaveCustomer() {
        // Given: The email is not already registered
        when(customerRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.empty());

        when(customerRepository.save(customer))
                .thenReturn(customer);

        // When: The customer registers
        Customer result = authService.register(customer);

        // Then: The customer should be saved and returned
        assertNotNull(result);
        assertEquals("john@example.com", result.getEmail());
        verify(customerRepository).save(customer);
    }

    @Test
    void register_WithExistingEmail_ShouldThrowException() {
        // Given: A customer already uses this email
        Customer existingCustomer = new Customer();
        existingCustomer.setEmail("john@example.com");

        when(customerRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(existingCustomer));

        // When: Registration is attempted
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(customer)
        );

        // Then: Registration should fail
        assertEquals("Email already in use", exception.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void login_WithCorrectCredentials_ShouldReturnCustomer() {
        // Given: A customer exists with the correct credentials
        when(customerRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(customer));

        // When: The customer logs in
        Customer result = authService.login(
                "john@example.com",
                "password123"
        );

        // Then: The matching customer should be returned
        assertNotNull(result);
        assertSame(customer, result);
        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    void login_WithIncorrectPassword_ShouldThrowException() {
        // Given: A customer exists with the provided email
        when(customerRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(customer));

        // When: The customer enters an incorrect password
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(
                        "john@example.com",
                        "wrongPassword"
                )
        );

        // Then: Login should fail
        assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );
    }
}
