package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExpenseTest {

    @Test
    public void testEquals() {
        // Create two expenses with the same attributes
        Expense expense1 = new Expense("Expense 1", 100.0);
        Expense expense2 = new Expense("Expense 1", 100.0);

        // Assert that the expenses are equal
        assertEquals(expense1, expense2);
    }

    @Test
    public void testNotEquals() {
        // Create two expenses with different titles and amounts
        Expense expense1 = new Expense("Expense 1", 100.0);
        Expense expense2 = new Expense("Expense 2", 200.0);

        // Assert that the expenses are not equal
        assertNotEquals(expense1, expense2);
    }

    @Test
    public void testEqualsSameObject() {
        // Create an expense
        Expense expense = new Expense("Expense", 50.0);

        // Assert that the expense is equal to itself
        assertEquals(expense, expense);
    }

    @Test
    public void testEqualsNull() {
        // Create an expense
        Expense expense = new Expense("Expense", 50.0);

        // Assert that the expense is not equal to null
        assertNotEquals(expense, null);
    }

    @Test
    public void testHashCodeConsistency() {
        // Create two expenses with the same attributes
        Expense expense1 = new Expense("Expense 1", 100.0);
        Expense expense2 = new Expense("Expense 1", 100.0);

        // Assert that the hash codes are consistent
        assertEquals(expense1.hashCode(), expense2.hashCode());
    }

    @Test
    public void testToString() {
        // Create an expense
        Expense expense = new Expense("Expense", 50.0);

        // Assert that the toString method returns a non-empty string
        assertNotNull(expense.toString());
    }
}
