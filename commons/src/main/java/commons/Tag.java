package commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


@Entity
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String color;


    @ManyToOne @JoinColumn(name = "event_id")
    @JsonIgnore
    private Event event;

    /**
     * constructor to create a Tag
     * @param name name of tag
     * @param color colour of tag
     */
    public Tag(String name, String color){
        this.name = name;
        this.color = color;
    }

    /**
     * Empty constructor
     */
    public Tag() {

    }

    /**
     * Sets the ID of the tag.
     * @param id The ID of the tag.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retrieves the ID of the tag.
     * @return The ID of the tag.
     */
    public long getId() {
        return id;
    }

    /**
     * Setter for the event of a tag.
     * @param event the event of the tag.
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * retrieve event this tag belongs to
     * @return event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * get the name of this tag
     * @return string name
     */
    public String getName() {
        return name;
    }

    /**
     * change the name of this tag
     * @param name name of tag
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * change the color of a tag
     * @param color colour of tag
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * get the color of a tag
     * @return colour of tag
     */
    public String getColor() {
        return color;
    }

    /**
     * toString method.
     * @return the toString expression of the tag.
     */
    @Override
    public String toString() {
        return "ID: " + id + " name: " + name + " color: " + color + "eventid: " + event.getId();
    }

    /**
     * Equals method
     * @param obj object to compare to
     * @return true iff same
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * hash
     * @return hash code
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }


}