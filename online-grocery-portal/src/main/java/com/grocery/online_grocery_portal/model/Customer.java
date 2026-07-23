package com.grocery.online_grocery_portal.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int customerID;

    private String name;
    private String email;
    private String password;

    @ElementCollection
    @CollectionTable(
            name = "customer_addresses",
            joinColumns = @JoinColumn(name = "customer_id")
    )
    @Column(name = "address")
    private List<String> deliveryAddresses = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cart_id")
    private ShoppingCart shoppingCart;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "history_id")
    private OrderHistory orderHistory;

    // TODO: Registration Logic
    public boolean register() {

        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        if (!email.contains("@") || !email.contains(".")) {
            return false;
        }

        if (password == null || password.trim().isEmpty()) {
            return false;
        }

        if (deliveryAddresses == null || deliveryAddresses.isEmpty()) {
            return false;
        }

        if (shoppingCart == null) {
            shoppingCart = new ShoppingCart();
        }

        if (orderHistory == null) {
            orderHistory = new OrderHistory();
        }

        return true;
    }

    // TODO: Login / Auth Logic
    public boolean login() {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        if (password == null || password.trim().isEmpty()) {
            return false;
        }

        return true;
    }

    // TODO: Address Management
    public void addAddress(String address) {
        if (address != null && !address.trim().isEmpty()) {
            String cleanedAddress = address.trim();

            if (!deliveryAddresses.contains(cleanedAddress)) {
                deliveryAddresses.add(cleanedAddress);
            }
        }
    }

    public boolean removeAddress(String address) {
        if (address == null) {
            return false;
        }

        return deliveryAddresses.remove(address.trim());
    }

    // Getters and Setters
    public int getCustomerID() { return customerID; }
    public void setCustomerID(int customerID) { this.customerID = customerID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public List<String> getDeliveryAddresses() { return deliveryAddresses; }

    public void setDeliveryAddresses(List<String> deliveryAddresses) {
        if (deliveryAddresses == null) {
            this.deliveryAddresses = new ArrayList<>();
        } else {
            this.deliveryAddresses = deliveryAddresses;
        }
    }

    public ShoppingCart getShoppingCart() { return shoppingCart; }
    public void setShoppingCart(ShoppingCart shoppingCart) { this.shoppingCart = shoppingCart; }

    public OrderHistory getOrderHistory() { return orderHistory; }
    public void setOrderHistory(OrderHistory orderHistory) { this.orderHistory = orderHistory; }
}