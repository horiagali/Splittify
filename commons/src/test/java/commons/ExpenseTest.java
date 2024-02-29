
//package commons;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.ArrayList;
//import java.awt.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class ExpenseTest {
//
//    private Expense expense;
//    private Participant payee;
//    private ArrayList<Participant> payors;
//
//
//    @BeforeEach
//    void setUp() {
//        payee = new Participant("Iulia", "iulia@gmail.com", "DEUTDEFF", "FR1420041010050500013M02606", 50.50);
//        payors = new ArrayList<>();
//        payors.add(new Participant("Martijn", "martijn@gmail.com", "ABCDUS33", "E89370400440532013000", 102.75));
//        payors.add(new Participant("Horia", "horia@gmail.com", "XYZABCD1", "DE89370400440532013000", 567.89));
//        expense = new Expense("Expense", 50.0, payee, payors);
//    }
//
//    @Test
//    void testConstructorAndGetters() {
//        assertEquals("Expense", expense.getTitle());
//        assertEquals(50.0, expense.getAmount());
//        assertEquals(payee, expense.getPayee());
//        assertEquals(payors, expense.getPayors());
//    }
//
//    @Test
//    void testSetNegativeAmount() {
//        assertThrows(IllegalArgumentException.class, () -> expense.setAmount(-50.0));
//    }
//
//    @Test
//    void testSetPayee() {
//        Participant newPayee = new Participant("Mihnea", "mihnea@example.com", "QWERABCD", "DE12345678901234567890", 987.65);
//        expense.setPayee(newPayee);
//        assertEquals(newPayee, expense.getPayee());
//    }
//
//    @Test
//    void testSetPayors() {
//        ArrayList<Participant> newPayors = new ArrayList<>();
//        newPayors.add(new Participant("Mihnea", "mihnea@example.com", "QWERABCD", "DE12345678901234567890", 987.65));
//        expense.setPayors(newPayors);
//        assertEquals(newPayors, expense.getPayors());
//    }
//
//    @Test
//    void testSettleDebts() {
//        // Create participants
//        Participant payee = new Participant("Mihnea", "mihnea@example.com", "QWERABCD", "DE12345678901234567890", 987.65);
//        Participant payor1 = new Participant("Amanda", "Amanda@example.com", "QWETYSBCD", "FR12345678901234567890", 60.64);
//        Participant payor2 = new Participant("Fayaz", "Fayaz@example.com", "QWERABCD", "SP12345678901234567890", 10.95);
//        ArrayList<Participant> payors = new ArrayList<>();
//        payors.add(payor1);
//        payors.add(payor2);
//
//        // Create an expense with a payee and payors
//        Expense expense = new Expense("Expense", 50.0, payee, payors);
//
//        // Initially, the debt of the payee and payors should be 0
//        assertEquals(0.0, payee.getBalance());
//        assertEquals(0.0, payor1.getBalance());
//        assertEquals(0.0, payor2.getBalance());
//
//        // Call the settleDebts() method
//        expense.settleDebts();
//
//        // After settling debts, the payee's debt should be decreased by the total amount of the expense
//        assertEquals(-50.0, payee.getBalance());
//
//        // Each payor's debt should be increased by an equal share of the expense amount
//        assertEquals(25.0, payor1.getBalance());
//        assertEquals(25.0, payor2.getBalance());
//    }
//
//
//
//
//
//    @Test
//    void testReverseSettleDebts() {
//        expense.settleDebts();
//        expense.reverseSettleDebts();
//        assertEquals(0.0, payee.getBalance());
//        assertEquals(0.0, payors.get(0).getBalance());
//        assertEquals(0.0, payors.get(1).getBalance());
//    }
//
//    @Test
//    void testEquals() {
//        Expense sameExpense = new Expense("Expense", 50.0, payee, payors);
//        assertEquals(expense, sameExpense);
//    }
//
//    @Test
//    void testNotEquals() {
//        Expense differentExpense = new Expense("Different Expense", 50.0, payee, payors);
//        assertNotEquals(expense, differentExpense);
//    }
//
//    @Test
//    void testHashCodeConsistency() {
//        Expense sameExpense = new Expense("Expense", 50.0, payee, payors);
//        assertEquals(expense.hashCode(), sameExpense.hashCode());
//    }
//
//    @Test
//    public void testNegativeAmountConstructor() {
//        assertThrows(IllegalArgumentException.class, () -> new Expense("Negative Expense", -50.0,  payee, payors));
//    }
//
//    @Test
//    public void testNegativeAmountSetter() {
//        Expense expense = new Expense("Expense", 50.0, payee, payors);
//        try {
//            expense.setAmount(-50.0);
//            fail("Expected IllegalArgumentException was not thrown");
//        } catch (IllegalArgumentException e) {
//            // This exception is expected
//            assertEquals("Amount cannot be negative", e.getMessage());
//        }
//    }
//
//
//    @Test
//    public void testNegativeAmountInSettlement() {
//        // Create participants
//        Participant payee = new Participant("Mihnea", "mihnea@example.com", "QWERABCD", "DE12345678901234567890", 987.65);
//        Participant payor = new Participant("Amanda", "Amanda@example.com", "QWETYSBCD", "FR12345678901234567890", 60.64);
//        ArrayList<Participant> payors = new ArrayList<>();
//        payors.add(payor);
//
//        // Create an expense with a payee and payors
//        Expense expense = new Expense("Expense", 50.0, payee, payors);
//
//        // Initially settle debts
//        expense.settleDebts();
//
//        // Attempt to settle with negative amount
//        assertThrows(IllegalArgumentException.class, () -> expense.setAmount(-50.0));
//
//        // Ensure debts remain unchanged
//        assertEquals(50.0, payee.getBalance());
//    }
//
//}
