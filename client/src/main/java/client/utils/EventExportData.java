package client.utils;

import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.Tag;

import java.util.List;

/**
 * Represents data to be exported for an event, including the event itself, associated tags,
 * participants, and expenses.
 */
public class EventExportData {
    private Event event;
    private List<Tag> tags;
    private List<Participant> participants;
    private List<Expense> expenses;

    /**
     * Default constructor.
     * Constructs an empty EventExportData object.
     */
    public EventExportData() {
    }

    /**
     * Constructs an EventExportData object with the
     * specified event, tags, participants, and expenses.
     *
     * @param event        The event to be exported.
     * @param tags         The list of tags associated with the event.
     * @param participants The list of participants involved in the event.
     * @param expenses     The list of expenses associated with the event.
     */
    public EventExportData(Event event, List<Tag> tags,
                           List<Participant> participants, List<Expense> expenses) {
        this.event = event;
        this.tags = tags;
        this.participants = participants;
        this.expenses = expenses;
    }

    /**
     * Gets the event to be exported.
     *
     * @return The event.
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Sets the event to be exported.
     *
     * @param event The event to be exported.
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * Gets the list of tags associated with the event.
     *
     * @return The list of tags.
     */
    public List<Tag> getTags() {
        return tags;
    }

    /**
     * Sets the list of tags associated with the event.
     *
     * @param tags The list of tags.
     */
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    /**
     * Gets the list of participants involved in the event.
     *
     * @return The list of participants.
     */
    public List<Participant> getParticipants() {
        return participants;
    }

    /**
     * Sets the list of participants involved in the event.
     *
     * @param participants The list of participants.
     */
    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    /**
     * Gets the list of expenses associated with the event.
     *
     * @return The list of expenses.
     */
    public List<Expense> getExpenses() {
        return expenses;
    }

    /**
     * Sets the list of expenses associated with the event.
     *
     * @param expenses The list of expenses.
     */
    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }
}
