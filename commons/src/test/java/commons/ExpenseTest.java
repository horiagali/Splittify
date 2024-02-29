package commons;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class ExpenseTest {

    @Test
    public void testSettleBalance() {
        Participant payer = new Participant("John", "john@example.com");
        ArrayList<Participant> owers = new ArrayList<>();
        owers.add(new Participant("Alice", "alice@example.com"));
        owers.add(new Participant("Bob", "bob@example.com"));
        double amount = 50;
        Expense expense = new Expense("Dinner", amount, payer, owers);
        assertEquals(50, payer.getBalance());
        for (Participant ower : owers) {
            assertEquals(-25, ower.getBalance());
        }
        expense.settleBalance();

        assertEquals(0, payer.getBalance());
        for (Participant ower : owers) {
            assertEquals(0, ower.getBalance());
        }
    }

    @Test
    public void testReverseSettleBalances() {
        Participant payer = new Participant("John", "john@example.com");
        ArrayList<Participant> owers = new ArrayList<>();
        owers.add(new Participant("Alice", "alice@example.com"));
        owers.add(new Participant("Bob", "bob@example.com"));
        double amount = 50;
        Expense expense = new Expense("Dinner", amount, payer, owers);
        expense.settleBalance();
        expense.reverseSettleBalance();

        assertEquals(50, payer.getBalance());
        for (Participant ower : owers) {
            assertEquals(-25, ower.getBalance());
        }
    }

    @Test
    public void testEquals() {
        Participant payer = new Participant("John", "john@example.com");
        ArrayList<Participant> owers = new ArrayList<>();
        owers.add(new Participant("Alice", "alice@example.com"));
        owers.add(new Participant("Bob", "bob@example.com"));
        Expense expense1 = new Expense("Dinner", 50, payer, owers);
        Expense expense2 = new Expense("Dinner", 50, payer, owers);
        Expense expense3 = new Expense("Lunch", 30, payer, owers);

        assertTrue(expense1.equals(expense2));
        assertFalse(expense1.equals(expense3));
    }

    @Test
    public void testHashCode() {
        Participant payer = new Participant("John", "john@example.com");
        ArrayList<Participant> owers = new ArrayList<>();
        owers.add(new Participant("Alice", "alice@example.com"));
        owers.add(new Participant("Bob", "bob@example.com"));
        Expense expense1 = new Expense("Dinner", 50, payer, owers);
        Expense expense2 = new Expense("Dinner", 50, payer, owers);

        assertEquals(expense1.hashCode(), expense2.hashCode());
    }

    @Test
    public void testToString() {
        Participant payer = new Participant("John", "john@example.com");
        ArrayList<Participant> owers = new ArrayList<>();
        owers.add(new Participant("Alice", "alice@example.com"));
        owers.add(new Participant("Bob", "bob@example.com"));
        Expense expense = new Expense("Dinner", 50, payer, owers);

        String expected = "Expense{ id=0\n" +
                "  title=Dinner\n" +
                "  amount=50.0\n" +
                "  payer=John\n" +
                "  owers=[Participant{id=0, name='Alice', email='alice@example.com', balance=-25.0, event=null}," +
                " Participant{id=0, name='Bob', email='bob@example.com', balance=-25.0, event=null}]\n" +
                "]";
        assertEquals(expected, expense.toString());
    }
    @Test
    public void testGettersAndSetters() {
        // Create sample data
        Participant payer = new Participant("John", "john@example.com");
        ArrayList<Participant> owers = new ArrayList<>();
        owers.add(new Participant("Alice", "alice@example.com"));
        owers.add(new Participant("Bob", "bob@example.com"));
        double amount = 50;
        Expense expense = new Expense("Dinner", amount, payer, owers);

        // Test getters
        assertEquals("Dinner", expense.getTitle());
        assertEquals(amount, expense.getAmount());
        assertEquals(payer, expense.getPayer());
        assertEquals(owers, expense.getOwers());

        // Modify values using setters
        expense.setTitle("Lunch");
        expense.setAmount(30);
        Participant newPayer = new Participant("Emma", "emma@example.com");
        ArrayList<Participant> newOwers = new ArrayList<>();
        newOwers.add(new Participant("David", "david@example.com"));
        newOwers.add(new Participant("Eva", "eva@example.com"));
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
        Participant payer = new Participant("John", "john@example.com");
        ArrayList<Participant> owers = new ArrayList<>();
        owers.add(new Participant("Alice", "alice@example.com"));
        owers.add(new Participant("Bob", "bob@example.com"));

        // Test if creating an expense with negative amount throws IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> new Expense("Dinner", -50, payer, owers));
    }

    @Test
    public void testGetIdAndSetId() {
        Participant payer = new Participant("John", "john@example.com");
        ArrayList<Participant> owers = new ArrayList<>();
        owers.add(new Participant("Alice", "alice@example.com"));
        owers.add(new Participant("Bob", "bob@example.com"));
        double amount = 50;
        Expense expense = new Expense("Dinner", amount, payer, owers);

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
