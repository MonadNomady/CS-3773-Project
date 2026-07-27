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
    public void addItem() {
        //given: new product
        product.setProductID(1);
        product.setAvailable(true);

        //when: add item to cart
        cart.addItem(product, 1);

        //then
        assertEquals(product, cart.getItems().get(0).getProduct());
    }

    @Test
    public void deleteItem() {
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
    public void clearCart() {
        //given: products added to cart
        product.setProductID(1);
        product.setAvailable(true);
        cart.addItem(product, 1);

        //when: clear cart
        cart.clearCart();

        //then
        assertTrue(cart.getItems().isEmpty());
    }
}