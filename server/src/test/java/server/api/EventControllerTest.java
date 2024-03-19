package server.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import commons.Event;
import server.service.EventService;

class EventControllerTest {

    private EventService eventService;
    private EventController eventController;

    @BeforeEach
    void setUp() {
        // Create a mock EventService
        eventService = mock(EventService.class);
        eventController = new EventController(eventService);
    }

    @Test
    void testGetAllEvents() {
        List<Event> mockEvents = new ArrayList<>();
        mockEvents.add(new Event("Event 1", "Description 1", "Location 1", new Date()));
        mockEvents.add(new Event("Event 2", "Description 2", "Location 2", new Date()));
        when(eventService.getEvents()).thenReturn(new ResponseEntity<>(mockEvents, HttpStatus.OK));

        ResponseEntity<List<Event>> responseEntity = eventController.getAllEvents();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockEvents, responseEntity.getBody());
    }

    @Test
    void testGetEventById() {
        Event mockEvent = new Event("Event 1", "Description 1", "Location 1", new Date());
        when(eventService.getEventById(anyLong())).thenReturn(new ResponseEntity<>(mockEvent, HttpStatus.OK));

        ResponseEntity<Event> responseEntity = eventController.getById(1L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockEvent, responseEntity.getBody());
    }

    @Test
    void testCreateEvent() {
        Event eventToCreate = new Event("Event 1", "Description 1", "Location 1", new Date());
        Event createdEvent = new Event("Event 1", "Description 1", "Location 1", new Date());
        when(eventService.createEvent(any(Event.class))).thenReturn(new ResponseEntity<>(createdEvent, HttpStatus.CREATED));

        ResponseEntity<Event> responseEntity = eventController.createEvent(eventToCreate);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(createdEvent, responseEntity.getBody());
    }

    @Test
    void testDeleteEvent() {
        Event deletedEvent = new Event("Event 1", "Description 1", "Location 1", new Date());
        when(eventService.deleteEvent(anyLong())).thenReturn(new ResponseEntity<>(deletedEvent, HttpStatus.OK));

        ResponseEntity<Event> responseEntity = eventController.deleteEvent(1L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(deletedEvent, responseEntity.getBody());
    }

    @Test
    void testUpdateEvent() {
        Event updatedEvent = new Event("Updated Event", "Updated Description", "Updated Location", new Date());
        when(eventService.updateEvent(any(Event.class), anyLong())).thenReturn(new ResponseEntity<>(updatedEvent, HttpStatus.OK));

        ResponseEntity<Event> responseEntity = eventController.updateEvent(updatedEvent, 1L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(updatedEvent, responseEntity.getBody());
    }
}
