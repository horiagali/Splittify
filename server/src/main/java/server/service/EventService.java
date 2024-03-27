package server.service;

import commons.Event;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.EventRepository;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class EventService {
    private final EventRepository eventRepository;

    /**
     * Constructor for eventService
     * @param eventRepository an eventRepository
     */
    protected EventService(EventRepository eventRepository){
        this.eventRepository = eventRepository;
    }

    /**
     * create an event
     * @param event the new Event
     * @return the new Event
     */
    public ResponseEntity<Event> createEvent(Event event){
        Event eventEntity = new Event(
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getDate());
        Event saved = eventRepository.save(eventEntity);
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                .log(Level.INFO, "Event created: "+saved);
        return ResponseEntity.ok(saved);
    }

    /**
     * getter for the events
     * @return all the events in the repository
     */
    public ResponseEntity<List<Event>> getEvents(){
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, "Events requested");
        return ResponseEntity.ok(eventRepository.findAll());
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

    /**
     * delete a event by id
     * @param id the id
     * @return the deleted event
     */
    public ResponseEntity<Event> deleteEvent(Long id){
        if (!eventRepository.findById(id).isPresent()) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                    .log(Level.WARNING, "404: Event not found via 'deleteEvent'");
            return ResponseEntity.notFound().build();
        }
        Event toBeRemoved = eventRepository.findById(id).get();
        eventRepository.deleteById(id);
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                .log(Level.INFO, "Event deleted: "+toBeRemoved);
        return ResponseEntity.ok(toBeRemoved);
    }

    /**
     * update an existing event
     * @param event the event to be updated
     * @param id an integer
     * @return the new event
     */
    public ResponseEntity<Event> updateEvent(Event event, Long id){
        if (!eventRepository.findById(id).isPresent()) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                    .log(Level.WARNING, "404: Event not found via 'updateEvent'");
            return ResponseEntity.notFound().build();
        }
        Event toBeUpdated = eventRepository.findById(id).get();
        toBeUpdated.setTitle(event.getTitle());
        toBeUpdated.setDescription(event.getDescription());
        toBeUpdated.setLocation(event.getLocation());
        toBeUpdated.setDate(event.getDate());
        toBeUpdated.setParticipants(event.getParticipants());
        toBeUpdated.setTags(event.getTags());
        toBeUpdated.setExpenses(event.getExpenses());
        eventRepository.save(toBeUpdated);
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                .log(Level.INFO, "Event updated: "+toBeUpdated);
        return ResponseEntity.ok(toBeUpdated);
    }

}
