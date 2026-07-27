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
    void calculateTax_ShouldAdd825PercentTax() {
        //given: subtotal = $100
        order.setTotalAmount(100.00);

        //When: calculating tax
        double total = order.calculateTax(0.0825);

        //Then: total amount should be $108.25
        assertEquals(108.25, total, 0.001);
    }

    @Test
    void applyDiscount_WithSave10_ShouldReduceTotal() {
        //given: subtotal = $100
        order.setTotalAmount(100.00);

        //When: apply discount
        order.applyDiscount("SAVE10");

        //then: total amount should be $90
        assertEquals(90.00, order.getTotalAmount(), 0.001);
    }

    @Test
    void placeOrder_WithValidOrder_ShouldReturnTrue() {
        //given: subtotal = $100
        order.setTotalAmount(100.00);
        order.setDeliveryType("Delivery");

        //When: place order
        boolean result = order.placeOrder();

        //then
        assertTrue(result);
    }

    @Test
    void applyDiscount_WithInvalidCode_ShouldKeepOriginalTotal() {
        // Given: An order subtotal of $100.00
        order.setTotalAmount(100.00);

        // When: An invalid discount code is applied
        order.applyDiscount("INVALID");

        // Then: The total should remain unchanged
        assertEquals(100.00, order.getTotalAmount(), 0.001);
    }

    @Test
    void placeOrder_WithoutDeliveryType_ShouldReturnFalse() {
        // Given: An order with a total but no delivery type
        order.setTotalAmount(100.00);

        // When: The order is placed
        boolean result = order.placeOrder();

        // Then: The order should not be placed
        assertFalse(result);
    }

    @Test
    void placeOrder_WithZeroTotal_ShouldReturnFalse() {
        // Given: An order with no purchase amount
        order.setTotalAmount(0.00);
        order.setDeliveryType("Delivery");

        // When: The order is placed
        boolean result = order.placeOrder();

        // Then: The order should not be placed
        assertFalse(result);
    }
}