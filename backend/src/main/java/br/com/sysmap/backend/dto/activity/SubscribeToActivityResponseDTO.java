package br.com.sysmap.backend.dto.activity;

import br.com.sysmap.backend.enums.SubscriptionStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record SubscribeToActivityResponseDTO(
    UUID id,
    SubscriptionStatus subscriptionStatus,
    LocalDateTime confirmedAt,
    UUID activityId,
    UUID userId
) {
}
