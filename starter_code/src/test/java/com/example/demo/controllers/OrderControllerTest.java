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

    @Test
    public void submitOrder_happyPathTest() throws Exception {
        // Set up test username
        String username = "testUsername";
        // Set up a test user and car
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
}
