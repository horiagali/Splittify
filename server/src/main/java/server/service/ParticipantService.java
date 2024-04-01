package server.service;

import commons.Event;
import commons.Participant;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.EventRepository;
import server.database.ParticipantRepository;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


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
        if (participant.getNickname() == null || participant.getNickname().isBlank()) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                    .log(Level.WARNING, "400: Some required values may be null or empty");
            return ResponseEntity.badRequest().build();
        }
        if (!eventRepository.findById(eventId).isPresent()) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                    .log(Level.WARNING, "404: Event not found via 'createParticipant'");
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
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                .log(Level.INFO, "Participant created: "+saved);
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
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                    .log(Level.WARNING, "404: Event not found via 'getParticipantById'");
            return ResponseEntity.notFound().build();
        }

        if (!participantRepository.findById(participantId).isPresent()) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                    .log(Level.WARNING, "404: Participant not found via 'getParticipantById'");
            return ResponseEntity.notFound().build();
        }

        Event event = eventRepository.findById(eventId).get();
        Participant participant = participantRepository.findById(participantId).get();

        if(participant.getEvent() != event) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                    .log(Level.WARNING,
                            "404: Participant does not belong to event via 'getParticipantById'");
            return ResponseEntity.notFound().build();
        }
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                .log(Level.INFO,
                        "Participant requested: "+participant);
        return ResponseEntity.ok(participant);
    }

    /**
     * Remove participant by id
     * @param id id of participant to be removed
     * @return 200 if successful, 404 if not found
     */
    public ResponseEntity<Participant> removeParticipant(Long id) {
        if (!participantRepository.findById(id).isPresent()) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                    .log(Level.WARNING, "404: Participant not found via 'removeParticipant'");
            return ResponseEntity.notFound().build();
        }
        Participant toBeRemoved = participantRepository.findById(id).get();
        participantRepository.deleteById(id);
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                .log(Level.INFO, "Participant deleted: "+toBeRemoved);
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
        if (changeParticipant.getNickname() == null || changeParticipant.getNickname().isBlank()) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                    .log(Level.WARNING, "400: Some required values may be null or empty");
            return ResponseEntity.badRequest().build();
        }

        if (!eventRepository.findById(eventId).isPresent()) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                    .log(Level.WARNING, "404: Event not found via 'updateParticipant'");
            return ResponseEntity.notFound().build();
        }

        if (!participantRepository.findById(participantId).isPresent()) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                    .log(Level.WARNING, "404: Participant not found via 'updateParticipant'");
            return ResponseEntity.notFound().build();
        }

        Event event = eventRepository.findById(eventId).get();
        Participant participant = participantRepository.findById(participantId).get();

        if(participant.getEvent() != event) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                    .log(Level.WARNING,
                            "404: Participant does not belong to event via 'updateParticipant'");
            return ResponseEntity.notFound().build();
        }
        participant.setBalance(changeParticipant.getBalance());
        participant.setBic(changeParticipant.getBic());
        participant.setNickname(changeParticipant.getNickname());
        participant.setEmail(changeParticipant.getEmail());
        participant.setIban(changeParticipant.getIban());
        participant = participantRepository.save(participant);
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                .log(Level.INFO,
                        "Participant updated: "+participant);
        return ResponseEntity.ok(participant);
    }

    /**
     *
     * @param eventId
     * @param participantId
     * @return participant
     */
    public ResponseEntity<Participant> deleteParticipant(Long eventId, Long participantId) {
        if (!eventRepository.findById(eventId).isPresent()) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                    .log(Level.WARNING, "404: Event not found via 'deleteParticipant'");
            return ResponseEntity.notFound().build();
        }

        if (!participantRepository.findById(participantId).isPresent()) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                    .log(Level.WARNING, "404: Participant not found via 'deleteParticipant'");
            return ResponseEntity.notFound().build();
        }

        Event event = eventRepository.findById(eventId).get();
        Participant participant = participantRepository.findById(participantId).get();

        if(participant.getEvent() != event) {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                    .log(Level.WARNING,
                            "404: Participant does not belong to event via 'deleteParticipant'");
            return ResponseEntity.notFound().build();
        }
        participantRepository.deleteById(participantId);
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
                .log(Level.INFO,
                        "Participant deleted: "+participant);
        return ResponseEntity.ok(participant);
    }

}
