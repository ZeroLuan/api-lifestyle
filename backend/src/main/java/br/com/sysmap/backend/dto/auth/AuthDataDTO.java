package br.com.sysmap.backend.dto.auth;

import br.com.sysmap.backend.dto.common.AchievementDTO;
import java.util.List;
import java.util.UUID;

public record AuthDataDTO(
    UUID id,
    String name,
    String email,
    String avatar,
    Integer xp,
    Integer level,
    String token,
    List<AchievementDTO> achievements
) {
}
