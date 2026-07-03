package br.com.sysmap.backend.dto.user;

import java.util.UUID;

public record UserPreferencesDTO(UUID typeId, String typeName, String typeDescription) {
}
