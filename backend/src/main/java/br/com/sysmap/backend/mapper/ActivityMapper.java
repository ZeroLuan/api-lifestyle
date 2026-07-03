package br.com.sysmap.backend.mapper;

import br.com.sysmap.backend.dto.activity.*;
import br.com.sysmap.backend.dto.common.ActivityTypeDTO;
import br.com.sysmap.backend.entity.Activities;
import br.com.sysmap.backend.entity.ActivityParticipants;
import br.com.sysmap.backend.entity.ActivityTypes;
import br.com.sysmap.backend.util.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {DateUtils.class})
public interface ActivityMapper {

    // Entidade -> Response DTO (Visualização)
    @Mapping(target = "type", source = "type.name")
    @Mapping(target = "address", source = "address.address")
    @Mapping(target = "participantCount", expression = "java(entity.getParticipants() != null ? entity.getParticipants().size() : 0)")
    @Mapping(target = "userSubscriptionStatus", ignore = true) // Definido no Service baseado no usuário logado
    ActivityResponseDTO toResponseDTO(Activities entity);

    List<ActivityResponseDTO> toResponseDTOList(List<Activities> entities);

    // Create DTO -> Entidade (Criação)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true) // Tratado pelo serviço de armazenamento (S3/LocalStack)
    @Mapping(target = "address", ignore = true) // Tratado no Service (criação de ActivityAddresses)
    @Mapping(target = "type", ignore = true) // Tratado no Service (busca por UUID)
    @Mapping(target = "creator", ignore = true) // Tratado no Service (contexto de autenticação)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "confirmationCode", ignore = true)
    @Mapping(target = "participants", ignore = true)
    Activities toEntity(CreateActivityDTO dto);

    // Update DTO -> Entidade (Atualização)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "confirmationCode", ignore = true)
    @Mapping(target = "participants", ignore = true)
    Activities toEntity(UpdateActivityDTO dto);

    // Mapeamento de Participantes (Visualização da lista)
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "name", source = "user.name")
    @Mapping(target = "avatar", source = "user.avatar")
    @Mapping(target = "subscriptionStatus", source = "subscriptionStatus")
    ActivityParticipantDataDTO toParticipantDTO(ActivityParticipants entity);

    List<ActivityParticipantDataDTO> toParticipantDTOList(List<ActivityParticipants> entities);

    // Mapeamento de Tipos de Atividade
    ActivityTypeDTO toTypeDTO(ActivityTypes entity);

    List<ActivityTypeDTO> toTypeDTOList(List<ActivityTypes> entities);

    // Mapeamento de Resposta de Inscrição
    @Mapping(target = "activityId", source = "activity.id")
    @Mapping(target = "userId", source = "user.id")
    SubscribeToActivityResponseDTO toSubscribeResponseDTO(ActivityParticipants entity);
}

