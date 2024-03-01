package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.util.ArrayList;


public class ExpenseTest {

    @Test
    public void testSettleBalance() {
        Participant payer = new Participant("sda", "john@example.com","bic","iban",12);
        ArrayList<Participant> owers = new ArrayList<>();
        owers.add(new Participant("Alice", "alice@example.com","bic","iban",12));
        owers.add(new Participant("Bob", "bob@example.com","bic","iban",12));
        double amount = 50;
        Tag tag = new Tag("testTag", Color.yellow);
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
        Participant payer = new Participant("John", "john@example.com","bic","iban",12);
        ArrayList<Participant> owers = new ArrayList<>();
        owers.add(new Participant("Alice", "alice@example.com","bic","iban",12));
        owers.add(new Participant("Bob", "bob@example.com","bic","iban",12));
        double amount = 50;
        Tag tag = new Tag("testTag", Color.yellow);
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
        Participant payer = new Participant("John", "john@example.com","bic","iban",12);
        ArrayList<Participant> owers = new ArrayList<>();
        owers.add(new Participant("Alice", "alice@example.com","bic","iban",12));
        owers.add(new Participant("Bob", "bob@example.com","bic","iban",12));
        Tag tag = new Tag("testTag", Color.yellow);
        Expense expense1 = new Expense("Dinner", 50, payer, owers, tag);
        Expense expense2 = new Expense("Dinner", 50, payer, owers, tag);
        Expense expense3 = new Expense("Lunch", 30, payer, owers, tag);

        assertEquals(expense1, expense2);
        assertNotEquals(expense1, expense3);
    }

    @Test
    public void testHashCode() {
        Participant payer = new Participant("John", "john@example.com","bic","iban",12);
        ArrayList<Participant> owers = new ArrayList<>();
        owers.add(new Participant("Alice", "alice@example.com","bic","iban",12));
        owers.add(new Participant("Bob", "bob@example.com","bic","iban",12));
        Tag tag = new Tag("testTag", Color.yellow);
        Expense expense1 = new Expense("Dinner", 50, payer, owers, tag);
        Expense expense2 = new Expense("Dinner", 50, payer, owers, tag);

        assertEquals(expense1.hashCode(), expense2.hashCode());
    }
//    @Test
//    public void testToString() {
//        Participant payer = new Participant("John", "john@example.com","bic","iban",12);
//        ArrayList<Participant> owers = new ArrayList<>();
//        owers.add(new Participant("Alice", "alice@example.com","bic","iban",12));
//        owers.add(new Participant("Bob", "bob@example.com","bic","iban",12));
//        Expense expense = new Expense("Dinner", 50, payer, owers);
//
//        String expected = "Expense{ id=0\n" +
//                "  title=Dinner\n" +
//                "  amount=50.0\n" +
//                "  payer=John\n" +
//                "  owers=[Participant{id=0, name='Alice', email='alice@example.com', balance=-25.0, event=null}," +
//                " Participant{id=0, name='Bob', email='bob@example.com', balance=-25.0, event=null}]\n" +
//                "]";
//        assertEquals(expected, expense.toString());
//    }

    @Test
    public void testGettersAndSetters() {
        // Create sample data
        Participant payer = new Participant("John", "john@example.com","bic","iban",12);
        ArrayList<Participant> owers = new ArrayList<>();
        owers.add(new Participant("Alice", "alice@example.com","bic","iban",12));
        owers.add(new Participant("Bob", "bob@example.com","bic","iban",12));
        double amount = 50;
        Tag tag = new Tag("testTag", Color.yellow);
        Expense expense = new Expense("Dinner", amount, payer, owers, tag);

        // Test getters
        assertEquals("Dinner", expense.getTitle());
        assertEquals(amount, expense.getAmount());
        assertEquals(payer, expense.getPayer());
        assertEquals(owers, expense.getOwers());

        // Modify values using setters
        expense.setTitle("Lunch");
        expense.setAmount(30);
        Participant newPayer = new Participant("Emma", "emma@example.com","bic","iban",12);
        ArrayList<Participant> newOwers = new ArrayList<>();
        newOwers.add(new Participant("David", "david@example.com","bic","iban",12));
        newOwers.add(new Participant("Eva", "eva@example.com","bic","iban",12));
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
        Participant payer = new Participant("John", "john@example.com","bic","iban",12);
        ArrayList<Participant> owers = new ArrayList<>();
        owers.add(new Participant("Alice", "alice@example.com","bic","iban",12));
        owers.add(new Participant("Bob", "bob@example.com","bic","iban",12));
        Tag tag = new Tag("testTag", Color.yellow);
        // Test if creating an expense with negative amount throws IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> new Expense("Dinner", -50, payer, owers, tag));
    }

    @Test
    public void testGetIdAndSetId() {
        Participant payer = new Participant("John", "john@example.com","bic","iban",12);
        ArrayList<Participant> owers = new ArrayList<>();
        owers.add(new Participant("Alice", "alice@example.com","bic","iban",12));
        owers.add(new Participant("Bob", "bob@example.com","bic","iban",12));
        double amount = 50;
        Tag tag = new Tag("testTag", Color.yellow);
        Expense expense = new Expense("Dinner", amount, payer, owers, tag);

        // Test getId() method
        assertEquals(0, expense.getId());

        // Test setId() method
        expense.setId(123);
        assertEquals(123, expense.getId());
    }

    @Test
    public void testDefaultConstructor() {
        // Create an Expense object using the default constructor
        Expense expense = new Expense();

        // Test if the default constructor initializes the object correctly
        assertNotNull(expense);
        assertEquals(0, expense.getId());
        assertNull(expense.getTitle());
        assertNull(expense.getPayer());
        assertNull(expense.getOwers());
        assertEquals(0.0, expense.getAmount());
    }
}
