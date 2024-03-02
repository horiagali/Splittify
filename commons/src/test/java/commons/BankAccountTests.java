package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BankAccountTests {

    @Test
    public void testConstructors() {
        BankAccount acc1 = new BankAccount();
        BankAccount acc2 = new BankAccount("Owner", "IBAN", "BIC");

        assertNull(acc1.getOwner());
        assertNull(acc1.getIban());
        assertNull(acc1.getBic());

        assertNotNull(acc2.getOwner());
        assertNotNull(acc2.getBic());
        assertNotNull(acc2.getIban());
    }

    @Test
    public void testEqualsAndHash() {
        BankAccount acc1 = new BankAccount("John", "123455", "000000");
        BankAccount acc2 = new BankAccount("John", "123455", "000000");
        BankAccount acc3 = new BankAccount("Mike", "542315", "111111");

        assertEquals(acc1, acc2);
        assertNotEquals(acc1, acc3);

        assertEquals(acc1.hashCode(), acc2.hashCode());
        assertNotEquals(acc1.hashCode(), acc3.hashCode());
    }

    @Test
    public void testSetters() {
        BankAccount acc = new BankAccount();

        assertNull(acc.getOwner());
        assertNull(acc.getIban());
        assertNull(acc.getBic());

        acc.setOwner("Owner");
        acc.setIban("Iban");
        acc.setBic("Bic");

        assertEquals(acc.getOwner(), "Owner");
        assertEquals(acc.getIban(), "Iban");
        assertEquals(acc.getBic(), "Bic");
    }

}
