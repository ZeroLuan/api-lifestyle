package br.com.sysmap.backend.openapi;

import br.com.sysmap.backend.dto.activity.*;
import br.com.sysmap.backend.dto.common.ActivityTypeDTO;
import br.com.sysmap.backend.dto.common.SuccessResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Atividades", description = "Contém todas as operações relacionadas às atividades da plataforma.")
public interface ActivitiesDoc {

    @Operation(
            summary = "Criar uma atividade",
            description = "Endpoint para criar uma nova atividade.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Atividade criada com sucesso",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ActivityResponseDTO.class)
                            )
                    )
            }
    )
    @ApiError400
    @ApiError401
    @ApiError403
    @ApiError500
    @PostMapping(value = "/new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ActivityResponseDTO> createActivity(@Valid @ModelAttribute CreateActivityDTO dto);


    @Operation(
            summary = "Listar todas as atividades com filtro por tipo e ordenação",
            description = "Endpoint para listar todas as atividades registradas. Parâmetros opcionais: typeId (filtrar por tipo), orderBy (campo de ordenação) e order (asc/desc).",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Atividades retornadas com sucesso",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ActivityResponseDTO.class)
                            )
                    )
            }
    )
    @ApiError401
    @ApiError403
    @ApiError500
    ResponseEntity<List<ActivityResponseDTO>> getAllActivities(
            @RequestParam(required = false) UUID typeId,
            @RequestParam(defaultValue = "createdAt") String orderBy,
            @RequestParam(defaultValue = "desc") String order);


    @Operation(
            summary = "Listar atividades com paginação, filtro por tipo e ordenação",
            description = "Endpoint para listar de forma paginada as atividades registradas.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Atividades paginadas retornadas com sucesso",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = PaginatedActivitiesDTO.class)
                            )
                    )
            }
    )
    @ApiError401
    @ApiError403
    @ApiError500
    ResponseEntity<PaginatedActivitiesDTO> getActivities(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) UUID typeId,
            @RequestParam(defaultValue = "createdAt") String orderBy,
            @RequestParam(defaultValue = "desc") String order);


    @Operation(
            summary = "Listar tipos de atividades",
            description = "Endpoint para listar os tipos de atividades disponíveis.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Tipos de atividade retornados com sucesso",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ActivityTypeDTO.class)
                            )
                    )
            }
    )
    @ApiError401
    @ApiError403
    @ApiError500
    ResponseEntity<List<ActivityTypeDTO>> getTypes();


    @Operation(
            summary = "Editar uma atividade existente",
            description = "Endpoint para editar uma atividade existente. Apenas o criador pode editar.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Atividade editada com sucesso",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ActivityResponseDTO.class)
                            )
                    )
            }
    )
    @ApiError400
    @ApiError401
    @ApiError403
    @ApiError404
    @ApiError409
    @ApiError500
    @PutMapping(value = "/{id}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ActivityResponseDTO> updateActivity(
            @Parameter(description = "ID da atividade") @PathVariable UUID id,
            @Valid @ModelAttribute UpdateActivityDTO dto);

            
    @Operation(
            summary = "Excluir uma atividade existente",
            description = "Endpoint para excluir uma atividade existente. Apenas o criador pode excluir.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Atividade excluída com sucesso",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SuccessResponseDTO.class),
                                    examples = @ExampleObject(
                                            name = "Atividade excluída",
                                            value = "{\"message\": \"Atividade excluída com sucesso.\"}"
                                    )
                            )
                    )
            }
    )
    @ApiError401
    @ApiError403
    @ApiError404
    @ApiError500
    ResponseEntity<SuccessResponseDTO> deleteActivity(
            @Parameter(description = "ID da atividade") @PathVariable UUID id);


    @Operation(
            summary = "Concluir uma atividade",
            description = "Endpoint para concluir uma atividade. Apenas o criador pode concluí-la.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Atividade concluída com sucesso",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SuccessResponseDTO.class),
                                    examples = @ExampleObject(
                                            name = "Sucesso ao concluir atividade",
                                            value = "{\"message\": \"Atividade concluída com sucesso.\"}"
                                    )
                            )
                    )
            }
    )
    @ApiError401
    @ApiError403
    @ApiError404
    @ApiError500
    ResponseEntity<SuccessResponseDTO> concludeActivity(
            @Parameter(description = "ID da atividade") @PathVariable UUID id);


    @Operation(
            summary = "Inscrever-se em uma atividade",
            description = "Endpoint para o usuário logado se inscrever em uma atividade.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Inscrição realizada com sucesso",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SubscribeToActivityResponseDTO.class)
                            )
                    )
            }
    )
    @ApiError401
    @ApiError403
    @ApiError404
    @ApiError409
    @ApiError500
    ResponseEntity<SubscribeToActivityResponseDTO> subscribeToActivity(
            @Parameter(description = "ID da atividade") @PathVariable UUID id);


    @Operation(
            summary = "Cancelar inscrição em uma atividade",
            description = "Endpoint para cancelar a inscrição do usuário logado em uma atividade.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Inscrição cancelada com sucesso",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SuccessResponseDTO.class),
                                    examples = @ExampleObject(
                                            name = "Participação cancelada",
                                            value = "{\"message\": \"Participação cancelada com sucesso.\"}"
                                    )
                            )
                    )
            }
    )
    @ApiError400
    @ApiError401
    @ApiError403
    @ApiError404
    @ApiError500
    ResponseEntity<SuccessResponseDTO> unsubscribeFromActivity(
            @Parameter(description = "ID da atividade") @PathVariable UUID id);


    @Operation(
            summary = "Aprovar ou negar inscrição de participante em atividade privada",
            description = "Endpoint para aprovar ou negar inscrição de participante em atividade privada. Apenas o criador pode aprovar/negar.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Solicitação processada com sucesso",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SuccessResponseDTO.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Solicitação aprovada",
                                                    value = "{\"message\": \"Soliticação de participação aprovada com sucesso.\"}"
                                            ),
                                            @ExampleObject(
                                                    name = "Solicitação rejeitada",
                                                    value = "{\"message\": \"Soliticação de participação rejeitada com sucesso.\"}"
                                            )
                                    }
                            )
                    )
            }
    )
    @ApiError400
    @ApiError401
    @ApiError403
    @ApiError404
    @ApiError500
    ResponseEntity<SuccessResponseDTO> approveParticipant(
            @Parameter(description = "ID da atividade") @PathVariable UUID id,
            @Valid @RequestBody ParticipantApprovalDTO dto);


    @Operation(
            summary = "Fazer check-in em uma atividade usando código de confirmação",
            description = "Endpoint para fazer check-in em uma atividade utilizando o código de confirmação.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Participação confirmada com sucesso",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = SuccessResponseDTO.class),
                                    examples = @ExampleObject(
                                            name = "Participação confirmada",
                                            value = "{\"message\": \"Participação confirmada com sucesso.\"}"
                                    )
                            )
                    )
            }
    )
    @ApiError400
    @ApiError401
    @ApiError403
    @ApiError404
    @ApiError409
    @ApiError500
    ResponseEntity<SuccessResponseDTO> checkIn(
            @Parameter(description = "ID da atividade") @PathVariable UUID id,
            @Valid @RequestBody CheckInDTO dto);


    @Operation(
            summary = "Buscar participantes de uma atividade",
            description = "Endpoint para buscar os participantes de uma atividade específica.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Participantes retornados com sucesso",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ActivityParticipantDataDTO.class)
                            )
                    )
            }
    )
    @ApiError401
    @ApiError403
    @ApiError404
    @ApiError500
    ResponseEntity<List<ActivityParticipantDataDTO>> getActivityParticipants(
            @Parameter(description = "ID da atividade") @PathVariable UUID id);


    @Operation(
            summary = "Buscar atividades criadas pelo usuário (paginado)",
            description = "Endpoint para listar de forma paginada as atividades criadas pelo usuário logado.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Atividades do criador retornadas com sucesso",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = PaginatedActivitiesDTO.class)
                            )
                    )
            }
    )
    @ApiError401
    @ApiError403
    @ApiError500
    ResponseEntity<PaginatedActivitiesDTO> getActivitiesCreatedByLoggedUser(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize);


    @Operation(
            summary = "Buscar todas as atividades criadas pelo usuário",
            description = "Endpoint para listar todas as atividades criadas pelo usuário logado.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Todas as atividades do criador retornadas com sucesso",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ActivityResponseDTO.class)
                            )
                    )
            }
    )
    @ApiError401
    @ApiError403
    @ApiError500
    ResponseEntity<List<ActivityResponseDTO>> getAllActivitiesCreatedByLoggedUser();


    @Operation(
            summary = "Buscar atividades em que o usuário se inscreveu (paginado)",
            description = "Endpoint para listar de forma paginada as atividades em que o usuário logado se inscreveu.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Atividades do participante retornadas com sucesso",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = PaginatedActivitiesDTO.class)
                            )
                    )
            }
    )
    @ApiError401
    @ApiError403
    @ApiError500
    ResponseEntity<PaginatedActivitiesDTO> getActivitiesWhereLoggedUserIsSubscribed(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize);


    @Operation(
            summary = "Buscar todas as atividades em que o usuário se inscreveu",
            description = "Endpoint para listar todas as atividades em que o usuário logado se inscreveu.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Todas as atividades do participante retornadas com sucesso",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ActivityResponseDTO.class)
                            )
                    )
            }
    )
    @ApiError401
    @ApiError403
    @ApiError500
    ResponseEntity<List<ActivityResponseDTO>> getAllActivitiesWhereLoggedUserIsSubscribed();
}
