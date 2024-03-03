package server.service;

import commons.Participant;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.ParticipantRepository;

import java.util.List;

@Service
public class ParticipantService {
    private final ParticipantRepository participantRepository;

    /**
     * Constructor for this service
     * @param participantRepository repo for the participants
     */
    public ParticipantService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    /**
     * Create a new participant
     * @param participant participant to be added to the database
     * @return 200 if successful
     */
    public ResponseEntity<Participant> createParticipant(Participant participant) {
        Participant participantEntity = new Participant(
                participant.getNickname(),
                participant.getEmail(),
                participant.getBic(),
                participant.getIban(),
                participant.getBalance());
       Participant saved = participantRepository.save(participantEntity);
        return ResponseEntity.ok(saved);
    }

    /**
     * Find all participants in the repository
     * @return List of participants
     */
    public List<Participant> getParticipants() {
        return participantRepository.findAll();
    }

    /**
     * Find participant by id
     * @param id id of the participant to be found
     * @return 200 if successful, 404 if not found
     */
    public ResponseEntity<Participant> getParticipantById(Long id) {
        if (!participantRepository.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(participantRepository.findById(id).get());
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
     * Update participant
     * @param participant new participant data
     * @param id id of the participant to be updated
     * @return 200 if successful, else 404 if not found
     */
    public ResponseEntity<Participant> updateParticipant(Participant participant, Long id) {
        if (!participantRepository.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Participant toBeUpdated = participantRepository.findById(id).get();

        toBeUpdated.setNickname(participant.getNickname());
        toBeUpdated.setEmail(participant.getEmail());
        toBeUpdated.setBic(participant.getBic());
        toBeUpdated.setIban(participant.getIban());
        toBeUpdated.setBalance(participant.getBalance());

        return ResponseEntity.ok(toBeUpdated);
    }
}
