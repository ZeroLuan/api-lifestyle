package br.com.sysmap.backend.dto.activity;

import br.com.sysmap.backend.enums.SubscriptionStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record ActivityResponseDTO(
    UUID id,
    String title,
    String description,
    String type,
    String address,
    String image,
    Boolean isPrivate,
    LocalDateTime scheduledDate,
    LocalDateTime completedAt,
    Integer participantCount,
    ActivityCreatorDTO creator,
    SubscriptionStatus userSubscriptionStatus,
    String confirmationCode
) {
}
