package server.api;

import commons.Participant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.service.ParticipantService;

import java.util.List;

@RestController
@RequestMapping("/api/participants")

public class ParticipantController {
    private final ParticipantService participantService;

    /**
     * Constructor for this service class
     * @param participantService service to be used
     */
    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    /**
     * Returns all participants in the database
     * @return List of participants
     */
    @GetMapping
    public List<Participant> getAll() {
        return participantService.getParticipants();
    }

    /**
     * Find a participant by their id
     * @param id id of the participant
     * @return 200 status of successful, else 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Participant> getById(@PathVariable Long id) {
        return participantService.getParticipantById(id);
    }

    /**
     * Add a participant to the database
     * @param participant Participant to be added
     * @return 200 code if successful
     */
    @PostMapping
    public ResponseEntity<Participant> add(@RequestBody Participant participant) {
        return participantService.createParticipant(participant);
    }

    /**
     * Delete a participant by their id
     * @param id id of the participant
     * @return status code 200 if successful, else 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Participant> removeById(@PathVariable Long id) {
        return participantService.removeParticipant(id);
    }

    /**
     * Endpoint for updating participants
     * @param id id of the participant to be updated
     * @param participant new participant data
     * @return 200 if successful, else 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Participant> update(@PathVariable Long id, @RequestBody Participant participant) {
        return participantService.updateParticipant(participant, id);
    }




}
