package com.grocery.online_grocery_portal.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerTest {
    private Customer customer;

    @BeforeEach
    public void setUp() {
        customer = new Customer();
    }

    @Test
    public void addAddress() {
        //When: add address
        customer.addAddress("1 UTSA Cir");

        //then
        assertEquals("1 UTSA Cir", customer.getDeliveryAddresses().get(0));
    }

    @Test
    public void removeAddress() {
        //given: customer address added
        customer.addAddress("1 UTSA Cir");

        //When: remove address
        customer.removeAddress("1 UTSA Cir");

        //then
        assertTrue(customer.getDeliveryAddresses().isEmpty());
    }
}