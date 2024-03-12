package server.api;

import java.util.List;

import commons.Tag;
import org.springframework.web.bind.annotation.*;

import server.service.TagService;

@RestController
@RequestMapping("/api/events/{event_id}/tags")
public class TagController {

    private final TagService tagService;

    /**
     * Constructor for the TagController
     * @param tagService a TagService
     */
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * Retrieves all tags.
     * @param eventId the eventId
     * @return List of all tags.
     */
    @GetMapping
    @ResponseBody
    public List<Tag> getAllTags(@PathVariable(name = "event_id") Long eventId) {
        return tagService.getTags(eventId);
    }

    /**
     * Retrieves a tag by its ID.
     * @param id The ID of the tag to retrieve.
     * @param eventId the eventId
     * @return Tag object corresponding to the given ID,
     * or null if the tag is not found.
     */
    @GetMapping("/{id}")
    @ResponseBody
    public Tag getById(@PathVariable("id") Long id, @PathVariable(name = "event_id") Long eventId) {
        return tagService.getTagById(id, eventId);
    }

    /**
     * Adds a new tag.
     * @param tag The tag to add.
     * @param eventId eventId
     * @return The added tag.
     */
    @PostMapping
    @ResponseBody
    public Tag createTag(@RequestBody Tag tag, @PathVariable(name = "event_id") Long eventId) {
        return tagService.createTag(tag, eventId);
    }

    /**
     * Delete a tag by ID.
     * @param eventId The ID of the event
     * @param tagId the id of the tag to delete
     * @return The deleted tag.
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public Tag deleteTag(@PathVariable(name = "event_id") Long eventId,
                         @PathVariable(name = "id") Long tagId){
        return tagService.deleteTag(eventId, tagId);
    }

    /**
     * Update an existing tag.
     * @param eventId eventId
     * @param tag The updated tag.
     * @param id The ID of the tag to update.
     * @return The updated tag.
     */
    @PutMapping("/{id}")
    @ResponseBody
    public Tag updateTag(@PathVariable(name = "event_id") Long eventId, @RequestBody Tag tag,
                         @PathVariable(name = "id") Long id){
        return tagService.updateTag(eventId, tag, id);
    }

}