package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private OrderRepository orderRepository = mock(OrderRepository.class);

    private UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void setup() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
    }

    /**
     * Happy path test for submitting a user's order
     * @throws Exception
     */
    @Test
    public void submitOrder_happyPathTest() throws Exception {
        // Set up test username
        String username = "testUsername";
        // Set up a test user and cart and item
        User user = new User();
        Cart cart = new Cart();
        Item item = new Item();

        // Set up an item for the cart
        item.setId(0l);
        item.setName("testItem");
        item.setPrice(BigDecimal.valueOf(10.00));
        item.setDescription("This is a test item");

        // Set the items for the cart
        cart.setId(0L);
        cart.addItem(item);

        // Set the username and cart for the test user
        user.setId(0L);
        user.setUsername(username);
        user.setCart(cart);

        // Stub the usage of the findByUsername method in the controller
        when(userRepository.findByUsername(username)).thenReturn(user);

        // Submit via the controller. Response is expected to hold a user order entity
        final ResponseEntity<UserOrder> response = orderController.submit(username);

        // Assert that the response is not null and the response code it 200.
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        // Get the user order from the response
        UserOrder order = response.getBody();

        // Asset the order is not empty
        assertNotNull(order);
        // Assert that the order contains the item and the total is correct
        assertEquals("testItem", order.getItems().get(0).getName());
        assertEquals(BigDecimal.valueOf(10.00), order.getTotal());

    }

    /**
     * Test that the correct response is returned if submitting an order for a null user
     * @throws Exception
     */
    @Test
    public void submitOrder_nullUser() throws Exception {
        // Set up test username
        String username = "testUsername";
        // Set up a test user set to null
        User user = null;
        // Stub the usage of the findByUsername method in the controller
        when(userRepository.findByUsername(username)).thenReturn(user);

        // Submit via the controller. Response is expected to hold a user order entity
        final ResponseEntity<UserOrder> response = orderController.submit(username);

        // Assert that the response is not null and the response code is 404 (NOT FOUND).
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    /**
     * Happy path test for getting list of orders by user
     * @throws Exception
     */
    @Test
    public void getOrders_happyPathTest() throws Exception {
        // Set up test username
        String username = "testUsername";
        // Set up a test user and order and item
        User user = new User();
        UserOrder order = new UserOrder();
        Item item = new Item();


        // Set up an item for the order
        item.setId(0l);
        item.setName("testItem");
        item.setPrice(BigDecimal.valueOf(10.00));
        item.setDescription("This is a test item");

        // Create list of items for order
        List<Item> items = new ArrayList<>();
        items.add(item);

        // Set the username and cart for the test user
        user.setId(0L);
        user.setUsername(username);

        // Set up the user order
        order.setItems(items);
        order.setUser(user);
        order.setId(0L);
        order.setTotal(BigDecimal.valueOf(10.00));

        // Create a list of orders
        List<UserOrder> userOrders = new ArrayList<>();
        userOrders.add(order);

        // Stub the usage of the findByUsername method in the controller
        when(userRepository.findByUsername(username)).thenReturn(user);
        // Stub the usage of the findByUser method for orders in the controller
        when(orderRepository.findByUser(user)).thenReturn(userOrders);

        // Get orders via the controller. The response is expected to hold a list of orders
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(username);

        // Assert that the response is not null and the response is good
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        // Get the list of orders from the response
        List<UserOrder> returnedOrders = response.getBody();

        // Assert the the list of orders contains the correct information
        assertNotNull(returnedOrders);
        assertEquals(item.getName(), returnedOrders.get(0).getItems().get(0).getName());
        assertEquals(order.getTotal(), returnedOrders.get(0).getTotal());
        assertEquals(user.getUsername(), returnedOrders.get(0).getUser().getUsername());


    }

    /**
     * Test that the correct response is returned if getting orders for a null user
     * @throws Exception
     */
    @Test
    public void getOrders_nullUser() throws Exception {
        // Set up test username
        String username = "testUsername";
        // Set up a test user set to null
        User user = null;
        // Stub the usage of the findByUsername method in the controller
        when(userRepository.findByUsername(username)).thenReturn(user);

        // Get orders via the controller. Response is expected to hold a list of user orders
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(username);

        // Assert that the response is not null and the response code is 404 (NOT FOUND).
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
