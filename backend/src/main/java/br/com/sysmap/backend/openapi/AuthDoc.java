package br.com.sysmap.backend.openapi;

import br.com.sysmap.backend.dto.auth.AuthDataDTO;
import br.com.sysmap.backend.dto.auth.CreateUserDTO;
import br.com.sysmap.backend.dto.auth.SignInDTO;
import br.com.sysmap.backend.dto.common.SuccessResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Autenticação", description = "Contém as operações de cadastro e login de usuários.")
public interface AuthDoc {

    @Operation(
            summary = "Cadastro de usuário",
            description = "Endpoint para cadastrar um novo usuário.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Usuário criado com sucesso",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SuccessResponseDTO.class),
                                    examples = @ExampleObject(
                                            name = "Sucesso ao criar usuário",
                                            value = "{\"message\": \"Usuário criado com sucesso.\"}"
                                    )
                            )
                    )
            }
    )
    @ApiError400
    @ApiError409
    @ApiError500
    ResponseEntity<SuccessResponseDTO> register(@Valid @RequestBody CreateUserDTO dto);


    @Operation(
            summary = "Login de usuário",
            description = "Endpoint para realizar login com e-mail e senha.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Login realizado com sucesso — retorna token JWT e dados do usuário",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = AuthDataDTO.class)
                            )
                    )
            }
    )
    @ApiError400
    @ApiError401
    @ApiError403
    @ApiError404
    @ApiError500
    ResponseEntity<AuthDataDTO> signIn(@Valid @RequestBody SignInDTO dto);
}
