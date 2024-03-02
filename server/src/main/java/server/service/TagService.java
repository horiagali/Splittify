package server.service;

import commons.Tag;
import org.springframework.stereotype.Service;
import server.database.TagRepository;

import java.util.List;

@Service
public class TagService {
    private TagRepository tagRepository;

    /**
     * Constructor for TagService
     * @param tagRepository a TagRepository
     */
    protected TagService(TagRepository tagRepository){
        this.tagRepository = tagRepository;
    }

    /**
     * Create a tag
     * @param tag The new Tag
     * @return The new Tag
     */
    public Tag createTag(Tag tag){
        Tag tagEntity = new Tag(tag.getName(), tag.getColor());
        tagRepository.save(tagEntity);
        return tagEntity;
    }

    /**
     * Get all tags
     * @return All tags in the repository
     */
    public List<Tag> getTags(){
        return tagRepository.findAll();
    }

    /**
     * Get a tag by ID
     * @param id The ID of the tag
     * @return The found Tag, or null if not found
     */
    public Tag getTagById(Integer id){
        return tagRepository.findById(id).orElse(null);
    }

    /**
     * Delete a tag by ID
     * @param id The ID of the tag to delete
     * @return The deleted tag, or null if not found
     */
    public Tag deleteTag(Integer id){
        Tag toDelete = getTagById(id);
        if (toDelete != null)
            tagRepository.delete(toDelete);
        return toDelete;
    }

    /**
     * Update an existing tag
     * @param tag The updated tag
     * @param id the id
     * @return The updated tag
     */
    public Tag updateTag(Tag tag, Integer id){
        Tag existingTag = getTagById(id);
        if (existingTag == null)
            return null;
        existingTag.setName(tag.getName());
        existingTag.setColor(tag.getColor());
        tagRepository.save(existingTag);
        return existingTag;
    }
}