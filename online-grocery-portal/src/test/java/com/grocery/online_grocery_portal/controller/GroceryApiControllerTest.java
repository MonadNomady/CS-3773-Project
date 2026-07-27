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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.closeTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class GroceryApiControllerTest {

    private AuthService authService;
    private OrderService orderService;
    private CartService cartService;
    private CustomerService customerService;
    private ProductRepository productRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Mock dependencies
        authService = mock(AuthService.class);
        orderService = mock(OrderService.class);
        cartService = mock(CartService.class);
        customerService = mock(CustomerService.class);
        productRepository = mock(ProductRepository.class);

        // Create the controller using the mock dependencies
        GroceryApiController controller = new GroceryApiController(
                authService,
                orderService,
                cartService,
                customerService,
                productRepository
        );

        // Create MockMvc without loading the entire Spring application
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    // ==================================================
    // Product tests
    // ==================================================

    @Test
    void getAllProducts_ShouldReturnProducts() throws Exception {
        // Given: Two products exist
        Product firstProduct = new Product();
        firstProduct.setProductID(1);

        Product secondProduct = new Product();
        secondProduct.setProductID(2);

        when(productRepository.findAll())
                .thenReturn(List.of(firstProduct, secondProduct));

        // When and Then: The products endpoint should return both products
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].productID").value(1))
                .andExpect(jsonPath("$[1].productID").value(2));

        verify(productRepository).findAll();
    }

    @Test
    void getAllProducts_WithAvailableOnly_ShouldReturnAvailableProducts()
            throws Exception {

        // Given: One available product exists
        Product product = new Product();
        product.setProductID(1);
        product.setAvailable(true);

        when(productRepository.findByAvailableTrue())
                .thenReturn(List.of(product));

        // When and Then: Only available products should be returned
        mockMvc.perform(get("/api/products")
                        .param("availableOnly", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].productID").value(1))
                .andExpect(jsonPath("$[0].available").value(true));

        verify(productRepository).findByAvailableTrue();
        verify(productRepository, never()).findAll();
    }

    @Test
    void searchProducts_ShouldReturnMatchingProducts() throws Exception {
        // Given: A matching product exists
        Product product = new Product();
        product.setProductID(3);

        when(productRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        "milk",
                        "milk"
                ))
                .thenReturn(List.of(product));

        // When and Then: The search endpoint should return the match
        mockMvc.perform(get("/api/products/search")
                        .param("query", "milk"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].productID").value(3));

        verify(productRepository)
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        "milk",
                        "milk"
                );
    }

    // ==================================================
    // Registration and login tests
    // ==================================================

    @Test
    void register_WithValidCustomer_ShouldReturnCreated() throws Exception {
        // Given: The registration service saves a customer
        Customer savedCustomer = new Customer();
        savedCustomer.setCustomerID(1);
        savedCustomer.setName("John Smith");
        savedCustomer.setEmail("john@example.com");
        savedCustomer.setPassword("password123");

        when(authService.register(any(Customer.class)))
                .thenReturn(savedCustomer);

        String requestBody = """
                {
                    "name": "John Smith",
                    "email": "john@example.com",
                    "password": "password123"
                }
                """;

        // When and Then: Registration should return HTTP 201
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerID").value(1))
                .andExpect(jsonPath("$.name").value("John Smith"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(authService).register(any(Customer.class));
    }

    @Test
    void register_WithExistingEmail_ShouldReturnConflict() throws Exception {
        // Given: The email is already registered
        when(authService.register(any(Customer.class)))
                .thenThrow(new IllegalArgumentException(
                        "Email already in use"
                ));

        String requestBody = """
                {
                    "name": "John Smith",
                    "email": "john@example.com",
                    "password": "password123"
                }
                """;

        // When and Then: Registration should return HTTP 409
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error")
                        .value("Email already in use"));

        verify(authService).register(any(Customer.class));
    }

    @Test
    void login_WithCorrectCredentials_ShouldReturnCustomer()
            throws Exception {

        // Given: The credentials are valid
        Customer customer = new Customer();
        customer.setCustomerID(1);
        customer.setEmail("john@example.com");
        customer.setPassword("password123");

        when(authService.login(
                "john@example.com",
                "password123"
        )).thenReturn(customer);

        String requestBody = """
                {
                    "email": "john@example.com",
                    "password": "password123"
                }
                """;

        // When and Then: Login should return HTTP 200
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerID").value(1))
                .andExpect(jsonPath("$.email")
                        .value("john@example.com"));

        verify(authService).login(
                "john@example.com",
                "password123"
        );
    }

    @Test
    void login_WithMissingPassword_ShouldReturnBadRequest()
            throws Exception {

        // Given: The request contains an email but no password
        String requestBody = """
                {
                    "email": "john@example.com"
                }
                """;

        // When and Then: Login should return HTTP 400
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Email and password required"));

        verifyNoInteractions(authService);
    }

    @Test
    void login_WithIncorrectPassword_ShouldReturnUnauthorized()
            throws Exception {

        // Given: The authentication service rejects the credentials
        when(authService.login(
                "john@example.com",
                "wrongPassword"
        )).thenThrow(new IllegalArgumentException(
                "Invalid email or password"
        ));

        String requestBody = """
                {
                    "email": "john@example.com",
                    "password": "wrongPassword"
                }
                """;

        // When and Then: Login should return HTTP 401
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error")
                        .value("Invalid email or password"));
    }

    // ==================================================
    // Address tests
    // ==================================================

    @Test
    void addAddress_WithValidAddress_ShouldReturnCustomer()
            throws Exception {

        // Given: The customer exists and the address can be added
        Customer customer = new Customer();
        customer.setCustomerID(1);
        customer.addAddress("1 UTSA Cir");

        when(customerService.addAddress(1, "1 UTSA Cir"))
                .thenReturn(customer);

        String requestBody = """
                {
                    "address": "1 UTSA Cir"
                }
                """;

        // When and Then: The address should be added
        mockMvc.perform(post("/api/customers/{customerId}/addresses", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerID").value(1))
                .andExpect(jsonPath("$.deliveryAddresses[0]")
                        .value("1 UTSA Cir"));

        verify(customerService).addAddress(1, "1 UTSA Cir");
    }

    @Test
    void addAddress_WithBlankAddress_ShouldReturnBadRequest()
            throws Exception {

        // Given: The address contains only spaces
        String requestBody = """
                {
                    "address": "   "
                }
                """;

        // When and Then: The request should return HTTP 400
        mockMvc.perform(post("/api/customers/{customerId}/addresses", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Address cannot be empty"));

        verifyNoInteractions(customerService);
    }

    @Test
    void getAddresses_WithUnknownCustomer_ShouldReturnNotFound()
            throws Exception {

        // Given: The customer does not exist
        when(customerService.getDeliveryAddresses(99))
                .thenThrow(new IllegalArgumentException(
                        "Customer not found"
                ));

        // When and Then: The request should return HTTP 404
        mockMvc.perform(get(
                        "/api/customers/{customerId}/addresses",
                        99
                ))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Customer not found"));
    }

    // ==================================================
    // Shopping cart tests
    // ==================================================

    @Test
    void getCart_WithValidCustomer_ShouldReturnCart()
            throws Exception {

        // Given: The customer has a shopping cart
        ShoppingCart cart = new ShoppingCart();
        cart.setCartID(5);

        when(cartService.getCart(1))
                .thenReturn(cart);

        // When and Then: The cart should be returned
        mockMvc.perform(get("/api/cart/{customerId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartID").value(5));

        verify(cartService).getCart(1);
    }

    @Test
    void addToCart_WithValidProduct_ShouldReturnCart()
            throws Exception {

        // Given: The service successfully adds the product
        ShoppingCart cart = new ShoppingCart();
        cart.setCartID(5);

        when(cartService.addItemToCart(1, 10, 2))
                .thenReturn(cart);

        // When and Then: The cart should be returned
        mockMvc.perform(post("/api/cart/{customerId}/add", 1)
                        .param("productId", "10")
                        .param("quantity", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartID").value(5));

        verify(cartService).addItemToCart(1, 10, 2);
    }

    @Test
    void addToCart_WithInvalidProduct_ShouldReturnBadRequest()
            throws Exception {

        // Given: The product cannot be added
        when(cartService.addItemToCart(1, 99, 1))
                .thenThrow(new IllegalArgumentException(
                        "Product not found"
                ));

        // When and Then: The request should return HTTP 400
        mockMvc.perform(post("/api/cart/{customerId}/add", 1)
                        .param("productId", "99")
                        .param("quantity", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Product not found"));
    }

    // ==================================================
    // Checkout and order-history tests
    // ==================================================

    @Test
    void calculateTotals_WithSave10AndExpress_ShouldReturnTotals()
            throws Exception {

        // Given: A $100 subtotal, SAVE10, and express delivery
        String requestBody = """
                {
                    "subtotal": 100.00,
                    "promoCode": "SAVE10",
                    "deliveryType": "EXPRESS"
                }
                """;

        // When and Then:
        // $100 - 10% = $90
        // Tax = $7.425
        // Delivery = $5.99
        // Total = $103.415
        mockMvc.perform(post("/api/cart/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subtotal")
                        .value(closeTo(100.00, 0.001)))
                .andExpect(jsonPath("$.discountedSubtotal")
                        .value(closeTo(90.00, 0.001)))
                .andExpect(jsonPath("$.tax")
                        .value(closeTo(7.425, 0.001)))
                .andExpect(jsonPath("$.deliveryFee")
                        .value(closeTo(5.99, 0.001)))
                .andExpect(jsonPath("$.total")
                        .value(closeTo(103.415, 0.001)));
    }

    @Test
    void checkout_WithValidCart_ShouldReturnOrder()
            throws Exception {

        // Given: The order service successfully places the order
        Order order = new Order();
        order.setOrderID(20);
        order.setTotalAmount(97.425);
        order.setDeliveryType("STANDARD_DELIVERY");
        order.setDiscountCode("SAVE10");

        when(orderService.placeOrder(
                1,
                "STANDARD_DELIVERY",
                "SAVE10"
        )).thenReturn(order);

        String requestBody = """
                {
                    "deliveryType": "STANDARD_DELIVERY",
                    "promoCode": "SAVE10"
                }
                """;

        // When and Then: Checkout should return HTTP 200
        mockMvc.perform(post("/api/checkout/{customerId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderID").value(20))
                .andExpect(jsonPath("$.totalAmount")
                        .value(closeTo(97.425, 0.001)))
                .andExpect(jsonPath("$.deliveryType")
                        .value("STANDARD_DELIVERY"));

        verify(orderService).placeOrder(
                1,
                "STANDARD_DELIVERY",
                "SAVE10"
        );
    }

    @Test
    void checkout_WithEmptyCart_ShouldReturnBadRequest()
            throws Exception {

        // Given: The cart is empty
        when(orderService.placeOrder(
                1,
                "STANDARD_DELIVERY",
                null
        )).thenThrow(new IllegalStateException(
                "Cannot place order with an empty cart"
        ));

        String requestBody = """
                {
                    "deliveryType": "STANDARD_DELIVERY"
                }
                """;

        // When and Then: Checkout should return HTTP 400
        mockMvc.perform(post("/api/checkout/{customerId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Cannot place order with an empty cart"));

        verify(orderService).placeOrder(
                1,
                "STANDARD_DELIVERY",
                null
        );
    }

    @Test
    void checkout_WithUnknownCustomer_ShouldReturnNotFound()
            throws Exception {

        // Given: The customer does not exist
        when(orderService.placeOrder(
                99,
                "STANDARD_DELIVERY",
                null
        )).thenThrow(new IllegalArgumentException(
                "Customer not found"
        ));

        // When and Then: Checkout should return HTTP 404
        mockMvc.perform(post("/api/checkout/{customerId}", 99)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Customer not found"));
    }

    @Test
    void getOrderHistory_SortedByAmount_ShouldReturnOrders()
            throws Exception {

        // Given: The customer has two previous orders
        Order firstOrder = new Order();
        firstOrder.setOrderID(1);
        firstOrder.setTotalAmount(100.00);

        Order secondOrder = new Order();
        secondOrder.setOrderID(2);
        secondOrder.setTotalAmount(50.00);

        when(orderService.getOrderHistorySortedByAmount(1))
                .thenReturn(List.of(firstOrder, secondOrder));

        // When and Then: Orders should be returned
        mockMvc.perform(get("/api/orders/history/{customerId}", 1)
                        .param("sortBy", "amount"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].orderID").value(1))
                .andExpect(jsonPath("$[0].totalAmount")
                        .value(closeTo(100.00, 0.001)))
                .andExpect(jsonPath("$[1].orderID").value(2));

        verify(orderService)
                .getOrderHistorySortedByAmount(1);
    }
}
