package server.service;

import commons.Participant;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.ParticipantRepository;

import java.util.List;

@Service
public class ParticipantService {
    private final ParticipantRepository participantRepository;

    public ParticipantService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

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

    public List<Participant> getParticipants() {
        return participantRepository.findAll();
    }

    public ResponseEntity<Participant> getParticipantById(Long id) {
        if (!participantRepository.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(participantRepository.findById(id).get());
    }

    public ResponseEntity<Participant> removeParticipant(Long id) {
        if (!participantRepository.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Participant toBeRemoved = participantRepository.findById(id).get();
        participantRepository.deleteById(id);
        return ResponseEntity.ok(toBeRemoved);
    }

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
