package br.com.sysmap.backend.dto.common;

import java.util.UUID;

public record AchievementDTO(UUID id, String name, String criterion) {
}
