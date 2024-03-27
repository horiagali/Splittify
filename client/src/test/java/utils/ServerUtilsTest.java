package utils;

import client.utils.ServerUtils;
import commons.Event;
import commons.Expense;
import commons.Participant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ServerUtilsTest {

    private ServerUtils serverUtils;
    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() throws IOException, InterruptedException {
        restTemplate = mock(RestTemplate.class);
        serverUtils = new ServerUtils();
        serverUtils.setRestTemplate(restTemplate); // Inject the mocked RestTemplate
    }

    @Test
    public void testAddExpense() {
        // Create mock objects
        Event event = new Event();
        event.setId(1L);

        Participant payer = new Participant();
        payer.setNickname("payer");

        List<Participant> selectedParticipants = new ArrayList<>();
        Participant participant1 = new Participant();
        participant1.setNickname("participant1");
        selectedParticipants.add(participant1);

        // Mock the postForObject method of restTemplate
        Expense expectedExpense = new Expense();
        when(restTemplate.postForObject(anyString(), any(), ArgumentMatchers.<Class<Expense>>any()))
                .thenReturn(expectedExpense);

        // Call the method to be tested
        Expense result = serverUtils.addExpenseToEvent(event.getId(), expectedExpense);

        // Verify that the restTemplate method was called with the expected arguments
        verify(restTemplate).postForObject(anyString(), any(), ArgumentMatchers.<Class<Expense>>any());

        // Assert that the result is the same as the expected expense
        assertEquals(expectedExpense, result);
    }
}
