package server.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import server.service.PasswordService;

@RestController
public class PasswordController {

    /**
     * Gets password.
     * @return password.
     */
    @GetMapping("/admin")
    public String getPassword() {
        return PasswordService.generatePassword();
    }
}
