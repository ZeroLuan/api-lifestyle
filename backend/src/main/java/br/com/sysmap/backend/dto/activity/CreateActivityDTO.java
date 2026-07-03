package br.com.sysmap.backend.dto.activity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

public record CreateActivityDTO(
    @NotBlank(message = "O título é obrigatório")
    String title,

    @NotBlank(message = "A descrição é obrigatória")
    String description,

    @NotNull(message = "O tipo da atividade é obrigatório")
    UUID typeId,

    @NotBlank(message = "O endereço é obrigatório")
    String address,

    @NotNull(message = "A imagem é obrigatória")
    MultipartFile image,

    @NotBlank(message = "A data agendada é obrigatória")
    String scheduledDate,

    @NotNull(message = "O campo isPrivate é obrigatório")
    Boolean isPrivate
) {
}
