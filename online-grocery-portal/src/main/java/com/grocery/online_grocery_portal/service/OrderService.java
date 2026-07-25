package com.grocery.online_grocery_portal.service;

import com.grocery.online_grocery_portal.model.Customer;
import com.grocery.online_grocery_portal.model.Order;
import com.grocery.online_grocery_portal.model.ShoppingCart;
import com.grocery.online_grocery_portal.repository.CustomerRepository;
import com.grocery.online_grocery_portal.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private static final double SALES_TAX_RATE = 0.0825;

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        CartService cartService) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.cartService = cartService;
    }

    @Transactional
    public Order placeOrder(int customerId) {
        return placeOrder(customerId, "STANDARD_DELIVERY", null);
    }

    @Transactional
    public Order placeOrder(int customerId, String deliveryType, String promoCode) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        ShoppingCart cart = customer.getShoppingCart();
        if (cart == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot place order with an empty cart");
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setDeliveryType(deliveryType);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(cart.getSubtotal());

        if (promoCode != null && !promoCode.isBlank()) {
            order.applyDiscount(promoCode); // Applies discount code logic
        }

        order.calculateTax(SALES_TAX_RATE);

        if (!order.placeOrder()) {
            throw new IllegalStateException("Invalid order state: check total amount or delivery type");
        }

        Order savedOrder = orderRepository.save(order);

        savedOrder.displaySummary();

        cartService.clearCart(customerId);

        return savedOrder;
    }

    @Transactional(readOnly = true)
    public List<Order> getCustomerOrderHistory(int customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new IllegalArgumentException("Customer not found with ID: " + customerId);
        }
        return orderRepository.findByCustomer_CustomerID(customerId);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrderHistorySortedByDate(int customerId, boolean newestFirst) {
        if (newestFirst) {
            return orderRepository.findByCustomer_CustomerIDOrderByOrderDateDesc(customerId);
        }
        return orderRepository.findByCustomer_CustomerIDOrderByOrderDateAsc(customerId);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrderHistorySortedByAmount(int customerId) {
        return orderRepository.findByCustomer_CustomerIDOrderByTotalAmountDesc(customerId);
    }

    @Transactional(readOnly = true)
    public Order getOrderById(int orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
    }
}