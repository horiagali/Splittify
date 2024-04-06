package client.utils;

import commons.Mail;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

public class EmailUtils {
    /**
     * horrible
     * @return wow
     */
    @Bean
    public static JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername("ooppteam56@gmail.com");
        mailSender.setPassword("cgcfhfqpssctwcos");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    /**
     * send mail
     * @param mail the mail
     * @return the sent mail
     */
    public static ResponseEntity<Mail> sendEmail(Mail mail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mail.getToEmail());
        message.setFrom("ooppteam56@gmail.com");
        message.setText(mail.getText());
        message.setSubject(mail.getSubject());
        getJavaMailSender().send(message);
        return ResponseEntity.ok().build();
    }

}
