package service;

import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import server.database.EventRepository;
import server.database.TagRepository;
import server.service.TagService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class TagServiceTest {
    private TagRepository tagRepository;
    private EventRepository eventRepository;
    private TagService tagService;
    private Event event;
    private List<Expense> expenses;
    private List<Participant> participants;
    private List<Tag> tags;

    @BeforeEach
    void setUp() {
        tagRepository = mock(TagRepository.class);
        eventRepository = mock(EventRepository.class);
        tagService = new TagService(tagRepository, eventRepository);

        // Initialize lists
        expenses = new ArrayList<>();
        participants = new ArrayList<>();
        tags = new ArrayList<>();

        // Create an event
        event = new Event("Birthday Party", new Date(), "Celebrating John's birthday", "Party Hall", expenses, participants, tags);
    }

    @Test
    void testCreateTag_Success() {
        // Mock data
        Tag tag = new Tag("Test Tag", "red");
        long eventId = 1L;

        // Mock repositories and service
        TagRepository tagRepository = mock(TagRepository.class);
        EventRepository eventRepository = mock(EventRepository.class);
        TagService tagService = new TagService(tagRepository, eventRepository);

        // Mock behavior of eventRepository.findById()
        when(eventRepository.findById(eventId)).thenReturn(java.util.Optional.of(new Event()));

        // Mock behavior of tagRepository.save()
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        // Test the createTag method
        ResponseEntity<Tag> responseEntity = tagService.createTag(tag, eventId);

        // Verify that the response is OK (200) and the returned tag matches the input tag
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(tag, responseEntity.getBody());
    }

    @Test
    void testGetTags_Success() {
        long eventId = 1L;
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag("Tag 1", "blue"));
        tags.add(new Tag("Tag 2", "green"));

        TagRepository tagRepository = mock(TagRepository.class);
        EventRepository eventRepository = mock(EventRepository.class);
        TagService tagService = new TagService(tagRepository, eventRepository);

        when(tagRepository.findTagsByEvent_Id(eventId)).thenReturn(tags);
        ResponseEntity<List<Tag>> responseEntity = tagService.getTags(eventId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(tags, responseEntity.getBody());
    }

    @Test
    void testCreateTag_NullTagName() {
        Tag tag = new Tag(null, "Red");
        ResponseEntity<Tag> responseEntity = tagService.createTag(tag, 1L);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void testCreateTag_BlankTagName() {
        Tag tag = new Tag("", "Red");
        ResponseEntity<Tag> responseEntity = tagService.createTag(tag, 1L);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void testCreateTag_EventNotFound() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());
        Tag tag = new Tag("Test Tag", "Red");
        ResponseEntity<Tag> responseEntity = tagService.createTag(tag, 1L);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void testGetTagById_Successful() {
        Event event = new Event();
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));

        Tag tag = new Tag("Test Tag", "Red");
        tag.setEvent(event);
        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(tag));

        ResponseEntity<Tag> responseEntity = tagService.getTagById(1L, 1L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(tag, responseEntity.getBody());
    }

    @Test
    void testGetTagById_EventNotFound() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Tag> responseEntity = tagService.getTagById(1L, 1L);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void testGetTagById_TagNotFound() {
        Event event = new Event();
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));

        when(tagRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Tag> responseEntity = tagService.getTagById(1L, 1L);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void testGetTagById_TagNotBelongsToEvent() {
        Event event = new Event();
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        Tag tag = new Tag("Test Tag", "Red");
        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(tag));
        ResponseEntity<Tag> responseEntity = tagService.getTagById(1L, 2L);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void testDeleteTag_Successful() {
        Event event = new Event();
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));

        Tag tag = new Tag("Test Tag", "Red");
        tag.setEvent(event);
        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(tag));

        ResponseEntity<Tag> responseEntity = tagService.deleteTag(1L, 1L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(tag, responseEntity.getBody());
    }

    @Test
    void testDeleteTag_TagNotFound() {
        Event event = new Event();
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));

        when(tagRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Tag> responseEntity = tagService.deleteTag(1L, 1L);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void testDeleteTag_EventNotFound() {
        Long eventId = 1L;
        Long tagId = 1L;

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        ResponseEntity<Tag> responseEntity = tagService.deleteTag(eventId, tagId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        verify(tagRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDeleteTag_TagNotBelongingToEvent() {
        Long eventId = 1L;
        Long tagId = 1L;

        Event event = new Event();
        Tag tag = new Tag();
        Event differentEvent = new Event();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        ResponseEntity<Tag> responseEntity = tagService.deleteTag(eventId, tagId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        verify(tagRepository, never()).deleteById(anyLong());
    }

    @Test
    void testUpdateTag_Successful() {
        Long eventId = 1L;
        Long tagId = 1L;
        Tag newTag = new Tag("New Tag Name", "hex");

        Event event = new Event();
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        Tag existingTag = new Tag("Old Tag Name", "old_color");
        existingTag.setEvent(event);
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(existingTag));

        when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<Tag> response = tagService.updateTag(eventId, newTag, tagId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("New Tag Name", response.getBody().getName());
        assertEquals("hex", response.getBody().getColor());
    }


    @Test
    void testUpdateTag_NullOrBlankName() {
        Long eventId = 1L;
        Long tagId = 1L;

        Event event = new Event();
        Tag existingTag = new Tag("Existing Tag", "Blue");
        Tag updatedTag = new Tag(null, "Red");

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(existingTag));

        ResponseEntity<Tag> responseEntity = tagService.updateTag(eventId, updatedTag, tagId);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void testUpdateTag_EventNotFound() {
        Long eventId = 1L;
        Long tagId = 1L;

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        ResponseEntity<Tag> responseEntity = tagService.updateTag(eventId, new Tag("Updated Tag", "Red"), tagId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void testUpdateTag_TagNotFound() {
        Long eventId = 1L;
        Long tagId = 1L;

        Event event = new Event();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        ResponseEntity<Tag> responseEntity = tagService.updateTag(eventId, new Tag("Updated Tag", "Red"), tagId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void testUpdateTag_TagNotBelongingToEvent() {
        Long eventId = 1L;
        Long tagId = 1L;

        Event event = new Event();
        Tag existingTag = new Tag("Existing Tag", "Blue");
        Event differentEvent = new Event();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(existingTag));

        ResponseEntity<Tag> responseEntity = tagService.updateTag(eventId, new Tag("Updated Tag", "Red"), tagId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

}
