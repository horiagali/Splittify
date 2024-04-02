package service;

import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import server.database.EventRepository;
import server.database.TagRepository;
import server.service.EventService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class EventServiceTest {

    private EventRepository eventRepository;
    private TagRepository tagRepository;
    private EventService eventService;
    private Event event;
    private List<Expense> expenses;
    private List<Participant> participants;
    private List<Tag> tags;

    @BeforeEach
    void setUp() {
        eventRepository = mock(EventRepository.class);
        tagRepository = mock(TagRepository.class);
        eventService = new EventService(eventRepository, tagRepository);

        expenses = new ArrayList<>();
        participants = new ArrayList<>();
        tags = new ArrayList<>();
        event = new Event("Birthday Party", new Date(), "Celebrating John's birthday", "Party Hall", expenses, participants, tags);
    }

    @Test
    void testCreateEvent() {
        // Prepare test data
        Event event = new Event("Test Event", "Description", "Location", null);
        event.setId(1L); // Set a non-null ID

        // Mock behavior
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        // Perform the test
        ResponseEntity<Event> response = eventService.createEvent(event);

        // Verify the result
        assertEquals(event, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testCreateEventWithNullTitle() {
        Event event = new Event(null, "Description", "Location", new Date());
        event.setId(1L);

        ResponseEntity<Event> response = eventService.createEvent(event);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testGetEvents() {
        List<Event> events = new ArrayList<>();
        events.add(event);
        events.add(event);

        when(eventRepository.findAll()).thenReturn(events);

        ResponseEntity<List<Event>> response = eventService.getEvents();
        assertEquals(events, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetEventById() {
        Event event1 = new Event("Test Event", "Description", "Location", null);
        event1.setId(1L);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event1));
        ResponseEntity<Event> response = eventService.getEventById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(event1, response.getBody());
    }

    @Test
    void testGetEventById_NotFound() {
        Long id = 1L;

        when(eventRepository.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<Event> response = eventService.getEventById(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testDeleteEvent() {
        Long id = 1L;
        Event eventToDelete = new Event();
        eventToDelete.setId(id);

        when(eventRepository.findById(id)).thenReturn(Optional.of(eventToDelete));

        ResponseEntity<Event> response = eventService.deleteEvent(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(eventToDelete, response.getBody());

        verify(eventRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteEvent_NotFound() {
        Long id = 1L;
        when(eventRepository.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<Event> response = eventService.deleteEvent(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testUpdateEvent() {
        Long id = 1L;
        Event existingEvent = new Event();
        existingEvent.setId(id);

        Event updatedEvent = new Event("Updated Title", "Updated Description", "Updated Location", null);
        updatedEvent.setId(id);

        when(eventRepository.findById(id)).thenReturn(Optional.of(existingEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(updatedEvent);

        ResponseEntity<Event> response = eventService.updateEvent(updatedEvent, id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedEvent, response.getBody());
    }

    @Test
    void testUpdateEvent_NotFound() {
        Long id = 1L;
        Event updatedEvent = new Event("Updated Title", "Updated Description", "Updated Location", null);
        updatedEvent.setId(id);

        when(eventRepository.findById(id)).thenReturn(Optional.empty());
        ResponseEntity<Event> response = eventService.updateEvent(updatedEvent, id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testUpdateEvent_InvalidTitle() {
        Long id = 1L;
        Event existingEvent = new Event();
        existingEvent.setId(id);

        Event updatedEvent = new Event();

        when(eventRepository.findById(id)).thenReturn(Optional.of(existingEvent));
        ResponseEntity<Event> response = eventService.updateEvent(updatedEvent, id);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }
}
