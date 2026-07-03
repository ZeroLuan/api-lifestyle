package br.com.sysmap.backend.controller;

import br.com.sysmap.backend.dto.activity.*;
import br.com.sysmap.backend.dto.common.SuccessResponseDTO;
import br.com.sysmap.backend.enums.SubscriptionStatus;
import br.com.sysmap.backend.service.ActivityService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivitiesControllerTest {

    @Mock
    private ActivityService activityService;

    @InjectMocks
    private ActivitiesController activitiesController;

    @Test
    @DisplayName("getActivities deve retornar lista paginada")
    void shouldReturnPaginatedActivities() {
        PaginatedActivitiesDTO dto = new PaginatedActivitiesDTO(List.of(), 1, 10, 0L, 0);
        when(activityService.getActivities(anyInt(), anyInt(), any(), anyString(), anyString())).thenReturn(dto);

        ResponseEntity<PaginatedActivitiesDTO> response = activitiesController.getActivities(1, 10, null, "createdAt", "desc");

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(activityService).getActivities(1, 10, null, "createdAt", "desc");
    }

    @Test
    @DisplayName("checkIn deve retornar sucesso")
    void shouldCheckIn() {
        UUID id = UUID.randomUUID();
        CheckInDTO dto = new CheckInDTO("123456");
        SuccessResponseDTO success = new SuccessResponseDTO("Check-in realizado");
        
        when(activityService.checkIn(eq(id), any())).thenReturn(success);

        ResponseEntity<SuccessResponseDTO> response = activitiesController.checkIn(id, dto);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Check-in realizado", response.getBody().message());
        verify(activityService).checkIn(id, dto);
    }

    @Test
    @DisplayName("subscribeToActivity deve retornar dados da inscrição")
    void shouldSubscribe() {
        UUID id = UUID.randomUUID();
        SubscribeToActivityResponseDTO subResponse = new SubscribeToActivityResponseDTO(
                UUID.randomUUID(), SubscriptionStatus.APPROVED, null, id, UUID.randomUUID());
        
        when(activityService.subscribeToActivity(id)).thenReturn(subResponse);

        ResponseEntity<SubscribeToActivityResponseDTO> response = activitiesController.subscribeToActivity(id);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(SubscriptionStatus.APPROVED, response.getBody().subscriptionStatus());
        verify(activityService).subscribeToActivity(id);
    }

    @Test
    @DisplayName("concludeActivity deve retornar sucesso")
    void shouldConcludeActivity() {
        UUID id = UUID.randomUUID();
        SuccessResponseDTO success = new SuccessResponseDTO("Concluída");
        when(activityService.concludeActivity(id)).thenReturn(success);

        ResponseEntity<SuccessResponseDTO> response = activitiesController.concludeActivity(id);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(activityService).concludeActivity(id);
    }

    @Test
    @DisplayName("getActivityParticipants deve retornar lista")
    void shouldGetParticipants() {
        UUID id = UUID.randomUUID();
        when(activityService.getParticipants(id)).thenReturn(List.of());

        ResponseEntity<List<ActivityParticipantDataDTO>> response = activitiesController.getActivityParticipants(id);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(activityService).getParticipants(id);
    }
}
