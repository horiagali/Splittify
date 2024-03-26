package server.service;

import commons.Event;
import commons.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.EventRepository;
import server.database.TagRepository;

import java.util.List;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final TagRepository tagRepository;
    private final TagService tagService;

    /**
     * Constructor for eventService
     * @param eventRepository an eventRepository
     * @param tagRepository
     */
    protected EventService(EventRepository eventRepository, TagRepository tagRepository){
        this.eventRepository = eventRepository;
        this.tagRepository = tagRepository;
        tagService = new TagService(tagRepository, eventRepository);
    }

    /**
     * create an event
     * @param event the new Event
     * @return the new Event
     */
    public ResponseEntity<Event> createEvent(Event event){
        Event saved = eventRepository.save(new Event(
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getDate()));
        tagService.createTag(new Tag("food", "#42f572"), saved.getId());
        tagService.createTag(new Tag("travel", "#f54254"), saved.getId());
        tagService.createTag(new Tag("entrance fees", "#07dafa"), saved.getId());
        tagService.createTag(new Tag("no tag", "#9fa9ab"), saved.getId());
        tagService.createTag(new Tag("gifting money","#e5ff00"), saved.getId());
        return ResponseEntity.ok(saved);
    }

    /**
     * getter for the events
     * @return all the events in the repository
     */
    public ResponseEntity<List<Event>> getEvents(){
        return ResponseEntity.ok(eventRepository.findAll());
    }

    /**
     * a getter by id for an event
     * @param id an Integer
     * @return the found Event
     */
    public ResponseEntity<Event> getEventById(Long id) {
        if (!eventRepository.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Event found = eventRepository.findById(id).get();
//        found.getParticipants();
        return ResponseEntity.ok(found);
    }

    /**
     * delete a event by id
     * @param id the id
     * @return the deleted event
     */
    public ResponseEntity<Event> deleteEvent(Long id){
        if (!eventRepository.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Event toBeRemoved = eventRepository.findById(id).get();
        eventRepository.deleteById(id);
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
        return ResponseEntity.ok(toBeUpdated);
    }

}
