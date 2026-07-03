package br.com.sysmap.backend.dto.activity;

import java.util.UUID;

public record ActivityCreatorDTO(UUID id, String name, String avatar) {
}
