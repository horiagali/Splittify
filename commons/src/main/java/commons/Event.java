package commons;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
public class Event {

    // Do we need this? Because I don't know if we can differentiate them by title
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String eventId;
    private String title;
    private Date creationDate;
    private Date lastActivityDate;
    private String inviteCode;

    @OneToMany(mappedBy = "event")
    private List<Expense> expenses; // List of expenses associated with the event
    @OneToMany(mappedBy = "event")
    private List<Participant> participants; // List of participants in the event

    /**
     * Creates an Event
     * @param eventId Unique identifier for the event
     * @param title Title of the event
     * @param creationDate Date when the event was created
     * @param lastActivityDate Date of the last activity related to the event
     * @param inviteCode Invite code for others to join the event
     */
    public Event(String eventId, String title, Date creationDate, Date lastActivityDate, String inviteCode) {
        this.eventId = eventId;
        this.title = title;
        this.creationDate = creationDate;
        this.lastActivityDate = lastActivityDate;
        this.inviteCode = inviteCode;
        this.expenses = new ArrayList<>();
        this.participants = new ArrayList<>();
    }

    public Event() {
        // it needs a constructor without arguments for whatever reason
    }

    // Getters and setters
    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastActivityDate() {
        return lastActivityDate;
    }

    public void setLastActivityDate(Date lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    // Methods to manage participants
    public void addParticipant(Participant participant) {
        participants.add(participant);
    }

    public void removeParticipant(Participant participant) {
        participants.remove(participant);
    }

    // Methods to manage expenses
    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    public void removeExpense(Expense expense) {
        expenses.remove(expense);
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

    /**
     * To String method
     * @return a string
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
