package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import net.bytebuddy.dynamic.DynamicType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setup() {
        userController = new UserController();
        TestUtils.injectObjects(userController,"userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    /**
     * Happy path test for creating a user
     * @throws Exception
     */
    @Test
    public void createUser_happyPathTest() throws Exception {
        // Example of stubbing
        // When the specified section of code is hit in the user controller (encoder.encode("testPassword"))
        // Then do something else, in this case return "thisIsHashed" as the result of this method
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("testPassword");
        request.setConfirmPassword("testPassword");

        final ResponseEntity<User> response = userController.createUser(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();

        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());

    }

    /**
     * Test that the correct response is received if submitted password is
     * too short.
     * @throws Exception
     */
    @Test
    public void createUser_shortPasswordTest() throws Exception {

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("shortP");
        request.setConfirmPassword("shortP");

        final ResponseEntity<User> response = userController.createUser(request);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());

    }

    /**
     * Happy path test for finding a user by ID
     * @throws Exception
     */
    @Test
    public void findById_happyPathTest() throws Exception {
        User user = new User();
        user.setId(0);
        user.setUsername("test");
        user.setPassword("testPassword");

        when(userRepository.findById(0L)).thenReturn(Optional.of(user));

        final ResponseEntity<User> response = userController.findById(0L);

        assertEquals(200, response.getStatusCodeValue());

        User foundUser = response.getBody();

        assertNotNull(foundUser);
        assertEquals(0, foundUser.getId());
        assertEquals("test", foundUser.getUsername());
        assertEquals("testPassword", foundUser.getPassword());

    }

    /**
     * Happy path test for finding a user by Username
     * @throws Exception
     */
    @Test
    public void findByUserName_happyPathTest() throws Exception {
        User user = new User();
        user.setId(0);
        user.setUsername("test");
        user.setPassword("testPassword");

        when(userRepository.findByUsername("test")).thenReturn(user);

        final ResponseEntity<User> response = userController.findByUserName("test");

        assertEquals(200, response.getStatusCodeValue());

        User foundUser = response.getBody();

        assertNotNull(foundUser);
        assertEquals(0, foundUser.getId());
        assertEquals("test", foundUser.getUsername());
        assertEquals("testPassword", foundUser.getPassword());

    }

    /**
     * Test that the correct response is returned when a user is not found
     * @throws Exception
     */
    @Test
    public void findByUserName_userNotFoundTest() throws Exception {

        final ResponseEntity<User> response = userController.findByUserName("notAUser");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

    }
}
