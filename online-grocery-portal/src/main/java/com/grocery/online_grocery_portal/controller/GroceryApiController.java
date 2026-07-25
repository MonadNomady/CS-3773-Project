package com.grocery.online_grocery_portal.controller;

import com.grocery.online_grocery_portal.model.Customer;
import com.grocery.online_grocery_portal.model.Order;
import com.grocery.online_grocery_portal.model.Product;
import com.grocery.online_grocery_portal.model.ShoppingCart;
import com.grocery.online_grocery_portal.repository.ProductRepository;
import com.grocery.online_grocery_portal.service.AuthService;
import com.grocery.online_grocery_portal.service.CartService;
import com.grocery.online_grocery_portal.service.CustomerService;
import com.grocery.online_grocery_portal.service.OrderService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class GroceryApiController {

    private final AuthService authService;
    private final OrderService orderService;
    private final CartService cartService;
    private final CustomerService customerService;
    private final ProductRepository productRepository;

    public GroceryApiController(AuthService authService,
                                OrderService orderService,
                                CartService cartService,
                                CustomerService customerService,
                                ProductRepository productRepository) {
        this.authService = authService;
        this.orderService = orderService;
        this.cartService = cartService;
        this.customerService = customerService;
        this.productRepository = productRepository;
    }

    // ==========================================
    // 1. PRODUCT ENDPOINTS
    // ==========================================

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return productRepository.findByAvailableTrue();
    }

    @GetMapping("/products/search")
    public List<Product> searchProducts(@RequestParam("query") String query) {
        return productRepository.findByAvailableTrueAndNameContainingIgnoreCaseOrAvailableTrueAndDescriptionContainingIgnoreCase(query, query);
    }

    @GetMapping("/products/sort")
    public List<Product> getProductsSorted(@RequestParam("sortBy") String sortBy) {
        if ("priceAsc".equalsIgnoreCase(sortBy)) {
            return productRepository.findByAvailableTrue(Sort.by(Sort.Direction.ASC, "price"));
        } else if ("priceDesc".equalsIgnoreCase(sortBy)) {
            return productRepository.findByAvailableTrue(Sort.by(Sort.Direction.DESC, "price"));
        } else if ("availability".equalsIgnoreCase(sortBy)) {
            return productRepository.findAll(Sort.by(Sort.Direction.DESC, "available"));
        }
        return productRepository.findByAvailableTrue();
    }

    // ==========================================
    // 2. AUTHENTICATION ENDPOINTS
    // ==========================================

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Customer customer) {
        try {
            Customer savedCustomer = authService.register(customer);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and password required"));
        }

        try {
            Customer customer = authService.login(email, password);
            return ResponseEntity.ok(customer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }
// ==========================================
    // 3. CUSTOMER ADDRESS ENDPOINTS
    // ==========================================

    @GetMapping("/customers/{customerId}/addresses")
    public ResponseEntity<?> getAddresses(@PathVariable Integer customerId) {
        try {
            List<String> addresses = customerService.getDeliveryAddresses(customerId);
            return ResponseEntity.ok(addresses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/customers/{customerId}/addresses")
    public ResponseEntity<?> addAddress(
            @PathVariable Integer customerId,
            @RequestBody Map<String, String> request) {
        String address = request.get("address");
        if (address == null || address.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Address cannot be empty"));
        }

        try {
            Customer updatedCustomer = customerService.addAddress(customerId, address);
            return ResponseEntity.ok(updatedCustomer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/customers/{customerId}/addresses")
    public ResponseEntity<?> removeAddress(
            @PathVariable Integer customerId,
            @RequestParam String address) {
        try {
            Customer updatedCustomer = customerService.removeAddress(customerId, address);
            return ResponseEntity.ok(updatedCustomer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==========================================
    // 4. SHOPPING CART ENDPOINTS
    // ==========================================

    @GetMapping("/cart/{customerId}")
    public ResponseEntity<?> getCart(@PathVariable Integer customerId) {
        try {
            ShoppingCart cart = cartService.getCart(customerId);
            return ResponseEntity.ok(cart);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/cart/{customerId}/add")
    public ResponseEntity<?> addToCart(
            @PathVariable Integer customerId,
            @RequestParam Integer productId,
            @RequestParam(defaultValue = "1") Integer quantity) {
        try {
            ShoppingCart cart = cartService.addItemToCart(customerId, productId, quantity);
            return ResponseEntity.ok(cart);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/cart/{customerId}/remove/{productId}")
    public ResponseEntity<?> removeFromCart(
            @PathVariable Integer customerId,
            @PathVariable Integer productId) {
        try {
            ShoppingCart cart = cartService.removeItemFromCart(customerId, productId);
            return ResponseEntity.ok(cart);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/cart/{customerId}/clear")
    public ResponseEntity<?> clearCart(@PathVariable Integer customerId) {
        try {
            ShoppingCart cart = cartService.clearCart(customerId);
            return ResponseEntity.ok(cart);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==========================================
    // 5. ORDER & CHECKOUT ENDPOINTS
    // ==========================================

    @GetMapping("/orders/history/{customerId}")
    public ResponseEntity<?> getOrderHistory(
            @PathVariable Integer customerId,
            @RequestParam(required = false) String sortBy) {
        try {
            if ("date".equalsIgnoreCase(sortBy)) {
                return ResponseEntity.ok(orderService.getOrderHistorySortedByDate(customerId, true));
            } else if ("amount".equalsIgnoreCase(sortBy)) {
                return ResponseEntity.ok(orderService.getOrderHistorySortedByAmount(customerId));
            }
            return ResponseEntity.ok(orderService.getCustomerOrderHistory(customerId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/checkout/{customerId}")
    public ResponseEntity<?> checkout(
            @PathVariable Integer customerId,
            @RequestBody(required = false) Map<String, String> request) {

        String deliveryType = (request != null && request.containsKey("deliveryType"))
                ? request.get("deliveryType") : "STANDARD_DELIVERY";
        String promoCode = (request != null) ? request.get("promoCode") : null;

        try {
            Order order = orderService.placeOrder(customerId, deliveryType, promoCode);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}