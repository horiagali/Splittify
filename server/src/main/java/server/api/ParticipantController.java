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

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @GetMapping
    public List<Participant> getAll() {
        return participantService.getParticipants();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Participant> getById(@PathVariable Long id) {
        return participantService.getParticipantById(id);
    }

    @PostMapping
    public ResponseEntity<Participant> add(@RequestBody Participant participant) {
        return participantService.createParticipant(participant);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Participant> removeById(@PathVariable Long id) {
        return participantService.removeParticipant(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Participant> update(@PathVariable Long id, @RequestBody Participant participant) {
        return participantService.updateParticipant(participant, id);
    }




}
