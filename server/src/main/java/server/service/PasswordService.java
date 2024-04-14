package server.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class PasswordService {

    /**
     * Password generator.
     * @return password;
     */
    public static String generatePassword() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[8];
        secureRandom.nextBytes(randomBytes);
        String password = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        System.out.println("Admin password: " + password);
        return password;
    }
}
