package commons;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MailTest {

    @Test
    public void testMailConstructorAndGetters() {
        // Arrange
        String toEmail = "recipient@example.com";
        String subject = "Test Subject";
        String text = "This is a test email.";

        // Act
        Mail mail = new Mail(toEmail, subject, text);

        // Assert
        assertEquals(toEmail, mail.getToEmail());
        assertEquals(subject, mail.getSubject());
        assertEquals(text, mail.getText());
    }

    @Test
    public void testMailSetters() {
        // Arrange
        String toEmail = "recipient@example.com";
        String subject = "Test Subject";
        String text = "This is a test email.";
        Mail mail = new Mail();

        // Act
        mail.setToEmail(toEmail);
        mail.setSubject(subject);
        mail.setText(text);

        // Assert
        assertEquals(toEmail, mail.getToEmail());
        assertEquals(subject, mail.getSubject());
        assertEquals(text, mail.getText());
    }
}
