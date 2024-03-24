package server.api;

import commons.Mail;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import server.service.MailService;

@Controller
@RequestMapping("/api/mail")
public class MailController {
    private final MailService mailService;

    /**
     * constructor for the mail controller
     * @param mailService a MailService
     */
    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    /**
     * send email
     * @param mail the Mail
     * @return a mail
     */
    @PostMapping
    public ResponseEntity<Mail> sendEmail(@RequestBody Mail mail) {
        return mailService.sendMail(mail);
    }
}
