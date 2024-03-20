package server.service;

import commons.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    @Autowired
    private JavaMailSender javaMailSender;

    /**
     * send mail
     * @param mail the mail
     * @return the sent mail
     */
    public ResponseEntity<Mail> sendMail(Mail mail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mail.getToEmail());
        message.setFrom("ooppteam56@gmail.com");
        message.setText(mail.getText());
        message.setSubject(mail.getSubject());
        javaMailSender.send(message);
        return ResponseEntity.ok().build();
    }
}
