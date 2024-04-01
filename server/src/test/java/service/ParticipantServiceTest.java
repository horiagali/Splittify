package service;

import commons.Event;
import commons.Participant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import server.database.EventRepository;
import server.database.ParticipantRepository;
import server.service.ParticipantService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ParticipantServiceTest {

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private ParticipantService participantService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testCreateParticipant_Success() {
        Long eventId = 1L;
        Event event = new Event();
        event.setId(eventId);
        Participant participant = new Participant("Alice", "alice@example.com", "bic", "iban", 0.0);
        participant.setEvent(event);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(participantRepository.save(any(Participant.class))).thenReturn(participant);

        ResponseEntity<Participant> response = participantService.createParticipant(eventId, participant);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(participant, response.getBody());
        verify(participantRepository, times(1)).save(any(Participant.class));
    }

    @Test
    void testCreateParticipant_InvalidParticipant() {
        Long eventId = 1L;
        Participant participant = new Participant(); // Invalid participant with null values

        ResponseEntity<Participant> response = participantService.createParticipant(eventId, participant);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(participantRepository, never()).save(any(Participant.class));
    }

    @Test
    void testCreateParticipant_EventNotFound() {
        Long eventId = 1L;
        Participant participant = new Participant("Alice", "alice@example.com", "bic", "iban", 0.0);

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        ResponseEntity<Participant> response = participantService.createParticipant(eventId, participant);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(participantRepository, never()).save(any(Participant.class));
    }

    @Test
    void testGetParticipants() {
        Long eventId = 1L;
        List<Participant> participants = Collections.singletonList(new Participant("Alice", "alice@example.com", "bic", "iban", 0.0));

        when(participantRepository.findParticipantsByEventId(eventId)).thenReturn(participants);

        ResponseEntity<List<Participant>> response = participantService.getParticipants(eventId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(participants, response.getBody());
    }

}

