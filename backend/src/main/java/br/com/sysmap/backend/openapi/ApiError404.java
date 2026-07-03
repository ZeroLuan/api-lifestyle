package br.com.sysmap.backend.openapi;

import br.com.sysmap.backend.exception.config.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(
        responseCode = "404",
        description = "Recurso não encontrado",
        content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponseDTO.class),
                examples = {
                        @ExampleObject(
                                name = "Usuário não encontrado",
                                value = "{\"error\": \"Usuário não encontrado.\"}"
                        ),
                        @ExampleObject(
                                name = "Atividade não encontrada",
                                value = "{\"error\": \"Atividade não encontrada.\"}"
                        ),
                        @ExampleObject(
                                name = "Participante não encontrado",
                                value = "{\"error\": \"Participante não encontrado.\"}"
                        )
                }
        )
)
public @interface ApiError404 {
}
