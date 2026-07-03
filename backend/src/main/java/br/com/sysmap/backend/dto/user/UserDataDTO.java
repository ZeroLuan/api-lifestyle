package br.com.sysmap.backend.dto.user;

import br.com.sysmap.backend.dto.common.AchievementDTO;
import java.util.List;
import java.util.UUID;

public record UserDataDTO(
    UUID id,
    String name,
    String email,
    String avatar,
    Integer xp,
    Integer level,
    List<AchievementDTO> achievements
) {
}
