package server.service;

import commons.Event;
import commons.Participant;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.EventRepository;
import server.database.ParticipantRepository;

import java.util.List;


@Service
public class ParticipantService {
    private final ParticipantRepository participantRepository;
    private final EventRepository eventRepository;


    /**
     * Constructor for this service
     * @param eventRepository
     * @param participantRepository repo for the participants
     */
    public ParticipantService(ParticipantRepository participantRepository,
                              EventRepository eventRepository) {
        this.participantRepository = participantRepository;
        this.eventRepository = eventRepository;
    }

    /**
     * Create a new participant
     * @param eventId
     * @param participant participant to be added to the database
     * @return 200 if successful
     */
    @Transactional
    public ResponseEntity<Participant> createParticipant(Long eventId, Participant participant) {
        if (!eventRepository.findById(eventId).isPresent()) {
            System.out.println("Event with given ID not found");
            return ResponseEntity.notFound().build();
        }

        Event event = eventRepository.findById(eventId).get();

        Participant newParticipant = new Participant();
        newParticipant.setBalance(participant.getBalance());
        newParticipant.setBic(participant.getBic());
        newParticipant.setNickname(participant.getNickname());
        newParticipant.setEmail(participant.getEmail());
        newParticipant.setIban(participant.getIban());
        newParticipant.setEvent(event);
        Participant saved = participantRepository.save(newParticipant);
        return ResponseEntity.ok(saved);

    }

    /**
     * Find all participants in the repository
     * @param eventId
     * @return List of participants
     */
    public ResponseEntity<List<Participant>> getParticipants(Long eventId) {
        return ResponseEntity.ok(participantRepository.findParticipantsByEventId(eventId));
    }

    /**
     *
     * @param eventId
     * @param participantId
     * @return participant
     */
    public ResponseEntity<Participant> getParticipantById(Long eventId, Long participantId) {
        if (!eventRepository.findById(eventId).isPresent()) {
            System.out.println("Event with given ID not found");
            return ResponseEntity.notFound().build();
        }

        if (!participantRepository.findById(participantId).isPresent()) {
            System.out.println("Participant with given ID not found");
            return ResponseEntity.notFound().build();
        }
        Event event = eventRepository.findById(eventId).get();
        Participant participant = participantRepository.findById(participantId).get();

        if(participant.getEvent() != event) {
            System.out.println("Participant does not belong to event");
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(participant);
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

    /**
     *
     * @param eventId
     * @param participantId
     * @param changeParticipant
     * @return participant
     */
    public ResponseEntity<Participant> updateParticipant(Long eventId,
                                                         Long participantId,
                                                         Participant changeParticipant) {

        if (!eventRepository.findById(eventId).isPresent()) {
            System.out.println("Event with given ID not found");
            return ResponseEntity.notFound().build();
        }

        if (!participantRepository.findById(participantId).isPresent()) {
            System.out.println("Participant with given ID not found");
            return ResponseEntity.notFound().build();
        }
        Event event = eventRepository.findById(eventId).get();
        Participant participant = participantRepository.findById(participantId).get();

        if(participant.getEvent() != event) {
            System.out.println("Participant does not belong to event");
            return ResponseEntity.notFound().build();
        }
        participant.setBalance(changeParticipant.getBalance());
        participant.setBic(changeParticipant.getBic());
        participant.setNickname(changeParticipant.getNickname());
        participant.setEmail(changeParticipant.getEmail());
        participant.setIban(changeParticipant.getIban());
        Participant saved = participantRepository.save(participant);
        return ResponseEntity.ok(saved);
    }

    /**
     *
     * @param eventId
     * @param participantId
     * @return participant
     */
    public ResponseEntity<Participant> deleteParticipant(Long eventId, Long participantId) {
        if (!eventRepository.findById(eventId).isPresent()) {
            System.out.println("Event with given ID not found");
            return ResponseEntity.notFound().build();
        }

        if (!participantRepository.findById(participantId).isPresent()) {
            System.out.println("Participant with given ID not found");
            return ResponseEntity.notFound().build();
        }
        Event event = eventRepository.findById(eventId).get();
        Participant participant = participantRepository.findById(participantId).get();

        if(participant.getEvent() != event) {
            System.out.println("Participant does not belong to event");
            return ResponseEntity.notFound().build();
        }
        participantRepository.deleteById(participantId);
        return ResponseEntity.ok(participant);
    }

}
