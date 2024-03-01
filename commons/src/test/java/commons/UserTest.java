package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void testGettersAndSetters() {
        User user = new User(12, "John", "johndoe@tudelft.nl",
                new BankAccount("John Doe", "IBAN", "BIC"));

        assertEquals(user.getId(), 12);
        assertEquals(user.getName(), "John");
        assertEquals(user.getEmail(), "johndoe@tudelft.nl");
        assertEquals(user.getBank(), new BankAccount("John Doe", "IBAN", "BIC"));

        user.setId(100);
        user.setName("Mike");
        user.setEmail("mike@tudelft.nl");
        user.setBankAccount(new BankAccount("Mike", "IBAN", "BIC"));

        assertEquals(100, user.getId());
        assertEquals("Mike", user.getName());
        assertEquals("mike@tudelft.nl", user.getEmail());
        assertEquals(user.getBank(), new BankAccount("Mike", "IBAN", "BIC"));
    }

    @Test
    public void testEqualsAndHashCode() {
        User userOne = new User(12, "John", "johndoe@tudelft.nl",
                new BankAccount("John Doe", "IBAN", "BIC"));
        User userTwo = new User(12, "John", "johndoe@tudelft.nl",
                new BankAccount("John Doe", "IBAN", "BIC"));
        User userThree = new User(15, "Mike", "mike@tudelft.nl",
                new BankAccount("Mike Doe", "IBAN", "BIC"));

        assertEquals(userOne, userTwo);
        assertNotEquals(userOne, userThree);

        assertEquals(userOne.hashCode(), userTwo.hashCode());
        assertNotEquals(userOne.hashCode(), userThree.hashCode());
    }

    @Test
    public void testToString() {
        User user = new User(12, "John", "johndoe@tudelft.nl",
                new BankAccount("John Doe", "IBAN", "BIC"));
        String string = "User{id=12, name='John', email='johndoe@tudelft.nl', " +
                "bankAccount=BankAccount{owner='John Doe', iban='IBAN', bic='BIC'}}";
        assertEquals(string, user.toString());
    }
}
