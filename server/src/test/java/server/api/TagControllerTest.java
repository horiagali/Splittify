package server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import commons.Tag;
import server.service.TagService;

public class TagControllerTest {

    private TagService tagService;
    private TagController tagController;

    @BeforeEach
    public void setUp() {
        tagService = mock(TagService.class);
        tagController = new TagController(tagService);
    }

    @Test
    public void testGetAllTags() {
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag("Tag1", Color.RED));
        tags.add(new Tag("Tag2", Color.BLUE));
        when(tagService.getTags()).thenReturn(tags);
        List<Tag> result = tagController.getAllTags();
        assertEquals(2, result.size());
        assertEquals("Tag1", result.get(0).getName());
        assertEquals("Tag2", result.get(1).getName());
    }

    @Test
    public void testGetTagById() {
        Integer id = 1;
        Tag tag = new Tag("Tag1", Color.RED);
        when(tagService.getTagById(id)).thenReturn(tag);
        Tag result = tagController.getById(id);
        assertEquals(tag, result);
    }

    @Test
    public void testCreateTag() {
        Tag tag = new Tag("NewTag", Color.GREEN);
        when(tagService.createTag(tag)).thenReturn(tag);
        Tag result = tagController.createTag(tag);
        assertEquals(tag, result);
    }

    @Test
    public void testDeleteTag() {
        Integer id = 1;
        Tag tag = new Tag("Tag1", Color.RED);
        when(tagService.deleteTag(id)).thenReturn(tag);
        Tag result = tagController.deleteTag(id);
        assertEquals(tag, result);
    }

    @Test
    public void testUpdateTag() {
        Integer id = 1;
        Tag tag = new Tag("UpdatedTag", Color.BLUE);
        when(tagService.updateTag(any(Tag.class), eq(id))).thenReturn(tag);
        Tag result = tagController.updateTag(tag, id);
        assertEquals(tag, result);
    }
}
