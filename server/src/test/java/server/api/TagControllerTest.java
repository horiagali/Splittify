package server.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import server.service.TagService;
import commons.Tag;

class TagControllerTest {

    private TagService tagService;
    private TagController tagController;

    @BeforeEach
    void setUp() {
        // Create a mock TagService
        tagService = mock(TagService.class);
        tagController = new TagController(tagService);
    }

    @Test
    void testGetAllTags() {
        // Mock the behavior of tagService.getTags method
        List<Tag> mockTags = new ArrayList<>();
        mockTags.add(new Tag("Food", new Color(255, 0, 0)));
        mockTags.add(new Tag("Travel", new Color(0,255,0)));
        when(tagService.getTags(anyLong())).thenReturn(new ResponseEntity<>(mockTags, HttpStatus.OK));

        // Call the controller method
        ResponseEntity<List<Tag>> responseEntity = tagController.getAllTags(1L);

        // Assertions
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockTags, responseEntity.getBody());
    }

    @Test
    void testGetTagById() {
        // Mock the behavior of tagService.getTagById method
        Tag mockTag = new Tag("Food", new Color(255, 0, 0));
        when(tagService.getTagById(anyLong(), anyLong())).thenReturn(new ResponseEntity<>(mockTag, HttpStatus.OK));

        // Call the controller method
        ResponseEntity<Tag> responseEntity = tagController.getById(1L, 1L);

        // Assertions
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockTag, responseEntity.getBody());
    }

    @Test
    void testCreateTag() {
        // Mock the behavior of tagService.createTag method
        Tag tagToCreate = new Tag("Food", new Color(255, 0, 0));
        Tag createdTag = new Tag("Travel", new Color(0, 255, 0));
        when(tagService.createTag(any(Tag.class), anyLong())).thenReturn(new ResponseEntity<>(createdTag, HttpStatus.CREATED));

        // Call the controller method
        ResponseEntity<Tag> responseEntity = tagController.createTag(tagToCreate, 1L);

        // Assertions
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(createdTag, responseEntity.getBody());
    }

    @Test
    void testDeleteTag() {
        // Mock the behavior of tagService.deleteTag method
        Tag deletedTag = new Tag("Food", new Color(255, 0, 0));
        when(tagService.deleteTag(anyLong(), anyLong())).thenReturn(new ResponseEntity<>(deletedTag, HttpStatus.OK));

        // Call the controller method
        ResponseEntity<Tag> responseEntity = tagController.deleteTag(1L, 1L);

        // Assertions
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(deletedTag, responseEntity.getBody());
    }

    @Test
    void testUpdateTag() {
        // Mock the behavior of tagService.updateTag method
        Tag updatedTag = new Tag("Food", new Color(255, 0, 0));
        when(tagService.updateTag(anyLong(), any(Tag.class), anyLong())).thenReturn(new ResponseEntity<>(updatedTag, HttpStatus.OK));

        // Call the controller method
        ResponseEntity<Tag> responseEntity = tagController.updateTag(1L, new Tag("Travel", new Color(255, 0, 0)), 1L);

        // Assertions
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(updatedTag, responseEntity.getBody());
    }
}