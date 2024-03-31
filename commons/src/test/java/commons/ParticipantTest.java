package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParticipantTest {

    private Participant participant;
    private Event event;
    private List<Expense> expenses;
    private List<Participant> participants;
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
    public void testParameterizedConstructorAndGetters() {
        // Assert
        assertEquals(1L, participant.getParticipantID());
        assertEquals("John", participant.getNickname());
        assertEquals("john@example.com", participant.getEmail());
        assertEquals("bic", participant.getBic());
        assertEquals("iban", participant.getIban());
        assertEquals(12, participant.getBalance());
        assertEquals("Birthday Party", participant.getEvent().getTitle());
        assertEquals("Celebrating John's birthday", participant.getEvent().getDescription());
    }

    @Test
    public void testEmptyConstructor() {
        // Act
        Participant emptyParticipant = new Participant();

        // Assert
        assertNull(emptyParticipant.getParticipantID());
        assertNull(emptyParticipant.getNickname());
        assertNull(emptyParticipant.getEmail());
        assertNull(emptyParticipant.getBic());
        assertNull(emptyParticipant.getIban());
        assertEquals(0.0, emptyParticipant.getBalance());
        assertNull(emptyParticipant.getEvent());
    }

    @Test
    public void testSetters() {
        // Act
        participant.setParticipantID(2L);
        participant.setNickname("Alice");
        participant.setEmail("alice@example.com");
        participant.setBic("BIC456");
        participant.setIban("IBAN456");
        participant.setBalance(200.0);
        Date date = new Date();
        Event newEvent = new Event("New Birthday Party", date, "Celebrating Alice's birthday", "New Party Hall", expenses, participants, tags);
        participant.setEvent(newEvent);

        // Assert
        assertEquals(2L, participant.getParticipantID());
        assertEquals("Alice", participant.getNickname());
        assertEquals("alice@example.com", participant.getEmail());
        assertEquals("BIC456", participant.getBic());
        assertEquals("IBAN456", participant.getIban());
        assertEquals(200.0, participant.getBalance());
        assertEquals("New Birthday Party", participant.getEvent().getTitle());
        assertEquals("Celebrating Alice's birthday", participant.getEvent().getDescription());
    }

    @Test
    public void testToString() {
        assertNotNull(participant.toString());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        Participant participant2 = new Participant(1L, "John", "john@example.com", 12.0, event, "bic", "iban");

        // Act & Assert
        assertEquals(participant, participant2);
        assertEquals(participant.hashCode(), participant2.hashCode());
    }
}
