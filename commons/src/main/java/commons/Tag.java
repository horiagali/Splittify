package commons;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.awt.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


@Entity
public class Tag {
    @Id
    private long id;
    private String name;
    private Color color;

    @ManyToOne private Event event;

    /**
     * constructor to create a Tag
     * @param name
     * @param color
     */
    public Tag(String name, Color color){
        this.name = name;
        this.color = color;
    }

    /**
     * Set the id of a tag
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * get the ID of a tag
     * @return
     */
    public long getId() {
        return id;
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
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * change the color of a tag
     * @param color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * get the color of a tag
     * @return
     */
    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "ID: " + id + " name: " + name + " color: " + color;
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
