package br.com.sysmap.backend.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserDTO(
    String name,

    @Email(message = "Email inválido")
    String email,

    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    String password
) {
}
