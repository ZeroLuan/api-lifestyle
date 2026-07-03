package br.com.sysmap.backend.dto.user;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record UpdateAvatarDTO(
    @NotNull(message = "O arquivo de avatar é obrigatório")
    MultipartFile avatar
) {
}
