package com.grocery.online_grocery_portal.model;

import jakarta.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cartID;

    @ElementCollection
    @CollectionTable(name = "cart_items", joinColumns = @JoinColumn(name = "cart_id"))
    @MapKeyJoinColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<Product, Integer> items = new HashMap<>();

    private double subtotal;

    public Map<Product, Integer> getItems() {
        return items;
    }

    // TODO: Cart Arithmetic & Addition
    public void addItem(Product product, int quantity) {
    if (product == null || quantity <= 0) {
            return;
        }
        if (!product.isAvailable()) {
            return;
        }
        items.merge(product, quantity, Integer::sum);
        recalculateSubtotal();
    }

    // TODO: Item Removal
    public void deleteItem(Product product) {
        items.remove(product);
        recalculateSubtotal();

    }

    // TODO: Emptying Cart
    public void clearCart() {
        items.clear();
        subtotal = 0.0;
    }

    private void recalculateSubtotal() {
        double total = 0.0;
        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
            total += entry.getKey().getPrice() * entry.getValue();
        }
        subtotal = total;
    }

    // Getters and Setters
    public int getCartID() { return cartID; }
    public void setCartID(int cartID) { this.cartID = cartID; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}
