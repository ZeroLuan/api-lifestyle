package br.com.sysmap.backend.dto.activity;

import br.com.sysmap.backend.enums.SubscriptionStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record ActivityParticipantDataDTO(
    UUID id,
    UUID userId,
    String name,
    String avatar,
    SubscriptionStatus subscriptionStatus,
    LocalDateTime confirmedAt
) {
}
