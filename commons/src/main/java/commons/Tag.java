package commons;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Tag {
    @Id
    private Integer id;

    @ManyToOne private Event event;
    /**
     * Constructs a new Tag object.
     */
    public Tag(){

    }

    /**
     * Sets the ID of the tag.
     * @param id The ID of the tag.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Retrieves the ID of the tag.
     * @return The ID of the tag.
     */
    public Integer getId() {
        return id;
    }
}
