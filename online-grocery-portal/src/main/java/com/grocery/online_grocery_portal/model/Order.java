package com.grocery.online_grocery_portal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderID;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    private Customer customer;

    private double totalAmount;
    private double taxAmount;
    private String discountCode;
    private String deliveryType;

    private LocalDateTime orderDate;

    public Order() {}

    @PrePersist
    protected void onCreate() {
        this.orderDate = LocalDateTime.now();
    }

    // Handle 8.25% Sales Tax Equation
    public double calculateTax(double rate) {
        taxAmount = (totalAmount * rate);
        totalAmount += taxAmount;
        return totalAmount;
    }

    // Promo/Coupon Logic
    public void applyDiscount(String code) {
        discountCode = code;

        switch (code.toUpperCase()) {
            case "SAVE10":
                totalAmount *= 0.90;
                break;
            case "SAVE20":
                totalAmount *= 0.80;
                break;
            case "5OFF":
                totalAmount -= 5.00;
                break;
            case "10OFF":
                totalAmount -= 10.00;
                break;
        }
    }

    // Order Fulfillment Lifecycle
    public boolean placeOrder() {
        if (this.totalAmount <= 0) {
            return false;
        }

        if (this.deliveryType == null || this.deliveryType.isEmpty()) {
            return false;
        }

        return true;
    }

    // Console/Log Print Layouts
    public void displaySummary() {
        System.out.println("=== ORDER SUMMARY ===");
        System.out.println("Order ID: " + this.orderID);
        System.out.println("Subtotal: $" + String.format("%.2f", this.totalAmount));
        System.out.println("Tax: $" + String.format("%.2f", this.taxAmount));
        System.out.println("Discount Code: " + this.discountCode);
        System.out.println("DeliveryType: " + this.deliveryType);
        System.out.println("Order Date: " + this.orderDate);
    }

    // Getters and Setters
    public int getOrderID() { return orderID; }
    public void setOrderID(int orderID) { this.orderID = orderID; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public int getCustomerId() {
        return customer != null ? customer.getCustomerID() : 0;
    }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(double taxAmount) { this.taxAmount = taxAmount; }

    public String getDiscountCode() { return discountCode; }
    public void setDiscountCode(String discountCode) { this.discountCode = discountCode; }

    public String getDeliveryType() { return deliveryType; }
    public void setDeliveryType(String deliveryType) { this.deliveryType = deliveryType; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

}