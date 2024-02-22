package commons;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EventTest {
    
    private Event event;
    private Event event2;
    private Event event3;
    private Participant participant;
    private Expense expense;

    @BeforeEach
    void setup() {
        event = new Event("ABC123", "Ski Trip", null, null, "abcpretendthisisacode" );
        event2 = new Event("ABC123", "Ski Trip", null, null, "abcpretendthisisacode" );
        event3 = new Event("ABC123", "Party", null, null, "abcpretendthisisacode" );
        participant = new Participant("Martijn", "martijn@gmail.com");
        expense = new Expense("Bought flowers for the group", 20.5);
        
    }

    @Test
    void testEqualsSame() {
        assertEquals(event, event);
    }

    @Test
    void testEqualsOther() {
        assertEquals(event, event2);
    }

    @Test
    void testNotEquals() {
        assertNotEquals(event, event3);
    }

    @Test
    void testParticipantAdder() {
        event.addParticipant(participant);
        assertTrue(event.getParticipants().contains(participant));
    }

    @Test
    void testExpenseAdder() {
        event.addExpense(expense);
        assertTrue(event.getExpenses().contains(expense));
    }

}
