package server.service;

import commons.Event;
import commons.Participant;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.EventRepository;
import server.database.ParticipantRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ParticipantService {
    private final ParticipantRepository participantRepository;
    private final EventRepository eventRepository;


    /**
     * Constructor for this service
     * @param participantRepository repo for the participants
     */
    public ParticipantService(ParticipantRepository participantRepository, EventRepository eventRepository) {
        this.participantRepository = participantRepository;
        this.eventRepository = eventRepository;
    }

    /**
     * Create a new participant
     * @param participant participant to be added to the database
     * @return 200 if successful
     */
    @Transactional
    public ResponseEntity<Participant> createParticipant(Long eventId, Participant participant) {
        Optional<Event> event = eventRepository.findById(eventId);
        if(event.isEmpty()) throw new IllegalArgumentException("Event with given ID not found");
        else {
            Participant newParticipant = new Participant();
            newParticipant.setBalance(participant.getBalance());
            newParticipant.setBic(participant.getBic());
            newParticipant.setNickname(participant.getNickname());
            newParticipant.setEmail(participant.getEmail());
            newParticipant.setIban(participant.getIban());
            newParticipant.setEvent(event.get());
            Participant saved = participantRepository.save(newParticipant);
            return ResponseEntity.ok(saved);
        }
    }

    /**
     * Find all participants in the repository
     * @return List of participants
     */
    public List<Participant> getParticipants(Long eventId) {
        return participantRepository.findParticipantsByEventId(eventId);
    }

    public ResponseEntity<Participant> getParticipantById(Long eventId, Long participantId) {
        Optional<Event> event = eventRepository.findById(eventId);
        if(event.isEmpty()) throw new IllegalArgumentException("Event with given ID not found");
        Optional<Participant> participant = participantRepository.findById(participantId);
        if(participant.isEmpty()) throw new IllegalArgumentException("Participant with given ID not found");
        if(participant.get().getEvent() != event.get()) {
            throw new IllegalArgumentException("Participant doesn't belong to event");
        }
        return ResponseEntity.ok(participant.get());
    }

    /**
     * Remove participant by id
     * @param id id of participant to be removed
     * @return 200 if successful, 404 if not found
     */
    public ResponseEntity<Participant> removeParticipant(Long id) {
        if (!participantRepository.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Participant toBeRemoved = participantRepository.findById(id).get();
        participantRepository.deleteById(id);
        return ResponseEntity.ok(toBeRemoved);
    }

    public ResponseEntity<Participant> updateParticipant(Long eventId, Long participantId, Participant changeParticipant) {
        Optional<Event> event = eventRepository.findById(eventId);
        if(event.isEmpty()) throw new IllegalArgumentException("Event with given ID not found");
        Optional<Participant> participant = participantRepository.findById(participantId);
        if(participant.isEmpty()) throw new IllegalArgumentException("Participant with given ID not found");
        if(participant.get().getEvent() != event.get()) {
            throw new IllegalArgumentException("Participant doesn't belong to event");
        }
        participant.get().setBalance(changeParticipant.getBalance());
        participant.get().setBic(changeParticipant.getBic());
        participant.get().setNickname(changeParticipant.getNickname());
        participant.get().setEmail(changeParticipant.getEmail());
        participant.get().setIban(changeParticipant.getIban());
        Participant saved = participantRepository.save(participant.get());
        return ResponseEntity.ok(saved);
    }

    public ResponseEntity<Participant> deleteParticipant(Long eventId, Long participantId) {
        Optional<Event> event = eventRepository.findById(eventId);
        if(event.isEmpty()) throw new IllegalArgumentException("Event with given ID not found");
        Optional<Participant> participant = participantRepository.findById(participantId);
        if(participant.isEmpty()) throw new IllegalArgumentException("Participant with given ID not found");
        if(participant.get().getEvent() != event.get()) {
            throw new IllegalArgumentException("Participant doesn't belong to event");
        }
        participantRepository.delete(participant.get());
        return ResponseEntity.ok(participant.get());
    }
}
