package br.com.sysmap.backend.dto.common;

import java.util.UUID;

public record ActivityTypeDTO(UUID id, String name, String description) {
}
