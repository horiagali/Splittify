package server.api;

import commons.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import server.service.ParticipantService;

import java.util.List;

@RestController
@RequestMapping("/api/events/{event_id}/participants")
public class ParticipantController {
    private final ParticipantService participantService;
    @Autowired
    private SimpMessagingTemplate template;

    /**
     * Constructor for this service class
     * @param participantService service to be used
     */
    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    /**
     *
     * @param eventId
     * @return participants
     */
    @GetMapping
    public ResponseEntity<List<Participant>> getAll(@PathVariable(name = "event_id") Long eventId) {
        return participantService.getParticipants(eventId);
    }

    /**
     *
     * @param eventId
     * @param participantId
     * @return participant
     */
    @GetMapping("/{participant_id}")
    public ResponseEntity<Participant> getById(@PathVariable(name = "event_id") Long eventId,
                                               @PathVariable(name = "participant_id")
                                               Long participantId) {
        return participantService.getParticipantById(eventId, participantId);
    }

    /**
     * Add a participant to the database
     * @param eventId
     * @param participant Participant to be added
     * @return 200 code if successful
     */
    @PostMapping
    @ResponseBody
    public ResponseEntity<Participant> createParticipant(@PathVariable(name = "event_id")
                                                             Long eventId,
                                                         @RequestBody Participant participant) {
        ResponseEntity<Participant> created =
                participantService.createParticipant(eventId, participant);
        if (created.getStatusCode().equals(HttpStatus.OK)) {
            template.convertAndSend("/topic/participants", created.getBody());
        }
        return created;
    }

    /**
     *
     * @param eventId
     * @param participantId
     * @param participant
     * @return participant
     */
    @PutMapping("/{participant_id}")
    @ResponseBody
    public ResponseEntity<Participant> update(@PathVariable(name = "event_id") Long eventId,
                                              @PathVariable(name = "participant_id")
                                              Long participantId,
                                              @RequestBody Participant participant) {
        ResponseEntity<Participant> updated =
                participantService.updateParticipant(eventId, participantId, participant);
        if (updated.getStatusCode().equals(HttpStatus.OK)) {
            template.convertAndSend("/topic/participants", updated.getBody());
        }
        return updated;
    }

    /**
     *
     * @param eventId
     * @param participantId
     * @return participant
     */
    @DeleteMapping("/{participant_id}")
    public ResponseEntity<Participant> delete(@PathVariable(name = "event_id") Long eventId,
                                              @PathVariable(name = "participant_id")
                                              Long participantId) {
        ResponseEntity<Participant> deleted =
                participantService.deleteParticipant(eventId, participantId);
        if (deleted.getStatusCode().equals(HttpStatus.OK)) {
            template.convertAndSend("/topic/participants", deleted.getBody());
        }
        return deleted;
    }

}
