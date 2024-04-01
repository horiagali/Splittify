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
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        assertEquals(200, response.getStatusCodeValue());
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

    /**
     * a getter by id for an event
     * @param id an Integer
     * @return the found Event
     */
    public ResponseEntity<Event> getEventById(Long id) {
        if (!eventRepository.findById(id).isPresent()) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                    .log(Level.WARNING, "404: Event not found via 'getEventById'");
            return ResponseEntity.notFound().build();
        }
        Event found = eventRepository.findById(id).get();
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                .log(Level.INFO, "Event requested: "+found);
        return ResponseEntity.ok(found);
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
}
