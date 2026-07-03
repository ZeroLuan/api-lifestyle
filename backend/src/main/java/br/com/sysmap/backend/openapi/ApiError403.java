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
        responseCode = "403",
        description = "Acesso negado — conta desativada ou permissão insuficiente",
        content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponseDTO.class),
                examples = {
                        @ExampleObject(
                                name = "Conta desativada",
                                value = "{\"error\": \"Esta conta foi desativada e não pode ser utilizada.\"}"
                        ),
                        @ExampleObject(
                                name = "Permissão insuficiente",
                                value = "{\"error\": \"Você não tem permissão para realizar esta ação.\"}"
                        )
                }
        )
)
public @interface ApiError403 {
}
