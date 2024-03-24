package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DebtTest {

    @Test
    void testConstructorAndGetters() {
        User owedUser = new User(1, "Fayaz", "Fayaz@example.com", null);
        User indebtedUser = new User(2, "Mihnea", "mihnea@example.com", null);
        Double amount = 100.0;
        Debt debt = new Debt(1, owedUser, indebtedUser, amount);

        assertEquals(1, debt.getId());
        assertEquals(owedUser, debt.getOwed());
        assertEquals(indebtedUser, debt.getIndebted());
        assertEquals(amount, debt.getAmount());
    }

    @Test
    void testConstructorWithNegativeAmount() {
        User owedUser = new User(1, "Martijn", "martijn@example.com", null);
        User indebtedUser = new User(2, "Iulia", "iulia@example.com", null);
        Double amount = -100.0;

        assertThrows(IllegalArgumentException.class, () -> new Debt(1, owedUser, indebtedUser, amount));
    }

    @Test
    void testEqualsAndHashCode() {
        User owedUser1 = new User(1, "Martijn", "martijn@example.com", null);
        User owedUser2 = new User(1, "Martijn", "martijn@example.com", null);
        User indebtedUser = new User(2, "Iulia", "iulia@example.com", null);
        Double amount = 100.0;

        Debt debt1 = new Debt(1, owedUser1, indebtedUser, amount);
        Debt debt2 = new Debt(1, owedUser2, indebtedUser, amount);

        assertTrue(debt1.equals(debt2) && debt2.equals(debt1));
        assertEquals(debt1.hashCode(), debt2.hashCode());
    }

    @Test
    void testToString() {
        User owedUser = new User(2, "Iulia", "iulia@example.com", null);
        User indebtedUser = new User(1, "Martijn", "martijn@example.com", null);
        Double amount = 100.0;
        Debt debt = new Debt(1, owedUser, indebtedUser, amount);

        String expectedToString = "Debt{id=1, owed=User{id=2, name='Iulia', email='iulia@example.com', bankAccount=null}, " +
                "indebted=User{id=1, name='Martijn', email='martijn@example.com', bankAccount=null}, amount=100.0}";

        assertEquals(expectedToString, debt.toString());
    }
}
