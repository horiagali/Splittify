package server.api;

import java.util.List;

import commons.Tag;
import org.springframework.web.bind.annotation.*;

import server.service.TagService;

@RestController
@RequestMapping("/api/tags")
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
     * @return List of all tags.
     */
    @GetMapping
    @ResponseBody
    public List<Tag> getAllTags() {
        return tagService.getTags();
    }

    /**
     * Retrieves a tag by its ID.
     * @param id The ID of the tag to retrieve.
     * @return Tag object corresponding to the given ID,
     * or null if the tag is not found.
     */
    @GetMapping("/{id}")
    @ResponseBody
    public Tag getById(@PathVariable("id") Long id) {
        return tagService.getTagById(id);
    }

    /**
     * Adds a new tag.
     * @param tag The tag to add.
     * @return The added tag.
     */
    @PostMapping
    @ResponseBody
    public Tag createTag(@RequestBody Tag tag) {
        System.out.println(tag);
        return tagService.createTag(tag);
    }

    /**
     * Delete a tag by ID.
     * @param id The ID of the tag to delete.
     * @return The deleted tag.
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public Tag deleteTag(@PathVariable("id") Long id){
        return tagService.deleteTag(id);
    }

    /**
     * Update an existing tag.
     * @param tag The updated tag.
     * @param id The ID of the tag to update.
     * @return The updated tag.
     */
    @PutMapping("/{id}")
    @ResponseBody
    public Tag updateTag(@RequestBody Tag tag, @PathVariable("id") Long id){
        return tagService.updateTag(tag, id);
    }
}
