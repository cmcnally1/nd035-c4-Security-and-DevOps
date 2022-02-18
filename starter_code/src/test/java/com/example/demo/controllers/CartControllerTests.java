package com.example.demo.controllers;


import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTests {

    private CartController cartController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    /**
     * Happy path test for add to cart method in controller
     * @throws Exception
     */
    @Test
    public void addToCart_happyPathTest() throws Exception {

        // Set up cart
        Cart cart = new Cart();

        // Set up user
        User user = new User();
        user.setUsername("testUsername");
        user.setId(0L);
        user.setCart(cart);

        // Set up item
        Item item = new Item();
        item.setId(0L);
        item.setName("testItem");
        item.setDescription("This is a test item");
        item.setPrice(BigDecimal.valueOf(10.00));

        // Set up cart request
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setUsername(user.getUsername());
        cartRequest.setItemId(item.getId());
        cartRequest.setQuantity(1);

        // Stubs
        when(userRepository.findByUsername(cartRequest.getUsername())).thenReturn(user);
        when(itemRepository.findById(cartRequest.getItemId())).thenReturn(Optional.of(item));

        // Call the add to cart method. Response should contain a new cart
        final ResponseEntity<Cart> response = cartController.addTocart(cartRequest);

        // Assert that the response is not null and the response is good
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        // Extract the cart from the response
        Cart returnedCart = response.getBody();

        // Assert that the returned cart contains the correct information
        assertNotNull(returnedCart);
        assertEquals(item.getName(), returnedCart.getItems().get(0).getName());
        assertEquals(item.getPrice(), returnedCart.getTotal());
    }

    /**
     * Test that the correct response is returned if the user cannot be found when adding to cart
     * @throws Exception
     */
    @Test
    public void addToCart_nullUserTest() throws Exception {

        // Set up null user
        User user = null;

        // Set up item
        Item item = new Item();
        item.setId(0L);
        item.setName("testItem");
        item.setDescription("This is a test item");
        item.setPrice(BigDecimal.valueOf(10.00));

        // Set up cart request
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setItemId(item.getId());
        cartRequest.setQuantity(1);

        // Stub
        when(userRepository.findByUsername(cartRequest.getUsername())).thenReturn(user);

        // Call the add to cart method.
        final ResponseEntity<Cart> response = cartController.addTocart(cartRequest);

        // Assert that the response is not null and the response is NOT FOUND
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    /**
     * Test that the correct response is returned if the item cannot be found when adding to cart
     * @throws Exception
     */
    @Test
    public void addToCart_nullItemTest() throws Exception {

        // Set up cart
        Cart cart = new Cart();

        // Set up user
        User user = new User();
        user.setUsername("testUsername");
        user.setId(0L);
        user.setCart(cart);

        // Set up null item
        Item item = null;

        // Set up cart request
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setUsername(user.getUsername());
        cartRequest.setQuantity(1);

        // Stubs
        when(userRepository.findByUsername(cartRequest.getUsername())).thenReturn(user);
        when(itemRepository.findById(cartRequest.getItemId())).thenReturn(Optional.ofNullable(item));

        // Call the add to cart method.
        final ResponseEntity<Cart> response = cartController.addTocart(cartRequest);

        // Assert that the response is not null and the response is NOT FOUND
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
