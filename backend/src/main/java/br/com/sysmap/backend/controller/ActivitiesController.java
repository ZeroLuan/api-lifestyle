package br.com.sysmap.backend.controller;

import br.com.sysmap.backend.dto.activity.*;
import br.com.sysmap.backend.dto.common.ActivityTypeDTO;
import br.com.sysmap.backend.dto.common.SuccessResponseDTO;
import br.com.sysmap.backend.openapi.ActivitiesDoc;
import br.com.sysmap.backend.service.ActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivitiesController implements ActivitiesDoc {

    private final ActivityService activityService;

    @PostMapping(value = "/new", consumes = "multipart/form-data")
    public ResponseEntity<ActivityResponseDTO> createActivity(@Valid @ModelAttribute CreateActivityDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(activityService.createActivity(dto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ActivityResponseDTO>> getAllActivities(
            @RequestParam(required = false) UUID typeId,
            @RequestParam(defaultValue = "createdAt") String orderBy,
            @RequestParam(defaultValue = "desc") String order) {
        return ResponseEntity.ok(activityService.getAllActivities(typeId, orderBy, order));
    }

    @GetMapping
    public ResponseEntity<PaginatedActivitiesDTO> getActivities(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) UUID typeId,
            @RequestParam(defaultValue = "createdAt") String orderBy,
            @RequestParam(defaultValue = "desc") String order) {
        return ResponseEntity.ok(activityService.getActivities(page, pageSize, typeId, orderBy, order));
    }

    @GetMapping("/types")
    public ResponseEntity<List<ActivityTypeDTO>> getTypes() {
        return ResponseEntity.ok(activityService.getTypes());
    }

    @PutMapping(value = "/{id}/update", consumes = "multipart/form-data")
    public ResponseEntity<ActivityResponseDTO> updateActivity(
            @PathVariable UUID id,
            @Valid @ModelAttribute UpdateActivityDTO dto) {
        return ResponseEntity.ok(activityService.updateActivity(id, dto));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<SuccessResponseDTO> deleteActivity(@PathVariable UUID id) {
        return ResponseEntity.ok(activityService.deleteActivity(id));
    }

    @PutMapping("/{id}/conclude")
    public ResponseEntity<SuccessResponseDTO> concludeActivity(@PathVariable UUID id) {
        return ResponseEntity.ok(activityService.concludeActivity(id));
    }

    @PostMapping("/{id}/subscribe")
    public ResponseEntity<SubscribeToActivityResponseDTO> subscribeToActivity(@PathVariable UUID id) {
        return ResponseEntity.ok(activityService.subscribeToActivity(id));
    }

    @DeleteMapping("/{id}/unsubscribe")
    public ResponseEntity<SuccessResponseDTO> unsubscribeFromActivity(@PathVariable UUID id) {
        return ResponseEntity.ok(activityService.unsubscribeFromActivity(id));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<SuccessResponseDTO> approveParticipant(
            @PathVariable UUID id,
            @Valid @RequestBody ParticipantApprovalDTO dto) {
        return ResponseEntity.ok(activityService.approveParticipant(id, dto));
    }

    @PutMapping("/{id}/check-in")
    public ResponseEntity<SuccessResponseDTO> checkIn(
            @PathVariable UUID id,
            @Valid @RequestBody CheckInDTO dto) {
        return ResponseEntity.ok(activityService.checkIn(id, dto));
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ActivityParticipantDataDTO>> getActivityParticipants(@PathVariable UUID id) {
        return ResponseEntity.ok(activityService.getParticipants(id));
    }

    @GetMapping("/user/creator")
    public ResponseEntity<PaginatedActivitiesDTO> getActivitiesCreatedByLoggedUser(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok(activityService.getActivitiesCreatedByLoggedUser(page, pageSize));
    }

    @GetMapping("/user/creator/all")
    public ResponseEntity<List<ActivityResponseDTO>> getAllActivitiesCreatedByLoggedUser() {
        return ResponseEntity.ok(activityService.getAllActivitiesCreatedByLoggedUser());
    }

    @GetMapping("/user/participant")
    public ResponseEntity<PaginatedActivitiesDTO> getActivitiesWhereLoggedUserIsSubscribed(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok(activityService.getActivitiesWhereUserIsSubscribed(page, pageSize));
    }

    @GetMapping("/user/participant/all")
    public ResponseEntity<List<ActivityResponseDTO>> getAllActivitiesWhereLoggedUserIsSubscribed() {
        return ResponseEntity.ok(activityService.getAllActivitiesWhereUserIsSubscribed());
    }
}
