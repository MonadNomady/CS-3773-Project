package com.grocery.online_grocery_portal.service;

import com.grocery.online_grocery_portal.model.CartItem;
import com.grocery.online_grocery_portal.model.Customer;
import com.grocery.online_grocery_portal.model.Product;
import com.grocery.online_grocery_portal.model.ShoppingCart;
import com.grocery.online_grocery_portal.repository.CustomerRepository;
import com.grocery.online_grocery_portal.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public CartService(CustomerRepository customerRepository, ProductRepository productRepository) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public ShoppingCart getCart(int customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));

        ShoppingCart cart = customer.getShoppingCart();
        if (cart == null) {
            cart = new ShoppingCart();
            customer.setShoppingCart(cart);
            customerRepository.save(customer);
        }
        return cart;
    }

    @Transactional
    public ShoppingCart addItemToCart(int customerId, int productId, int quantity) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        if (!product.isAvailable()) {
            throw new IllegalArgumentException("Product is currently unavailable: " + product.getName());
        }

        ShoppingCart cart = customer.getShoppingCart();
        if (cart == null) {
            cart = new ShoppingCart();
            customer.setShoppingCart(cart);
        }

        cart.addItem(product, quantity);

        customerRepository.save(customer);
        return cart;
    }

    @Transactional
    public ShoppingCart removeItemFromCart(int customerId, int productId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));

        ShoppingCart cart = customer.getShoppingCart();
        if (cart != null) {
            cart.deleteItem(productId);
            customerRepository.save(customer);
        }
        return cart;
    }

    @Transactional
    public ShoppingCart clearCart(int customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));

        ShoppingCart cart = customer.getShoppingCart();
        if (cart != null) {
            cart.clearCart();
            customerRepository.save(customer);
        }
        return cart;
    }

    @Transactional(readOnly = true)
    public double getCartSubtotal(int customerId) {
        ShoppingCart cart = getCart(customerId);
        return cart.getSubtotal();
    }
}