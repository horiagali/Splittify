package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ExpenseTest {

    private Expense expense;
    private Participant payee;
    private ArrayList<Participant> payors;

    @BeforeEach
    void setUp() {
        payee = new Participant("Alice", "alice@example.com");
        payors = new ArrayList<>();
        payors.add(new Participant("Bob", "bob@example.com"));
        payors.add(new Participant("Charlie", "charlie@example.com"));
        expense = new Expense("Expense", 50.0, payee, payors);
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals("Expense", expense.getTitle());
        assertEquals(50.0, expense.getAmount());
        assertEquals(payee, expense.getPayee());
        assertEquals(payors, expense.getPayors());
    }

    @Test
    void testSetNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> expense.setAmount(-50.0));
    }

    @Test
    void testSetPayee() {
        Participant newPayee = new Participant("David", "david@example.com");
        expense.setPayee(newPayee);
        assertEquals(newPayee, expense.getPayee());
    }

    @Test
    void testSetPayors() {
        ArrayList<Participant> newPayors = new ArrayList<>();
        newPayors.add(new Participant("Eve", "eve@example.com"));
        expense.setPayors(newPayors);
        assertEquals(newPayors, expense.getPayors());
    }

    @Test
    void testSettleDebts() {
        // Create participants
        Participant payee = new Participant("John Doe", "john@example.com");
        Participant payor1 = new Participant("Jane Smith", "jane@example.com");
        Participant payor2 = new Participant("Bob Johnson", "bob@example.com");
        ArrayList<Participant> payors = new ArrayList<>();
        payors.add(payor1);
        payors.add(payor2);

        // Create an expense with a payee and payors
        Expense expense = new Expense("Expense", 50.0, payee, payors);

        // Initially, the debt of the payee and payors should be 0
        assertEquals(0.0, payee.getDebt());
        assertEquals(0.0, payor1.getDebt());
        assertEquals(0.0, payor2.getDebt());

        // Call the settleDebts() method
        expense.settleDebts();

        // After settling debts, the payee's debt should be increased by the total amount of the expense
        assertEquals(50.0, payee.getDebt());

        // Each payor's debt should be decreased by an equal share of the expense amount
        assertEquals(-25.0, payor1.getDebt());
        assertEquals(-25.0, payor2.getDebt());
    }




    @Test
    void testReverseSettleDebts() {
        expense.settleDebts();
        expense.reverseSettleDebts();
        assertEquals(0.0, payee.getDebt());
        assertEquals(0.0, payors.get(0).getDebt());
        assertEquals(0.0, payors.get(1).getDebt());
    }

    @Test
    void testEquals() {
        Expense sameExpense = new Expense("Expense", 50.0, payee, payors);
        assertEquals(expense, sameExpense);
    }

    @Test
    void testNotEquals() {
        Expense differentExpense = new Expense("Different Expense", 50.0, payee, payors);
        assertNotEquals(expense, differentExpense);
    }

    @Test
    void testHashCodeConsistency() {
        Expense sameExpense = new Expense("Expense", 50.0, payee, payors);
        assertEquals(expense.hashCode(), sameExpense.hashCode());
    }

    @Test
    public void testNegativeAmountConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new Expense("Negative Expense", -50.0,  payee, payors));
    }

    @Test
    public void testNegativeAmountSetter() {
        Expense expense = new Expense("Expense", 50.0, payee, payors);
        try {
            expense.setAmount(-50.0);
            fail("Expected IllegalArgumentException was not thrown");
        } catch (IllegalArgumentException e) {
            // This exception is expected
            assertEquals("Amount cannot be negative", e.getMessage());
        }
    }


    @Test
    public void testNegativeAmountInSettlement() {
        // Create participants
        Participant payee = new Participant("John Doe", "john@example.com");
        Participant payor = new Participant("Jane Smith", "jane@example.com");
        ArrayList<Participant> payors = new ArrayList<>();
        payors.add(payor);

        // Create an expense with a payee and payors
        Expense expense = new Expense("Expense", 50.0, payee, payors);

        // Initially settle debts
        expense.settleDebts();

        // Attempt to settle with negative amount
        assertThrows(IllegalArgumentException.class, () -> expense.setAmount(-50.0));

        // Ensure debts remain unchanged
        assertEquals(50.0, payee.getDebt());
    }

}
