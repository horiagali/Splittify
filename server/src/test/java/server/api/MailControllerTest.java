package server.api;

import commons.Mail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import server.service.MailService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

public class MailControllerTest {
    private MailService mailService;
    private MailController mailController;

    @BeforeEach
    void setUp() {
        mailService = mock(MailService.class);
        mailController = new MailController(mailService);
    }

    @Test
    public void testSendEmail() {

        Mail mail = new Mail();
        mail.setToEmail("recipient@example.com");
        mail.setSubject("Test Subject");
        mail.setText("Test Message");

        when(mailService.sendMail(mail)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        ResponseEntity<Mail> responseEntity = mailController.sendEmail(mail);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertSame(HttpStatus.OK, responseEntity.getStatusCode());

        verify(mailService, times(1)).sendMail(mail);
        verifyNoMoreInteractions(mailService);
    }
}
