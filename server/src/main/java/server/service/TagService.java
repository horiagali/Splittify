package server.service;

import commons.Event;
import commons.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.EventRepository;
import server.database.TagRepository;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class TagService {
    private final TagRepository tagRepository;
    private final EventRepository eventRepository;

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
    public ResponseEntity<Tag> createTag(Tag tag, Long eventId){
        if (!eventRepository.findById(eventId).isPresent()) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                    .log(Level.WARNING, "404: Event not found via 'createTag'");
            return ResponseEntity.notFound().build();
        }
        Event event = eventRepository.findById(eventId).get();
        Tag tagEntity = new Tag(tag.getName(), tag.getColor());
        tagEntity.setEvent(event);
        Tag saved = tagRepository.save(tagEntity);
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                .log(Level.INFO, "Tag created: "+saved);
        return ResponseEntity.ok(saved);
    }

    /**
     * Get all tags
     * @param eventId an eventId
     * @return All tags in the repository
     */
    public ResponseEntity<List<Tag>> getTags(Long eventId){
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                .log(Level.INFO, "Tags requested");
        return ResponseEntity.ok(tagRepository.findTagsByEvent_Id(eventId));
    }

    /**
     * Get a tag by ID
     * @param id The ID of the tag
     * @param eventId the eventId
     * @return The found Tag, or null if not found
     */
    public ResponseEntity<Tag> getTagById(Long id, Long eventId){
        if (!eventRepository.findById(eventId).isPresent()) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                    .log(Level.WARNING, "404: Event not found via 'getTagById'");
            return ResponseEntity.notFound().build();
        }

        if (!tagRepository.findById(id).isPresent()) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                    .log(Level.WARNING, "404: Tag not found via 'getTagById'");
            return ResponseEntity.notFound().build();
        }

        Event event = eventRepository.findById(eventId).get();
        Tag tag = tagRepository.findById(id).get();

        if(tag.getEvent() != event) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                    .log(Level.WARNING,
                            "404: Tag does not belong to event via 'getTagById'");
            return ResponseEntity.notFound().build();
        }
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                .log(Level.INFO, "Tag requested: "+tag);
        return ResponseEntity.ok(tag);
    }

    /**
     * Delete a tag by ID
     * @param id The ID of the tag to delete
     * @param eventId the eventId
     * @return The deleted tag, or null if not found
     */
    public ResponseEntity<Tag> deleteTag(Long eventId, Long id){
        if (!eventRepository.findById(eventId).isPresent()) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                    .log(Level.WARNING, "404: Event not found via 'deleteTag'");
            return ResponseEntity.notFound().build();
        }

        if (!tagRepository.findById(id).isPresent()) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                    .log(Level.WARNING, "404: Tag not found via 'deleteTag'");
            return ResponseEntity.notFound().build();
        }

        Event event = eventRepository.findById(eventId).get();
        Tag tag = tagRepository.findById(id).get();

        if(tag.getEvent() != event) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                    .log(Level.WARNING,
                            "404: Tag does not belong to event via 'deleteTag'");
            return ResponseEntity.notFound().build();
        }

        tagRepository.deleteById(id);
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                .log(Level.INFO, "Tag deleted: "+tag);
        return ResponseEntity.ok(tag);
    }

    /**
     * Update an existing tag
     * @param eventId eventId
     * @param newTag The updated tag
     * @param id the id
     * @return The updated tag
     */
    public ResponseEntity<Tag> updateTag(Long eventId, Tag newTag, Long id){
        if (!eventRepository.findById(eventId).isPresent()) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                    .log(Level.WARNING, "404: Event not found via 'updateTag'");
            return ResponseEntity.notFound().build();
        }

        if (!tagRepository.findById(id).isPresent()) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                    .log(Level.WARNING, "404: Tag not found via 'updateTag'");
            return ResponseEntity.notFound().build();
        }

        Event event = eventRepository.findById(eventId).get();
        Tag tag = tagRepository.findById(id).get();

        if(tag.getEvent() != event) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                    .log(Level.WARNING,
                            "404: Tag does not belong to event via 'updateTag'");
            return ResponseEntity.notFound().build();
        }

        tag.setName(newTag.getName());
        tag.setColor(newTag.getColor());
        tag.setEvent(newTag.getEvent());
        Tag saved = tagRepository.save(tag);
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                .log(Level.INFO, "Tag updated: "+saved);
        return ResponseEntity.ok(saved);
    }
}