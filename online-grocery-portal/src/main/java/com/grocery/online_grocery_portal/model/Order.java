package com.grocery.online_grocery_portal.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "customer_order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderID;

    private double totalAmount;
    private double taxAmount;
    private String discountCode;
    private String deliveryType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate;

    // TODO: Handle 8.25% Sales Tax Equation
    public double calculateTax(double rate) {
        taxAmount = (totalAmount * rate);
        totalAmount += taxAmount;
        return totalAmount;
    }

    // TODO: Promo/Coupon Logic
    public void applyDiscount(String code) {
        discountCode = code;
    }

    // TODO: Order Fulfillment Lifecycle
    public boolean placeOrder() {
        if (totalAmount <= 0) {
            return false;
        }

        if (deliveryType == null || deliveryType.isEmpty()) {
            return false;
        }

        orderDate = new Date();

        return true;
    }

    // TODO: Console/Log Print Layouts
    public void displaySummary() {
        System.out.println("Order ID: " + orderID);
        System.out.println("Subtotal: $" + totalAmount);
        System.out.println("Tax: $" + taxAmount);
        System.out.println("Discount Code: " + discountCode);
        System.out.println("DeliveryType: " + deliveryType);
        System.out.println("Order Date: " + orderDate);
    }

    // Getters and Setters
    public int getOrderID() { return orderID; }
    public void setOrderID(int orderID) { this.orderID = orderID; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(double taxAmount) { this.taxAmount = taxAmount; }

    public String getDiscountCode() { return discountCode; }
    public void setDiscountCode(String discountCode) { this.discountCode = discountCode; }

    public String getDeliveryType() { return deliveryType; }
    public void setDeliveryType(String deliveryType) { this.deliveryType = deliveryType; }

    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }
}