package br.com.sysmap.backend.service;

import br.com.sysmap.backend.dto.common.SuccessResponseDTO;
import br.com.sysmap.backend.dto.user.AvatarUrlDTO;
import br.com.sysmap.backend.dto.user.UpdateUserDTO;
import br.com.sysmap.backend.dto.user.UserDataDTO;
import br.com.sysmap.backend.dto.user.UserPreferencesDTO;
import br.com.sysmap.backend.entity.ActivityTypes;
import br.com.sysmap.backend.entity.Preferences;
import br.com.sysmap.backend.entity.UserAchievements;
import br.com.sysmap.backend.entity.Users;
import br.com.sysmap.backend.exception.BadRequestException;
import br.com.sysmap.backend.exception.ConflictException;
import br.com.sysmap.backend.exception.EntityNotFoundException;
import br.com.sysmap.backend.mapper.UserMapper;
import br.com.sysmap.backend.repository.*;
import br.com.sysmap.backend.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UsersRepository usersRepository;
    private final PreferencesRepository preferencesRepository;
    private final ActivityTypesRepository activityTypesRepository;
    private final UserAchievementsRepository userAchievementsRepository;
    private final AchievementsRepository achievementsRepository;
    private final UserMapper userMapper;
    private final FileService fileService;
    private final PasswordEncoder passwordEncoder;


    /**
     * Retorna o usuário autenticado no contexto da requisição.
     */
    public Users getLoggedUser() {
        AuthenticatedUser principal = (AuthenticatedUser) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        return principal.getUser();
    }

    private Users findActiveUserById(UUID id) {
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        if (user.getDeletedAt() != null) {
            throw new BadRequestException("Esta conta foi desativada e não pode ser utilizada.");
        }

        return user;
    }

    /**
     * Concede uma conquista ao usuário caso ele ainda não a possua.
     */
    @Transactional
    public void grantAchievementIfAbsent(Users user, String achievementName) {
        achievementsRepository.findByName(achievementName).ifPresent(achievement -> {
            boolean alreadyHas = userAchievementsRepository
                    .existsByUserIdAndAchievementId(user.getId(), achievement.getId());
            if (!alreadyHas) {
                UserAchievements ua = new UserAchievements();
                ua.setUser(user);
                ua.setAchievement(achievement);
                userAchievementsRepository.save(ua);
                log.info("Conquista '{}' concedida ao usuário {}", achievementName, user.getId());
            }
        });
    }


    /**
     * GET /user — Retorna dados do usuário logado.
     */
    @Transactional(readOnly = true)
    public UserDataDTO getLoggedUserData() {
        Users user = findActiveUserById(getLoggedUser().getId());
        return userMapper.toUserDataDTO(user);
    }

    /**
     * PUT /user/update — Atualiza nome, email e/ou senha.
     * 409 se e-mail já pertencer a outro usuário.
     */
    @Transactional
    public UserDataDTO updateUser(UpdateUserDTO dto) {
        Users user = findActiveUserById(getLoggedUser().getId());

        if (dto.name() != null && !dto.name().isBlank()) {
            user.setName(dto.name());
        }

        if (dto.email() != null && !dto.email().isBlank() && !dto.email().equals(user.getEmail())) {
            if (usersRepository.existsByEmail(dto.email())) {
                throw new ConflictException("O e-mail ou CPF informado já pertence a outro usuário.");
            }
            user.setEmail(dto.email());
        }

        if (dto.password() != null && !dto.password().isBlank()) {
            if (dto.password().length() < 6) {
                throw new BadRequestException("A senha deve ter no mínimo 6 caracteres.");
            }
            user.setPassword(passwordEncoder.encode(dto.password()));
        }

        usersRepository.save(user);
        return userMapper.toUserDataDTO(user);
    }

    /**
     * PUT /user/avatar — Atualiza o avatar do usuário.
     * Regra E2: Somente arquivos JPG/PNG são aceitos.
     */
    @Transactional
    public AvatarUrlDTO updateAvatar(MultipartFile file) {
        Users user = findActiveUserById(getLoggedUser().getId());

        String avatarUrl = fileService.uploadImage(file, "avatars");
        user.setAvatar(avatarUrl);
        usersRepository.save(user);

        grantAchievementIfAbsent(user, "Avatar Atualizado");

        return new AvatarUrlDTO(avatarUrl);
    }

    /**
     * POST /user/preferences/define — Define as preferências (interesses) do usuário.
     * Substitui todas as preferências existentes pelos novos IDs informados.
     */
    @Transactional
    public SuccessResponseDTO definePreferences(List<UUID> typeIds) {
        Users user = findActiveUserById(getLoggedUser().getId());

        preferencesRepository.deleteAllByUserId(user.getId());

        List<Preferences> newPreferences = typeIds.stream()
                .map(typeId -> {
                    ActivityTypes activityType = activityTypesRepository.findById(typeId)
                            .orElseThrow(() -> new EntityNotFoundException("Tipo de atividade não encontrado: " + typeId));
                    Preferences pref = new Preferences();
                    pref.setUser(user);
                    pref.setActivityType(activityType);
                    return pref;
                })
                .toList();

        preferencesRepository.saveAll(newPreferences);
        log.info("Preferências atualizadas para o usuário: {}", user.getId());

        return new SuccessResponseDTO("Preferências definidas com sucesso.");
    }

    /**
     * GET /user/preferences — Retorna as preferências do usuário logado.
     */
    @Transactional(readOnly = true)
    public List<UserPreferencesDTO> getPreferences() {
        Users user = findActiveUserById(getLoggedUser().getId());
        List<Preferences> prefs = preferencesRepository.findAllByUserId(user.getId());
        return userMapper.toUserPreferencesDTOList(prefs);
    }

    /**
     * DELETE /user/deactivate — Realiza soft delete da conta do usuário.
     */
    @Transactional
    public SuccessResponseDTO deactivateUser() {
        Users user = findActiveUserById(getLoggedUser().getId());
        user.softDelete();
        usersRepository.save(user);
        log.info("Conta desativada para o usuário: {}", user.getId());
        return new SuccessResponseDTO("Conta desativada com sucesso.");
    }
}
