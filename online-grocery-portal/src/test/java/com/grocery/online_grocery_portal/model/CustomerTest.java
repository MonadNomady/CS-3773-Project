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

    @Test
    void addAddress_WithBlankAddress_ShouldNotAddAddress() {
        // Given: A blank address
        String address = "   ";

        // When: The blank address is added
        customer.addAddress(address);

        // Then: The address list should remain empty
        assertTrue(customer.getDeliveryAddresses().isEmpty());
    }

    @Test
    void addAddress_WithNullAddress_ShouldNotAddAddress() {
        // Given: A null address
        String address = null;

        // When: The null address is added
        customer.addAddress(address);

        // Then: The address list should remain empty
        assertTrue(customer.getDeliveryAddresses().isEmpty());
    }

    @Test
    void addAddress_WithExtraSpaces_ShouldStoreTrimmedAddress() {
        // Given: An address with extra spaces
        String address = "   1 UTSA Cir   ";

        // When: The address is added
        customer.addAddress(address);

        // Then: The address should be stored without extra spaces
        assertEquals(1, customer.getDeliveryAddresses().size());
        assertEquals(
                "1 UTSA Cir",
                customer.getDeliveryAddresses().get(0)
        );
    }

    @Test
    void addAddress_WithDuplicateAddress_ShouldNotAddDuplicate() {
        // Given: A customer with a saved address
        customer.addAddress("1 UTSA Cir");

        // When: The same address is added again
        customer.addAddress("1 UTSA Cir");

        // Then: The address should appear only once
        assertEquals(1, customer.getDeliveryAddresses().size());
    }

    @Test
    void removeAddress_WithExtraSpaces_ShouldRemoveAddress() {
        // Given: A customer with a saved address
        customer.addAddress("1 UTSA Cir");

        // When: The address is removed with extra spaces
        boolean result = customer.removeAddress("   1 UTSA Cir   ");

        // Then: The trimmed address should be removed
        assertTrue(result);
        assertTrue(customer.getDeliveryAddresses().isEmpty());
    }
}