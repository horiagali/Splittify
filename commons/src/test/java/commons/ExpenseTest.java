package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.util.ArrayList;


public class ExpenseTest {

    private Participant payer;
    private ArrayList<Participant> owers;
    private double amount;
    private Tag tag;

    @BeforeEach
    public void setUp() {
        payer = new Participant("John", "john@example.com", "bic", "iban", 12);
        owers = new ArrayList<>();
        owers.add(new Participant("Alice", "alice@example.com", "bic", "iban", 12));
        owers.add(new Participant("Bob", "bob@example.com", "bic", "iban", 12));
        amount = 50;
        tag = new Tag("testTag", javafx.scene.paint.Color.ALICEBLUE);
    }

    @Test
    public void testSettleBalance() {
        Expense expense = new Expense("Dinner", amount, payer, owers, tag);
        assertEquals(62, payer.getBalance());
        for (Participant ower : owers) {
            assertEquals(-13, ower.getBalance());
        }
        expense.settleBalance();

        assertEquals(12, payer.getBalance());
        for (Participant ower : owers) {
            assertEquals(12, ower.getBalance());
        }
    }

    @Test
    public void testReverseSettleBalances() {
        Expense expense = new Expense("Dinner", amount, payer, owers, tag);
        expense.settleBalance();
        expense.reverseSettleBalance();

        assertEquals(62, payer.getBalance());
        for (Participant ower : owers) {
            assertEquals(-13, ower.getBalance());
        }
    }

    @Test
    public void testEquals() {
        Expense expense1 = new Expense("Dinner", 50, payer, owers, tag);
        Expense expense2 = new Expense("Dinner", 50, payer, owers, tag);
        Expense expense3 = new Expense("Lunch", 30, payer, owers, tag);

        assertEquals(expense1, expense2);
        assertNotEquals(expense1, expense3);
    }

    @Test
    public void testHashCode() {
        Expense expense1 = new Expense("Dinner", 50, payer, owers, tag);
        Expense expense2 = new Expense("Dinner", 50, payer, owers, tag);

        assertEquals(expense1.hashCode(), expense2.hashCode());
    }


    @Test
    public void testGettersAndSetters() {
        Expense expense = new Expense("Dinner", amount, payer, owers, tag);

        // Test getters
        assertEquals("Dinner", expense.getTitle());
        assertEquals(amount, expense.getAmount());
        assertEquals(payer, expense.getPayer());
        assertEquals(owers, expense.getOwers());

        // Modify values using setters
        expense.setTitle("Lunch");
        expense.setAmount(30);
        Participant newPayer = new Participant("Emma", "emma@example.com", "bic", "iban", 12);
        ArrayList<Participant> newOwers = new ArrayList<>();
        newOwers.add(new Participant("David", "david@example.com", "bic", "iban", 12));
        newOwers.add(new Participant("Eva", "eva@example.com", "bic", "iban", 12));
        expense.setPayer(newPayer);
        expense.setOwers(newOwers);

        // Test if values were modified correctly
        assertEquals("Lunch", expense.getTitle());
        assertEquals(30, expense.getAmount());
        assertEquals(newPayer, expense.getPayer());
        assertEquals(newOwers, expense.getOwers());
    }

    @Test
    public void testNegativeAmount() {
        // Test if creating an expense with negative amount throws IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> new Expense("Dinner", -50, payer, owers, tag));
    }

    @Test
    public void testGetIdAndSetId() {
        Expense expense = new Expense("Dinner", amount, payer, owers, tag);

        // Test getId() method
        Long x = null;
        assertEquals(x, expense.getId());
        x = 123L;
        // Test setId() method
        expense.setId(x);
        assertEquals(x, expense.getId());
    }

    @Test
    public void testDefaultConstructor() {
        // Create an Expense object using the default constructor
        Expense expense = new Expense();
        Long x = 0L;
        // Test if the default constructor initializes the object correctly
        assertNotNull(expense);
        assertEquals(null, expense.getId());
        assertNull(expense.getTitle());
        assertNull(expense.getPayer());
        assertNull(expense.getOwers());
        assertEquals(0.0, expense.getAmount());
    }

    @Test
    public void testGetTag() {
        Expense expense = new Expense("Dinner", amount, payer, owers, tag);
        assertEquals(tag, expense.getTag());
    }

    @Test
    public void testSetTag() {
        Expense expense = new Expense("Dinner", amount, payer, owers, tag);
        Tag newTag = new Tag("newTag", javafx.scene.paint.Color.ALICEBLUE);
        expense.setTag(newTag);
        assertEquals(newTag, expense.getTag());
    }

}
