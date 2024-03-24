package server.api;

import commons.Color;
import commons.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import server.service.TagService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

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
        List<Tag> mockTags = new ArrayList<>();
        mockTags.add(new Tag("Food", "HEXcolor"));
        mockTags.add(new Tag("Travel", "HEXcolor"));
        when(tagService.getTags(anyLong())).thenReturn(new ResponseEntity<>(mockTags, HttpStatus.OK));

        ResponseEntity<List<Tag>> responseEntity = tagController.getAllTags(1L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockTags, responseEntity.getBody());
    }

    @Test
    void testGetTagById() {
        Tag mockTag = new Tag("Food", "HEXcolor");
        when(tagService.getTagById(anyLong(), anyLong())).thenReturn(new ResponseEntity<>(mockTag, HttpStatus.OK));

        ResponseEntity<Tag> responseEntity = tagController.getById(1L, 1L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockTag, responseEntity.getBody());
    }

    @Test
    void testCreateTag() {
        Tag tagToCreate = new Tag("Food", "HEXcolor");
        Tag createdTag = new Tag("Travel", "HEXcolor");
        when(tagService.createTag(any(Tag.class), anyLong())).thenReturn(new ResponseEntity<>(createdTag, HttpStatus.CREATED));

        ResponseEntity<Tag> responseEntity = tagController.createTag(tagToCreate, 1L);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(createdTag, responseEntity.getBody());
    }

    @Test
    void testDeleteTag() {
        Tag deletedTag = new Tag("Food", "HEXcolor");
        when(tagService.deleteTag(anyLong(), anyLong())).thenReturn(new ResponseEntity<>(deletedTag, HttpStatus.OK));

        ResponseEntity<Tag> responseEntity = tagController.deleteTag(1L, 1L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(deletedTag, responseEntity.getBody());
    }

    @Test
    void testUpdateTag() {
        Tag updatedTag = new Tag("Food", "HEXcolor");
        when(tagService.updateTag(anyLong(), any(Tag.class), anyLong())).thenReturn(new ResponseEntity<>(updatedTag, HttpStatus.OK));

        ResponseEntity<Tag> responseEntity = tagController.updateTag(1L, new Tag("Travel", "HEXcolor"), 1L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(updatedTag, responseEntity.getBody());
    }
}