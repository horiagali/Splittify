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

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    void testGetParticipantById_Success() {
        Long eventId = 1L;
        Long participantId = 1L;
        Event event = new Event();
        event.setId(eventId);
        Participant participant = new Participant("Alice", "alice@example.com", "bic", "iban", 0.0);
        participant.setEvent(event);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(participantRepository.findById(participantId)).thenReturn(Optional.of(participant));

        ResponseEntity<Participant> response = participantService.getParticipantById(eventId, participantId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(participant, response.getBody());
    }

    @Test
    void testGetParticipantById_EventNotFound() {
        Long eventId = 1L;
        Long participantId = 1L;

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        ResponseEntity<Participant> response = participantService.getParticipantById(eventId, participantId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(participantRepository, never()).findById(any());
    }

    @Test
    void testGetParticipantById_ParticipantNotFound() {
        Long eventId = 1L;
        Long participantId = 1L;
        Event event = new Event();
        event.setId(eventId);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(participantRepository.findById(participantId)).thenReturn(Optional.empty());

        ResponseEntity<Participant> response = participantService.getParticipantById(eventId, participantId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetParticipantById_ParticipantDoesNotBelongToEvent() {
        Long eventId = 1L;
        Long participantId = 1L;
        Event event = new Event();
        event.setId(eventId);

        Event anotherEvent = new Event(); // Another event
        anotherEvent.setId(2L);

        Participant participant = new Participant("Alice", "alice@example.com", "bic", "iban", 0.0);
        participant.setEvent(anotherEvent); // Set participant to another event

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(participantRepository.findById(participantId)).thenReturn(Optional.of(participant));

        ResponseEntity<Participant> response = participantService.getParticipantById(eventId, participantId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testRemoveParticipant_ParticipantFound() {
        Long participantId = 1L;
        Participant participant = new Participant("Alice", "alice@example.com", "bic", "iban", 0.0);
        participant.setParticipantID(participantId);

        when(participantRepository.findById(participantId)).thenReturn(Optional.of(participant));

        ResponseEntity<Participant> response = participantService.removeParticipant(participantId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(participant, response.getBody());
    }

    @Test
    void testRemoveParticipant_ParticipantNotFound() {
        Long participantId = 1L;

        when(participantRepository.findById(participantId)).thenReturn(Optional.empty());

        ResponseEntity<Participant> response = participantService.removeParticipant(participantId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testUpdateParticipant_NicknameNull() {
        Long eventId = 1L;
        Long participantId = 1L;
        Participant changeParticipant = new Participant(); // Initialize with null nickname

        ResponseEntity<Participant> response = participantService.updateParticipant(eventId, participantId, changeParticipant);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testUpdateParticipant_EventNotFound() {
        Long eventId = 1L;
        Long participantId = 1L;
        Participant changeParticipant = new Participant("Alice", "alice@example.com", "bic", "iban", 0.0);

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        ResponseEntity<Participant> response = participantService.updateParticipant(eventId, participantId, changeParticipant);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testUpdateParticipant_ParticipantNotFound() {
        Long eventId = 1L;
        Long participantId = 1L;
        Participant changeParticipant = new Participant("Alice", "alice@example.com", "bic", "iban", 0.0);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(new Event()));
        when(participantRepository.findById(participantId)).thenReturn(Optional.empty());

        ResponseEntity<Participant> response = participantService.updateParticipant(eventId, participantId, changeParticipant);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testUpdateParticipant_ParticipantNotBelongToEvent() {
        Long eventId = 1L;
        Long participantId = 1L;
        Participant changeParticipant = new Participant("Alice", "alice@example.com", "bic", "iban", 0.0);

        Event event = new Event();
        Participant participant = new Participant("Bob", "bob@example.com", "bic", "iban", 0.0);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(participantRepository.findById(participantId)).thenReturn(Optional.of(participant));

        ResponseEntity<Participant> response = participantService.updateParticipant(eventId, participantId, changeParticipant);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testUpdateParticipant_CommonCase() {
        Long eventId = 1L;
        Long participantId = 1L;

        Participant changeParticipant = new Participant("Alice", "alice@example.com", "bic", "iban", 100.0);

        Event event = new Event();
        event.setId(eventId);

        Participant existingParticipant = new Participant("Old Nickname", "old@example.com", "oldBic", "oldIban", 50.0);
        existingParticipant.setParticipantID(participantId);
        existingParticipant.setEvent(event);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(participantRepository.findById(participantId)).thenReturn(Optional.of(existingParticipant));
        when(participantRepository.save(any(Participant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<Participant> response = participantService.updateParticipant(eventId, participantId, changeParticipant);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Alice", response.getBody().getNickname());
        assertEquals("alice@example.com", response.getBody().getEmail());
        assertEquals("bic", response.getBody().getBic());
        assertEquals("iban", response.getBody().getIban());
        assertEquals(100.0, response.getBody().getBalance());
    }

    @Test
    void testDeleteParticipant_Success() {
        Long eventId = 1L;
        Long participantId = 1L;

        Event event = new Event();
        event.setId(eventId);

        Participant participant = new Participant("Alice", "alice@example.com", "bic", "iban", 100.0);
        participant.setParticipantID(participantId);
        participant.setEvent(event);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(participantRepository.findById(participantId)).thenReturn(Optional.of(participant));
        doNothing().when(participantRepository).deleteById(participantId);

        ResponseEntity<Participant> response = participantService.deleteParticipant(eventId, participantId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(participantRepository, times(1)).deleteById(participantId);
    }

}

