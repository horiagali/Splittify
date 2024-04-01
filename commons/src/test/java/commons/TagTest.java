package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TagTest {

    private Participant participant;
    private Event event;
    private java.util.List<Expense> expenses;
    private java.util.List<Participant> participants;
    private List<Tag> tags;

    @BeforeEach
    public void setUp() {
        participant = new Participant("John", "john@example.com", "bic", "iban", 12);
        participant.setParticipantID(1L);
        expenses = new ArrayList<>();
        participants = new ArrayList<>();
        tags = new ArrayList<>();
        event = new Event("Birthday Party", new Date(), "Celebrating John's birthday", "Party Hall", expenses, participants, tags);
        participant.setEvent(event);
    }

    @Test
    public void testGettersAndSetters() {
        Tag tag = new Tag("John", "HEXcolor");

        tag.setId(123L);
        tag.setEvent(new Event());

        assertNotNull(tag.getName());
        assertNotNull(tag.getColor());
        assertNotNull(tag.getName());
        assertEquals(tag.getId(), 123);
    }

    @Test
    public void testEqualsAndHash() {
        String red = "HEXcolor1";

        Tag tag1 = new Tag("Tag", red);
        Tag tag2 = new Tag("Tag", red);
        Tag tag3 = new Tag("Tag", "HEXcolor");

        assertEquals(tag1, tag2);
        assertNotEquals(tag1, tag3);

        assertEquals(tag1.hashCode(), tag2.hashCode());
        assertNotEquals(tag1.hashCode(), tag3.hashCode());
    }

    @Test
    public void testToString() {
        String red = "HEXcolor";
        Tag tag = new Tag("tag", red);
        tag.setId(123L);
        tag.setEvent(new Event());

        String string = "ID: 123 name: tag color: " + red.toString();
        assertEquals(tag.toString(), string);
    }

    @Test
    public void testSetName() {
        // Arrange
        Tag tag = new Tag("John", "HEXcolor");

        // Act
        tag.setName("NewName");

        // Assert
        assertEquals("NewName", tag.getName());
    }

    @Test
    public void testSetColor() {
        // Arrange
        Tag tag = new Tag("John", "HEXcolor");

        // Act
        tag.setColor("NewColor");

        // Assert
        assertEquals("NewColor", tag.getColor());
    }

    @Test
    public void testGetEvent() {
        // Arrange
        Tag tag = new Tag("John", "HEXcolor");
        tag.setEvent(event);

        // Act
        Event retrievedEvent = tag.getEvent();

        // Assert
        assertNotNull(retrievedEvent);
        assertEquals(event, retrievedEvent);
    }

    @Test
    public void testEmptyConstructor() {
        // Arrange
        Tag tag = new Tag();

        // Assert
        assertNull(tag.getId());
        assertNull(tag.getName());
        assertNull(tag.getColor());
        assertNull(tag.getEvent());
    }

}
