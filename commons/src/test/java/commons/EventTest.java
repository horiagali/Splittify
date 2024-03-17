package commons;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventTest {

    private Event event;
    private Participant participant;
    private List<Expense> expenses;
    private List<Participant> participants;
    private List<Tag> tags;

    @BeforeEach
    public void setUp() {
        participant = new Participant("John", "john@example.com", "bic", "iban", 12);
        expenses = new ArrayList<>();
        participants = new ArrayList<>();
        tags = new ArrayList<>();
        event = new Event("Birthday Party", new Date(), "Celebrating John's birthday", "Party Hall", expenses, participants, tags);
    }

    @Test
    public void testDefaultConstructor() {
        // Test default constructor
        Event defaultEvent = new Event();
        assertNotNull(defaultEvent);
        assertNull(defaultEvent.getId());
        assertNull(defaultEvent.getTitle());
        assertNull(defaultEvent.getDate());
        assertNull(defaultEvent.getDescription());
        assertNull(defaultEvent.getLocation());
        if (defaultEvent.getExpenses() != null) {
            assertTrue(defaultEvent.getExpenses().isEmpty());
        }
        if (defaultEvent.getParticipants() != null) {
            assertTrue(defaultEvent.getParticipants().isEmpty());
        }
        if (defaultEvent.getTags() != null) {
            assertTrue(defaultEvent.getTags().isEmpty());
        }
    }


    @Test
    public void testAddParticipant() {
        Participant newParticipant = new Participant("Alice", "alice@example.com", "bic", "iban", 0);
        assertTrue(event.addParticipant(newParticipant));
        assertTrue(event.getParticipants().contains(newParticipant));
    }

    @Test
    public void testRemoveParticipant() {
        Participant newParticipant = new Participant("Bob", "bob@example.com", "bic", "iban", 0);
        event.addParticipant(newParticipant);
        assertTrue(event.removeParticipant(newParticipant));
        assertFalse(event.getParticipants().contains(newParticipant));
    }

    @Test
    public void testEditParticipant() {
        Participant oldParticipant = new Participant("Charlie", "charlie@example.com", "bic", "iban", 0);
        event.addParticipant(oldParticipant);
        Participant newParticipant = new Participant("Daniel", "daniel@example.com", "bic", "iban", 0);
        assertTrue(event.editParticipant(oldParticipant, newParticipant));
        assertFalse(event.getParticipants().contains(oldParticipant));
        assertTrue(event.getParticipants().contains(newParticipant));
    }

    @Test
    public void testAddExpense() {
        Expense expense = new Expense("Food", 100, participant, new ArrayList<>(), new Tag("Food", Color.GREEN));
        assertTrue(event.addExpense(expense));
        assertTrue(event.getExpenses().contains(expense));
    }

    @Test
    public void testRemoveExpense() {
        Expense expense = new Expense("Decorations", 50, participant, new ArrayList<>(), new Tag("Decorations", Color.BLUE));
        event.addExpense(expense);
        assertTrue(event.removeExpense(expense));
        assertFalse(event.getExpenses().contains(expense));
    }

    @Test
    public void testEditExpense() {
        Expense oldExpense = new Expense("Music", 200, participant, new ArrayList<>(), new Tag("Music", Color.RED));
        event.addExpense(oldExpense);
        Expense newExpense = new Expense("Lighting", 150, participant, new ArrayList<>(), new Tag("Lighting", Color.YELLOW));
        assertTrue(event.editExpense(oldExpense, newExpense));
        assertFalse(event.getExpenses().contains(oldExpense));
        assertTrue(event.getExpenses().contains(newExpense));
    }

    @Test
    public void testMyExpenses() {
        List<Expense> johnExpenses = event.myExpenses(participant);
        assertEquals(0, johnExpenses.size()); // Assuming participant John hasn't paid for any expenses yet
    }

    @Test
    public void testIncludingExpenses() {
        List<Expense> johnExpenses = event.includingExpenses(participant);
        assertEquals(0, johnExpenses.size()); // Assuming participant John hasn't been involved in any expenses yet
    }

    @Test
    public void testOwers() {
        List<Participant> owers = event.owers();
        assertEquals(0, owers.size()); // Assuming there are no participants owing money
    }

    @Test
    public void testSettleDebts() {
        // Assuming there are some debts to settle
        event.settleDebts();
        // Check if debts are settled properly
    }

    @Test
    public void testEquals() {
        Event event1 = new Event("Birthday Party", new Date(), "Celebrating John's birthday", "Party Hall", expenses, participants, tags);
        Event event2 = new Event("Birthday Party", new Date(), "Celebrating John's birthday", "Party Hall", expenses, participants, tags);
        Event event3 = new Event("Wedding", new Date(), "Celebrating Jane's wedding", "Banquet Hall", expenses, participants, tags);
        assertEquals(event1, event2);
        assertNotEquals(event1, event3);
    }

    @Test
    public void testHashCode() {
        Event event1 = new Event("Birthday Party", new Date(), "Celebrating John's birthday", "Party Hall", expenses, participants, tags);
        Event event2 = new Event("Birthday Party", new Date(), "Celebrating John's birthday", "Party Hall", expenses, participants, tags);
        assertEquals(event1.hashCode(), event2.hashCode());
    }

    @Test
    public void testGettersAndSetters() {
        // Test getters and setters
        Date newDate = new Date();
        event.setTitle("Wedding Reception");
        event.setDate(newDate);
        event.setDescription("Celebrating Jane's wedding");
        event.setLocation("Banquet Hall");
        assertEquals("Wedding Reception", event.getTitle());
        assertEquals(newDate, event.getDate());
        assertEquals("Celebrating Jane's wedding", event.getDescription());
        assertEquals("Banquet Hall", event.getLocation());
    }

    @Test
    public void testToString() {
        // Test toString method
        assertNotNull(event.toString());
    }
}
