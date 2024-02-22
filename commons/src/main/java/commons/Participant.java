package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import jakarta.persistence.*;

@jakarta.persistence.Table(indexes = {
        @Index(columnList = "name"),
        @Index(columnList = "email")
})

@Entity
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;
    private String email;
    private double debt;

    @ManyToOne
    private Event event;

    /**
     * Creates a participant
     * @param name name of participant
     * @param email email of participant
     */
    public Participant(String name, String email) {
        this.name = name;
        this.email = email;
        this.debt = 0;
    }

    public Participant() {
        // Default constructor required by JPA
    }

    // Getters and setters

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public double getDebt() {
        return debt;
    }

    public void setDebt(double debt) {
        this.debt = debt;
    }
    

    // Equals, hashCode, and toString methods

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

    /**
     * To String method
     * @return a string
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}