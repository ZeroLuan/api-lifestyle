package br.com.sysmap.backend.mapper;

import br.com.sysmap.backend.dto.auth.AuthDataDTO;
import br.com.sysmap.backend.dto.auth.CreateUserDTO;
import br.com.sysmap.backend.dto.common.AchievementDTO;
import br.com.sysmap.backend.dto.user.UserDataDTO;
import br.com.sysmap.backend.dto.user.UserPreferencesDTO;
import br.com.sysmap.backend.entity.Preferences;
import br.com.sysmap.backend.entity.UserAchievements;
import br.com.sysmap.backend.entity.Users;
import br.com.sysmap.backend.service.FileService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    protected FileService fileService;

    // Cadastro: Converte DTO de criação para a Entidade Users
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "xp", ignore = true)
    @Mapping(target = "level", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "preferences", ignore = true)
    @Mapping(target = "achievements", ignore = true)
    @Mapping(target = "activities", ignore = true)
    @Mapping(target = "participations", ignore = true)
    public abstract Users toEntity(CreateUserDTO dto);

    // Consulta: Converte Entidade Users para DTO de visualização de dados
    @Mapping(target = "achievements", source = "achievements")
    @Mapping(target = "avatar", expression = "java(entity.getAvatar() != null && !entity.getAvatar().isBlank() ? entity.getAvatar() : fileService.getDefaultAvatarUrl())")
    public abstract UserDataDTO toUserDataDTO(Users entity);

    // Autenticação: Converte Entidade Users para DTO de resposta de login
    @Mapping(target = "token", ignore = true)
    @Mapping(target = "achievements", source = "achievements")
    @Mapping(target = "avatar", expression = "java(entity.getAvatar() != null && !entity.getAvatar().isBlank() ? entity.getAvatar() : fileService.getDefaultAvatarUrl())")
    public abstract AuthDataDTO toAuthDataDTO(Users entity);

    // Mapeamento interno de Conquistas do Usuário -> DTO
    @Mapping(target = "id", source = "achievement.id")
    @Mapping(target = "name", source = "achievement.name")
    @Mapping(target = "criterion", source = "achievement.criterion")
    public abstract AchievementDTO toAchievementDTO(UserAchievements userAchievement);

    // Mapeamento de Preferências (Interesses)
    @Mapping(target = "typeId", source = "activityType.id")
    @Mapping(target = "typeName", source = "activityType.name")
    @Mapping(target = "typeDescription", source = "activityType.description")
    public abstract UserPreferencesDTO toUserPreferencesDTO(Preferences entity);

    public abstract List<UserPreferencesDTO> toUserPreferencesDTOList(List<Preferences> entities);
}
