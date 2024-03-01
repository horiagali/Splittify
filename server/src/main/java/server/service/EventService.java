package server.service;

import commons.Event;
import org.springframework.stereotype.Service;
import server.database.EventRepository;

import java.util.List;

@Service
public class EventService {
    private EventRepository eventRepository;

    /**
     * Constructor for eventService
     * @param eventRepository an eventRepository
     */
    protected EventService(EventRepository eventRepository){
        this.eventRepository = eventRepository;
    }

    /**
     * create a event
     * @param event the new Event
     * @return the new Event
     */
    public Event createEvent(Event event){
        Event eventEntity = new Event(event.getTitle(), event.getDescription(), event.getLocation(), event.getDate());
        eventRepository.save(eventEntity);
        return eventEntity;
    }

    /**
     * getter for the events
     * @return all the events in the repository
     */
    public List<Event> getEvents(){
        return eventRepository.findAll();
    }

    /**
     * a getter by id for an event
     * @param id an Integer
     * @return the found Event
     */
    public Event getEventById(Integer id){
        return eventRepository.findById(id).orElse(null);
    }

    /**
     * delete a event by id
     * @param id the id
     * @return the deleted event
     */
    public Event deleteEvent(Integer id){
        Event toDelete = getEventById(id);
        if (getEventById(id) != null)
            eventRepository.delete(toDelete);
        return toDelete;
    }

    /**
     * update an existing event
     * @param event
     * @param id an integer
     * @return the new event
     */
    public Event updateEvent(Event event, Integer id){
        if (getEventById(id) == null)
            return null;
        Event myEvent = new Event(event.getTitle(), event.getDate(), event.getDescription(),
                event.getLocation(), event.getExpenses(), event.getParticipants(), event.getTags());
        eventRepository.save(myEvent);
        return myEvent;
    }

}
