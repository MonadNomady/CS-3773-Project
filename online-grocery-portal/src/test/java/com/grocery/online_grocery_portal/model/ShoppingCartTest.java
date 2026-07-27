package com.grocery.online_grocery_portal.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ShoppingCartTest {
    private ShoppingCart cart;
    private Product product;

    @BeforeEach
    public void setUp() {
        cart = new ShoppingCart();
        product = new Product();
    }

    @Test
    void addItem() {
        //given: new product
        product.setProductID(1);
        product.setAvailable(true);

        //when: add item to cart
        cart.addItem(product, 1);

        //then
        assertEquals(product, cart.getItems().get(0).getProduct());
    }

    @Test
    void deleteItem_ShouldRemoveProductFromCart() {
        //given: new product
        product.setProductID(1);
        product.setAvailable(true);
        cart.addItem(product, 1);

        //when: delete item from cart
        cart.deleteItem(product);

        //then
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void clearCart_WithMultipleProducts_ShouldRemoveAllProducts() {
        // Given: Multiple products in the cart
        Product secondProduct = new Product();
        secondProduct.setProductID(2);
        secondProduct.setAvailable(true);

        cart.addItem(product, 1);
        cart.addItem(secondProduct, 2);

        // When: The cart is cleared
        cart.clearCart();

        // Then: No products should remain
        assertTrue(cart.getItems().isEmpty());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void addItem_WithUnavailableProduct_ShouldNotAddProduct() {
        // Given: An unavailable product
        product.setAvailable(false);

        // When: The product is added to the cart
        cart.addItem(product, 1);

        // Then: The shopping cart should remain empty
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void addItem_WithZeroQuantity_ShouldNotAddProduct() {
        // Given: An available product with a quantity of zero
        int quantity = 0;

        // When: The product is added to the cart
        cart.addItem(product, quantity);

        // Then: The shopping cart should remain empty
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void addItem_WithMultipleProducts_ShouldStoreBothProducts() {
        // Given: Two available products
        Product secondProduct = new Product();
        secondProduct.setProductID(2);
        secondProduct.setAvailable(true);

        // When: Both products are added
        cart.addItem(product, 1);
        cart.addItem(secondProduct, 2);

        // Then: The cart should contain two items
        assertEquals(2, cart.getItems().size());
    }

    @Test
    void addItem_WithSameProduct_ShouldIncreaseQuantity() {
        // Given: A product already exists in the cart
        cart.addItem(product, 1);

        // When: The same product is added again
        cart.addItem(product, 2);

        // Then: There should be one cart item with quantity 3
        assertEquals(1, cart.getItems().size());
        assertEquals(3, cart.getItems().get(0).getQuantity());
    }


    @Test
    void getSubtotal_WithMultipleProducts_ShouldReturnCorrectTotal() {
        // Given: Two products with prices and quantities
        product.setPrice(5.00);

        Product secondProduct = new Product();
        secondProduct.setProductID(2);
        secondProduct.setAvailable(true);
        secondProduct.setPrice(3.00);

        cart.addItem(product, 2);       // $5 × 2 = $10
        cart.addItem(secondProduct, 3); // $3 × 3 = $9

        // When: The subtotal is calculated
        double subtotal = cart.getSubtotal();

        // Then: The subtotal should be $19
        assertEquals(19.00, subtotal, 0.001);
    }
}