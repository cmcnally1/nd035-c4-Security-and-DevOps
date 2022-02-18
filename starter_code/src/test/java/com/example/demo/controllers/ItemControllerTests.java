package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTests {

    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    /**
     * Happy path test for the get items method in controller
     * @throws Exception
     */
    @Test
    public void getItems_happyPathTest() throws Exception {
        // Set up items to use to for stubbing
        Item item1 = new Item();
        item1.setId(0L);
        item1.setName("item1Name");
        item1.setDescription("This is item 1");
        item1.setPrice(BigDecimal.valueOf(10.00));

        Item item2 = new Item();
        item2.setId(1L);
        item2.setName("item2Name");
        item2.setDescription("This is item 2");
        item2.setPrice(BigDecimal.valueOf(20.00));

        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        // Stub the find all repository method
        when(itemRepository.findAll()).thenReturn(items);

        // Call the get items method. Response should contain list of items
        final ResponseEntity<List<Item>> responseEntity = itemController.getItems();

        // Assert that the response is not null and is ok
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        // Extract the list of items from the response
        List<Item> returnedItems = responseEntity.getBody();

        // Assert that the returned items are the same as the expected items
        assertTrue(item1.equals(returnedItems.get(0)));
        assertTrue(item2.equals(returnedItems.get(1)));
    }
}
