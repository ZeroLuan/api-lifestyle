package br.com.sysmap.backend.openapi;

import br.com.sysmap.backend.dto.common.SuccessResponseDTO;
import br.com.sysmap.backend.dto.user.*;
import br.com.sysmap.backend.exception.config.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@Tag(name = "Usuários", description = "Contém todas as operações relacionadas ao usuário autenticado.")
public interface UsersDoc {

    @Operation(
            summary = "Buscar dados do usuário",
            description = "Endpoint para buscar os dados do usuário logado.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Dados do usuário retornados com sucesso",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserDataDTO.class)
                            )
                    )
            }
    )
    @ApiError401
    @ApiError403
    @ApiError500
    ResponseEntity<UserDataDTO> getUser();


    @Operation(
            summary = "Editar dados do usuário",
            description = "Endpoint para editar dados do usuário logado.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Dados do usuário atualizados com sucesso",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserDataDTO.class)
                            )
                    )
            }
    )
    @ApiError401
    @ApiError403
    @ApiError409
    @ApiError500
    ResponseEntity<UserDataDTO> updateUserData(@Valid @RequestBody UpdateUserDTO dto);


    @Operation(
            summary = "Editar foto de perfil do usuário",
            description = "Endpoint para atualizar a foto de perfil do usuário logado.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Foto de perfil atualizada com sucesso",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = AvatarUrlDTO.class)
                            )
                    )
            }
    )
    @ApiError400
    @ApiError401
    @ApiError403
    @ApiError500
    @PutMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<AvatarUrlDTO> updateUserAvatar(@Valid @ModelAttribute UpdateAvatarDTO dto);


    @Operation(
            summary = "Desativar conta do usuário",
            description = "Endpoint para desativar a conta do usuário logado.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Conta desativada com sucesso",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDTO.class),
                                    examples = @ExampleObject(
                                            name = "Sucesso ao desativar conta",
                                            value = "{\"error\": \"Conta desativada com sucesso.\"}"
                                    )
                            )
                    )
            }
    )
    @ApiError401
    @ApiError403
    @ApiError500
    ResponseEntity<SuccessResponseDTO> deleteUser();


    @Operation(
            summary = "Buscar interesses do usuário",
            description = "Endpoint para buscar os interesses do usuário logado.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Interesses retornados com sucesso",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserPreferencesDTO.class)
                            )
                    )
            }
    )
    @ApiError401
    @ApiError403
    @ApiError500
    ResponseEntity<List<UserPreferencesDTO>> getUserPreferences();


    @Operation(
            summary = "Definir preferências do usuário",
            description = "Endpoint para definir as preferências do usuário logado.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Preferências atualizadas com sucesso",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SuccessResponseDTO.class),
                                    examples = @ExampleObject(
                                            name = "Sucesso ao atualizar preferências",
                                            value = "{\"message\": \"Preferências atualizadas com sucesso.\"}"
                                    )
                            )
                    )
            }
    )
    @ApiError400
    @ApiError401
    @ApiError403
    @ApiError500
    ResponseEntity<SuccessResponseDTO> updateUserPreferences(@RequestBody List<UUID> typeIds);
}
