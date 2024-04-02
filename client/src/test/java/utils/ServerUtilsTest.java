package utils;

import client.utils.ServerUtils;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.Tag;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ServerUtilsTest {

    private ServerUtils serverUtils;
    private RestTemplate restTemplate;
    private Event event;
    private List<Expense> expenses;
    private List<Participant> participants;
    private List<Tag> tags;
    @Mock
    private Client clientMock;

    @Mock
    private WebTarget webTargetMock;

    @Mock
    private Invocation.Builder builderMock;

    @BeforeEach
    public void setUp() throws IOException, InterruptedException {
        MockitoAnnotations.openMocks(this);
        restTemplate = mock(RestTemplate.class);
        serverUtils = new ServerUtils();
        serverUtils.setClient(clientMock);
        serverUtils.setRestTemplate(restTemplate);


        when(clientMock.target(anyString())).thenReturn(webTargetMock);
        when(webTargetMock.request()).thenReturn(builderMock);

        expenses = new ArrayList<>();
        participants = new ArrayList<>();
        tags = new ArrayList<>();
        event = new Event("Birthday Party", new Date(), "Celebrating John's birthday", "Party Hall", expenses, participants, tags);
    }

//    @Test
//    public void testGetEvents() {
//        // Create mock objects
//        long eventId = 1603L;
//        Event expectedEvent = new Event();
//        expectedEvent.setId(eventId);
//        expectedEvent.setTitle("Stielt Party");
//
//        // Mock the get method of ClientBuilder
//        Response mockResponse = mock(Response.class);
//        when(mockResponse.readEntity(Event.class)).thenReturn(expectedEvent);
//        when(clientMock.target(anyString())).thenReturn(webTargetMock);
//        when(webTargetMock.path(anyString())).thenReturn(webTargetMock);
//        when(webTargetMock.request()).thenReturn(builderMock);
//        when(builderMock.accept(anyString())).thenReturn(builderMock);
//        when(builderMock.get(Response.class)).thenReturn(mockResponse);
//
//        // Call the method to be tested
//        Event result = serverUtils.getEvent(eventId);
//
//        // Verify that the methods were called with the expected arguments
//        verify(clientMock).target("http://localhost:8080/");
//        verify(webTargetMock).path("api/events/" + eventId);
//        verify(webTargetMock).request();
//        verify(builderMock).accept(APPLICATION_JSON);
//        verify(builderMock).get(Response.class);
//
//        // Assert that the result is the same as the expected event
//        assertEquals(expectedEvent, result);
//    }

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

    @Test
    public void testAddParticipant() {
        // Create mock objects
        Event event = new Event();
        event.setId(1L);

        Participant payer = new Participant();
        payer.setNickname("payer");

        // Mock the postForObject method of restTemplate
        Participant expectedParticipant = new Participant();
        when(restTemplate.postForObject(anyString(), any(), ArgumentMatchers.<Class<Participant>>any()))
                .thenReturn(expectedParticipant);
        // Call the method to be tested
        Participant result = serverUtils.addParticipant(event.getId(), expectedParticipant);

        // Verify that the restTemplate method was called with the expected arguments
        verify(restTemplate).postForObject(anyString(), any(), ArgumentMatchers.<Class<Expense>>any());

        // Assert that the result is the same as the expected expense
        assertEquals(expectedParticipant, result);
    }


}
