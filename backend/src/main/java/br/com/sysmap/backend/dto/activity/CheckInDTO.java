package br.com.sysmap.backend.dto.activity;

import jakarta.validation.constraints.NotBlank;

public record CheckInDTO(
    @NotBlank(message = "O código de confirmação é obrigatório")
    String confirmationCode
) {
}
