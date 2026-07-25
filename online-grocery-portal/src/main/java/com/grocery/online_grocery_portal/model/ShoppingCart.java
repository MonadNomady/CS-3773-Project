package com.grocery.online_grocery_portal.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shopping_cart")
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cartID;

    @OneToOne
    @JoinColumn(name = "customer_id", unique = true)
    private Customer customer;

    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    //private double subtotal;

    public ShoppingCart() {}

    // TODO: Cart Arithmetic & Addition
    public void addItem(Product product, int quantity) {
        if (product == null || quantity <= 0 || !product.isAvailable()) {
            return;
        }

        for (CartItem item : items) {
            if (item.getProduct() != null && item.getProduct().getProductID() == product.getProductID()) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }

        CartItem newItem = new CartItem(product, quantity);
        newItem.setShoppingCart(this);
        items.add(newItem);
    }

    // TODO: Item Removal
    public void deleteItem(int productId) {
        if (this.items == null || this.items.isEmpty()) {
            return;
        }

        this.items.removeIf(item ->
                item != null &&
                        item.getProduct() != null &&
                        item.getProduct().getProductID() == productId
        );
    }

    public void deleteItem(Product product) {
        if (product == null) return;
        deleteItem(product.getProductID());
    }

    // TODO: Emptying Cart
    public void clearCart() {
        if (this.items != null) {
            this.items.clear();
        }
    }

    /*private void recalculateSubtotal() {
        double total = 0.0;
        for (CartItem item : items) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        subtotal = total;
    }*/

    public double getSubtotal() {
        if (this.items == null || this.items.isEmpty()) {
            return 0.0;
        }

        return this.items.stream()
                .filter(item -> item != null && item.getProduct() != null)
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }

    // Getters and Setters
    public int getCartID() { return cartID; }
    public void setCartID(int cartID) { this.cartID = cartID; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public List<CartItem> getItems() { return items; }
}
