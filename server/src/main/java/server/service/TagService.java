package server.service;

import commons.Event;
import commons.Tag;
import org.springframework.stereotype.Service;
import server.database.EventRepository;
import server.database.TagRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TagService {
    private TagRepository tagRepository;
    private EventRepository eventRepository;

    /**
     * Constructor for TagService
     * @param tagRepository a TagRepository
     * @param eventRepository a eventRepository
     */
    protected TagService(TagRepository tagRepository, EventRepository eventRepository){
        this.tagRepository = tagRepository;
        this.eventRepository = eventRepository;
    }

    /**
     * Create a tag
     * @param tag The new Tag
     * @param eventId the eventId
     * @return The new Tag
     */
    public Tag createTag(Tag tag, Long eventId){
        Optional<Event> event = eventRepository.findById(eventId);
        if(event.isEmpty()) throw new IllegalArgumentException("Event with given ID not found");
        Tag tagEntity = new Tag(tag.getName(), tag.getColor());
        tagEntity.setEvent(event.get());
        tagRepository.save(tagEntity);
        return tagEntity;
    }

    /**
     * Get all tags
     * @param eventId an eventId
     * @return All tags in the repository
     */
    public List<Tag> getTags(Long eventId){
        return tagRepository.findTagsByEvent_Id(eventId);
    }

    /**
     * Get a tag by ID
     * @param id The ID of the tag
     * @param eventId the eventId
     * @return The found Tag, or null if not found
     */
    public Tag getTagById(Long id, Long eventId){
        Optional<Event> event = eventRepository.findById(eventId);
        if(event.isEmpty()) throw new IllegalArgumentException("Event with given ID not found");
        Optional<Tag> tag = tagRepository.findById(id);
        if(tag.isEmpty())
            throw new IllegalArgumentException("Expense with given ID not found");
        if(tag.get().getEvent() != event.get()) {
            throw new IllegalArgumentException("Expense doesn't belong to event");
        }
        return tag.get();
    }

    /**
     * Delete a tag by ID
     * @param id The ID of the tag to delete
     * @param eventId the eventId
     * @return The deleted tag, or null if not found
     */
    public Tag deleteTag(Long eventId, Long id){
        Optional<Event> event = eventRepository.findById(eventId);
        if(event.isEmpty()) throw new IllegalArgumentException("Event with given ID not found");
        Optional<Tag> tag = tagRepository.findById(id);
        if(tag.isEmpty())
            throw new IllegalArgumentException("Expense with given ID not found");
        if(tag.get().getEvent() != event.get()) {
            throw new IllegalArgumentException("Expense doesn't belong to event");
        }
        tagRepository.delete(tag.get());
        return tag.get();
    }

    /**
     * Update an existing tag
     * @param eventId eventId
     * @param newTag The updated tag
     * @param id the id
     * @return The updated tag
     */
    public Tag updateTag(Long eventId, Tag newTag, Long id){
        Optional<Event> event = eventRepository.findById(eventId);
        if(event.isEmpty()) throw new IllegalArgumentException("Event with given ID not found");
        Optional<Tag> tag = tagRepository.findById(id);
        if(tag.isEmpty())
            throw new IllegalArgumentException("Expense with given ID not found");
        if(tag.get().getEvent() != event.get()) {
            throw new IllegalArgumentException("Expense doesn't belong to event");
        }

        tag.get().setName(newTag.getName());
        tag.get().setColor(newTag.getColor());
        tag.get().setEvent(newTag.getEvent());
        Tag saved = tagRepository.save(tag.get());
        return saved;
    }
}