package server.database;

import commons.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    /**
     *
     * @param eventId
     * @return participants
     */
    List<Participant> findParticipantsByEventId(Long eventId);

}
