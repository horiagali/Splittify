package client.utils;

import commons.Mail;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

public class EmailUtils {
    private static String host;
    private static Integer port;
    private static String username;

    private static String password;

    /**
     * getter for host
     * @return host
     */

    public static String getHost() {
        return host;
    }

    /**
     * getter for port
     * @return port
     */
    public static Integer getPort() {
        return port;
    }

    /**
     * getter for username
     * @return username
     */
    public static String getUsername() {
        return username;
    }

    /**
     * getter for password
     * @return password
     */
    public static String getPassword() {
        return password;
    }

    /**
     * setter for username
     * @param username a String
     */
    public static void setUsername(String username) {
        EmailUtils.username = username;
    }

    /**
     * setter for password
     * @param password
     */
    public static void setPassword(String password) {
        EmailUtils.password = password;
    }


    /**
     * setter for host
     * @param host
     */
    public static void setHost(String host) {
        EmailUtils.host = host;
    }

    /**
     * setter for port
     * @param port
     */
    public static void setPort(Integer port) {
        EmailUtils.port = port;
    }

    /**
     * mail sender...
     * @return wow
     */
    @Bean
    public static JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);

        mailSender.setUsername(username);
        mailSender.setPassword(password);

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
        message.setFrom(username);
        message.setCc(username);
        message.setText(mail.getText());
        message.setSubject(mail.getSubject());
        getJavaMailSender().send(message);
        return ResponseEntity.ok().build();
    }

}
