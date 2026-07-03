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
        responseCode = "409",
        description = "Conflito — recurso já existente ou ação já realizada",
        content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponseDTO.class),
                examples = {
                        @ExampleObject(
                                name = "E-mail ou CPF já existe",
                                value = "{\"error\": \"O e-mail ou CPF informado já pertence a outro usuário.\"}"
                        ),
                        @ExampleObject(
                                name = "Já se registrou",
                                value = "{\"error\": \"Você já se registrou nesta atividade.\"}"
                        ),
                        @ExampleObject(
                                name = "Participação já confirmada",
                                value = "{\"error\": \"Você já confirmou sua participação nesta atividade.\"}"
                        )
                }
        )
)
public @interface ApiError409 {
}
