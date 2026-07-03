package br.com.sysmap.backend.dto.activity;

import java.util.List;

public record PaginatedActivitiesDTO(
    List<ActivityResponseDTO> content,
    Integer page,
    Integer pageSize,
    Long totalElements,
    Integer totalPages
) {
}
