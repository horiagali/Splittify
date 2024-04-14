package service;

import org.junit.jupiter.api.Test;
import server.service.PasswordService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
public class PasswordServiceTest {

    @Test
    public void testGeneratePassword() {
        // Generate a password using PasswordService
        String password = PasswordService.generatePassword();

        // Verify password length
        assertEquals(11, password.length());

        // Verify password format (URL-safe Base64 without padding)
        assertTrue(password.matches("^[A-Za-z0-9_-]+$"));

        // Since this is an indirect output test,
        // we can't directly assert the exact value of the password.
        // Instead, we validate its properties and format.
        System.out.println("Generated password: " + password);
    }

}