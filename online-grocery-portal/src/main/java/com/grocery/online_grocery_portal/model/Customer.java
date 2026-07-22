package com.grocery.online_grocery_portal.model;

import jakarta.persistence.*;

@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int customerID;

    private String name;
    private String email;
    private String password;
    private String deliveryAddress;

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

        if (deliveryAddress == null || deliveryAddress.trim().isEmpty()) {
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
        this.deliveryAddress = address;
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

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public ShoppingCart getShoppingCart() { return shoppingCart; }
    public void setShoppingCart(ShoppingCart shoppingCart) { this.shoppingCart = shoppingCart; }

    public OrderHistory getOrderHistory() { return orderHistory; }
    public void setOrderHistory(OrderHistory orderHistory) { this.orderHistory = orderHistory; }
}