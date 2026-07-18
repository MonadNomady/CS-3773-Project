package com.grocery.online_grocery_portal.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
public class OrderHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int historyID;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "history_id")
    private List<Order> pastOrders = new ArrayList<>();

    // TODO: Fetching Orders
    public List<Order> getOrders(int customerID) {
        return pastOrders;
    }

    // TODO: Sorting Logic (Date Comparator)
    public void sortByDate(boolean ascending) {
        if (ascending) {
            pastOrders.sort(Comparator.comparing(Order::getOrderDate));
        }
        else {
            pastOrders.sort(Comparator.comparing(Order::getOrderDate).reversed());
        }
    }

    // TODO: Sorting Logic (Price Comparator)
    public void sortByPrice(boolean ascending) {
        if (ascending) {
            pastOrders.sort(Comparator.comparingDouble(Order::getTotalAmount));
        }
        else {
            pastOrders.sort(Comparator.comparingDouble(Order::getTotalAmount).reversed());
        }
    }

    // Getters and Setters
    public int getHistoryID() { return historyID; }
    public void setHistoryID(int historyID) { this.historyID = historyID; }

    public List<Order> getPastOrders() { return pastOrders; }
    public void setPastOrders(List<Order> pastOrders) { this.pastOrders = pastOrders; }
}