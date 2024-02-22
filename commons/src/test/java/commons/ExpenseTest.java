package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExpenseTest {

    @Test
    public void testEquals() {
        Expense expense1 = new Expense("Expense 1", 100.0);
        Expense expense2 = new Expense("Expense 1", 100.0);
        assertEquals(expense1, expense2);
    }

    @Test
    public void testNotEquals() {
        Expense expense1 = new Expense("Expense 1", 100.0);
        Expense expense2 = new Expense("Expense 2", 200.0);
        assertNotEquals(expense1, expense2);
    }

    @Test
    public void testEqualsSameObject() {
        Expense expense = new Expense("Expense", 50.0);
        assertEquals(expense, expense);
    }

    @Test
    public void testEqualsNull() {
        Expense expense = new Expense("Expense", 50.0);
        assertNotEquals(expense, null);
    }

    @Test
    public void testHashCodeConsistency() {
        Expense expense1 = new Expense("Expense 1", 100.0);
        Expense expense2 = new Expense("Expense 1", 100.0);
        assertEquals(expense1.hashCode(), expense2.hashCode());
    }

    @Test
    public void testToString() {
        Expense expense = new Expense("Expense", 50.0);
        assertNotNull(expense.toString());
    }
}
