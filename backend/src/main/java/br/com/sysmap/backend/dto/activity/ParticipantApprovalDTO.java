package br.com.sysmap.backend.dto.activity;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ParticipantApprovalDTO(
    @NotNull(message = "O ID do usuário é obrigatório")
    UUID userId,

    @NotNull(message = "O status de aprovação é obrigatório")
    Boolean approved
) {
}
