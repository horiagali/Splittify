package server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Participant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import server.service.ParticipantService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ParticipantControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ParticipantService participantService;

    @InjectMocks
    private ParticipantController participantController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(participantController).build();
    }

    @Test
    public void testGetAllParticipants() throws Exception {
        when(participantService.getParticipants(anyLong())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/api/events/1/participants"))
                .andExpect(status().isOk());

        verify(participantService, times(1)).getParticipants(anyLong());
        verifyNoMoreInteractions(participantService);
    }

    @Test
    public void testGetParticipantById() throws Exception {
        when(participantService.getParticipantById(anyLong(), anyLong())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/api/events/1/participants/1"))
                .andExpect(status().isOk());

        verify(participantService, times(1)).getParticipantById(anyLong(), anyLong());
        verifyNoMoreInteractions(participantService);
    }

    @Test
    public void testCreateParticipant() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String participantJson = objectMapper.writeValueAsString(new Participant());

        when(participantService.createParticipant(anyLong(), any())).thenReturn(ResponseEntity.ok().build());

        /*mockMvc.perform(post("/api/events/1/participants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(participantJson))
                .andExpect(status().isOk());

        verify(participantService, times(1)).createParticipant(anyLong(), any());
        verifyNoMoreInteractions(participantService);*/
    }

    @Test
    public void testUpdateParticipant() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String participantJson = objectMapper.writeValueAsString(new Participant());

        when(participantService.updateParticipant(anyLong(), anyLong(), any())).thenReturn(ResponseEntity.ok().build());

        /*mockMvc.perform(put("/api/events/1/participants/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(participantJson))
                .andExpect(status().isOk());

        verify(participantService, times(1)).updateParticipant(anyLong(), anyLong(), any());
        verifyNoMoreInteractions(participantService);*/
    }

    @Test
    public void testDeleteParticipant() throws Exception {
        when(participantService.deleteParticipant(anyLong(), anyLong())).thenReturn(ResponseEntity.ok().build());

        /*mockMvc.perform(delete("/api/events/1/participants/1"))
                .andExpect(status().isOk());

        verify(participantService, times(1)).deleteParticipant(anyLong(), anyLong());
        verifyNoMoreInteractions(participantService);*/
    }
}
