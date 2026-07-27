package com.grocery.online_grocery_portal.model;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {
    private Order order;

    @BeforeEach
    public void setUp() {
        order = new Order();
    }

    @Test
    public void calculateTax() {
        //given: subtotal = $100
        order.setTotalAmount(100.00);

        //When: calculating tax
        double total = order.calculateTax(0.0825);

        //Then: total amount should be $108.25
        assertEquals(108.25, total, 0.001);
    }

    @Test
    public void applyDiscount() {
        //given: subtotal = $100
        order.setTotalAmount(100.00);

        //When: apply discount
        order.applyDiscount("SAVE10");

        //then: total amount should be $90
        assertEquals(90.00, order.getTotalAmount(), 0.001);
    }

    @Test
    public void placeOrder() {
        //given: subtotal = $100
        order.setTotalAmount(100.00);
        order.setDeliveryType("Delivery");

        //When: place order
        boolean result = order.placeOrder();

        //then
        assertTrue(result);
    }
}