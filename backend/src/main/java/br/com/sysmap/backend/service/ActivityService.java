package br.com.sysmap.backend.service;

import br.com.sysmap.backend.dto.activity.*;
import br.com.sysmap.backend.dto.common.ActivityTypeDTO;
import br.com.sysmap.backend.dto.common.SuccessResponseDTO;
import br.com.sysmap.backend.entity.*;
import br.com.sysmap.backend.enums.SubscriptionStatus;
import br.com.sysmap.backend.exception.BadRequestException;
import br.com.sysmap.backend.exception.ConflictException;
import br.com.sysmap.backend.exception.EntityNotFoundException;
import br.com.sysmap.backend.exception.ForbiddenException;
import br.com.sysmap.backend.mapper.ActivityMapper;
import br.com.sysmap.backend.repository.*;
import br.com.sysmap.backend.util.RandomUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ActivityService {

    private static final int XP_PER_CHECKIN = 50;

    private final ActivitiesRepository activitiesRepository;
    private final ActivityParticipantsRepository participantsRepository;
    private final ActivityTypesRepository activityTypesRepository;
    private final ActivityAddressesRepository addressesRepository;
    private final UsersRepository usersRepository;
    private final ActivityMapper activityMapper;
    private final FileService fileService;
    private final UserService userService;



    private Activities findActiveActivity(UUID id) {
        return activitiesRepository.findActiveById(id)
                .orElseThrow(() -> new EntityNotFoundException("Atividade não encontrada."));
    }

    private void assertIsCreator(Activities activity, UUID userId, String message) {
        if (!activity.getCreator().getId().equals(userId)) {
            throw new ForbiddenException(message);
        }
    }

    private void assertIsNotConcluded(Activities activity, boolean isSubscription) {
        if (activity.getCompletedAt() != null) {
            if (isSubscription) {
                // E12
                throw new ForbiddenException("Não é possível se inscrever em uma atividade concluída.");
            } else {
                // E13
                throw new ForbiddenException("Não é possível confirmar presença em uma atividade concluída.");
            }
        }
    }

    private Sort buildSort(String orderBy, String order) {
        String field = switch (orderBy) {
            case "scheduledDate" -> "scheduledDate";
            case "title" -> "title";
            default -> "createdAt";
        };
        return "asc".equalsIgnoreCase(order)
                ? Sort.by(field).ascending()
                : Sort.by(field).descending();
    }

    private LocalDateTime parseDate(String date) {
        try {
            return LocalDateTime.parse(date);
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Informe os campos obrigatórios corretamente.");
        }
    }






    private void grantXpAndSave(Users user, int amount) {
        user.addXp(amount);
        usersRepository.save(user);
    }



    private ActivityResponseDTO toResponseDTO(Activities activity, Users loggedUser) {
        ActivityResponseDTO dto = activityMapper.toResponseDTO(activity);
        
        // Regra: Apenas o criador pode visualizar o código de confirmação
        // E apenas se a data da atividade já tiver chegado/passado
        boolean isCreator = loggedUser != null && activity.getCreator().getId().equals(loggedUser.getId());
        boolean hasStarted = !LocalDateTime.now().isBefore(activity.getScheduledDate());

        if (!isCreator || !hasStarted) {
            return new ActivityResponseDTO(
                    dto.id(), dto.title(), dto.description(), dto.type(), dto.address(),
                    dto.image(), dto.isPrivate(), dto.scheduledDate(), dto.completedAt(),
                    dto.participantCount(), dto.creator(), dto.userSubscriptionStatus(),
                    null // Oculta o código
            );
        }
        return dto;
    }



    /**
     * POST /activities/new — Cria uma nova atividade.
     * Conquista: "Primeira Atividade Criada" concedida ao criar pela primeira vez.
     */
    @Transactional
    public ActivityResponseDTO createActivity(CreateActivityDTO dto) {
        Users creator = userService.getLoggedUser();

        ActivityTypes type = activityTypesRepository.findById(dto.typeId())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de atividade não encontrado."));

        String imageUrl = fileService.uploadImage(dto.image(), "activities");

        ActivityAddresses address = new ActivityAddresses();
        address.setAddress(dto.address());
        addressesRepository.save(address);

        String confirmationCode = RandomUtils.generateAlphanumericCode(6);

        Activities activity = new Activities();
        activity.setTitle(dto.title());
        activity.setDescription(dto.description());
        activity.setType(type);
        activity.setAddress(address);
        activity.setImage(imageUrl);
        activity.setIsPrivate(dto.isPrivate());
        activity.setScheduledDate(parseDate(dto.scheduledDate()));
        activity.setConfirmationCode(confirmationCode);
        activity.setCreator(creator);

        activitiesRepository.save(activity);

        userService.grantAchievementIfAbsent(creator, "Primeira Atividade Criada");

        log.info("Atividade criada: {} por {}", activity.getId(), creator.getId());
        return toResponseDTO(activity, creator);
    }

    /**
     * GET /activities — Listagem paginada de atividades.
     * Se typeId for nulo, prioriza atividades que batem com os interesses do usuário.
     */
    @Transactional(readOnly = true)
    public PaginatedActivitiesDTO getActivities(int page, int pageSize, UUID typeId, String orderBy, String order) {
        Users user = userService.getLoggedUser();
        PageRequest pageable = PageRequest.of(page - 1, pageSize, buildSort(orderBy, order));
        
        Page<Activities> result = activitiesRepository.findAllActiveWithPriority(typeId, user.getId(), pageable);

        List<ActivityResponseDTO> content = result.getContent().stream()
                .map(a -> toResponseDTO(a, user))
                .toList();

        return new PaginatedActivitiesDTO(content, page, pageSize, result.getTotalElements(), result.getTotalPages());
    }

    /**
     * GET /activities/all — Lista todas as atividades sem paginação.
     */
    @Transactional(readOnly = true)
    public List<ActivityResponseDTO> getAllActivities(UUID typeId, String orderBy, String order) {
        Users user = userService.getLoggedUser();
        return activitiesRepository.findAllActiveList(typeId).stream()
                .map(a -> toResponseDTO(a, user))
                .toList();
    }

    /**
     * GET /activities/types — Retorna os tipos de atividade.
     */
    @Transactional(readOnly = true)
    public List<ActivityTypeDTO> getTypes() {
        return activityTypesRepository.findAll().stream()
                .map(t -> new ActivityTypeDTO(t.getId(), t.getName(), t.getDescription()))
                .toList();
    }

    /**
     * PUT /activities/{id}/update — Atualiza atividade.
     * E14: Apenas o criador pode editar.
     */
    @Transactional
    public ActivityResponseDTO updateActivity(UUID id, UpdateActivityDTO dto) {
        Users user = userService.getLoggedUser();
        Activities activity = findActiveActivity(id);

        assertIsCreator(activity, user.getId(), "Apenas o criador da atividade pode editá-la.");

        assertIsNotConcluded(activity, false);

        activity.setTitle(dto.title());
        activity.setDescription(dto.description());
        activity.setScheduledDate(parseDate(dto.scheduledDate()));
        activity.setIsPrivate(dto.isPrivate());

        ActivityTypes type = activityTypesRepository.findById(dto.typeId())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de atividade não encontrado."));
        activity.setType(type);

        activity.getAddress().setAddress(dto.address());

        if (dto.image() != null && !dto.image().isEmpty()) {
            String imageUrl = fileService.uploadImage(dto.image(), "activities");
            activity.setImage(imageUrl);
        }

        activitiesRepository.save(activity);
        return toResponseDTO(activity, user);
    }

    /**
     * DELETE /activities/{id}/delete — Soft delete de atividade.
     * E15: Apenas o criador pode excluir.
     */
    @Transactional
    public SuccessResponseDTO deleteActivity(UUID id) {
        Users user = userService.getLoggedUser();
        Activities activity = findActiveActivity(id);

        assertIsCreator(activity, user.getId(), "Apenas o criador da atividade pode exclui-la.");

        activity.softDelete();
        activitiesRepository.save(activity);

        log.info("Atividade {} deletada por {}", id, user.getId());
        return new SuccessResponseDTO("Atividade removida com sucesso.");
    }

    /**
     * PUT /activities/{id}/conclude — Conclui a atividade.
     * E17: Apenas o criador pode concluir.
     * Conquista: "Primeira Atividade Concluída".
     */
    @Transactional
    public SuccessResponseDTO concludeActivity(UUID id) {
        Users user = userService.getLoggedUser();
        Activities activity = findActiveActivity(id);

        assertIsCreator(activity, user.getId(), "Apenas o criador da atividade pode concluí-la.");

        if (activity.getCompletedAt() != null) {
            throw new ForbiddenException("Esta atividade já foi concluída.");
        }

        activity.setCompletedAt(LocalDateTime.now());
        activitiesRepository.save(activity);

        Users creator = usersRepository.findById(user.getId()).orElse(null);
        if (creator != null) {
            userService.grantAchievementIfAbsent(creator, "Primeira Atividade Concluída");
        }

        return new SuccessResponseDTO("Atividade concluída com sucesso.");
    }

    /**
     * POST /activities/{id}/subscribe — Inscreve o usuário logado.
     * E8: Criador não pode se inscrever.
     * E7: Não pode se inscrever duas vezes.
     * E12: Não pode se inscrever em atividade concluída.
     */
    @Transactional
    public SubscribeToActivityResponseDTO subscribeToActivity(UUID id) {
        Users user = userService.getLoggedUser();
        Activities activity = findActiveActivity(id);

        // E12
        assertIsNotConcluded(activity, true);

        // E8
        if (activity.getCreator().getId().equals(user.getId())) {
            throw new ForbiddenException("O criador da atividade não pode se inscrever como um participante.");
        }

        // E7
        if (participantsRepository.existsByActivityIdAndUserId(id, user.getId())) {
            throw new ConflictException("Você já se registrou nesta atividade.");
        }

        SubscriptionStatus status = activity.getIsPrivate()
                ? SubscriptionStatus.PENDING
                : SubscriptionStatus.APPROVED;

        ActivityParticipants participant = new ActivityParticipants();
        participant.setSubscriptionStatus(status);
        participant.setUser(user);
        participant.setActivity(activity);
        participantsRepository.save(participant);

        return new SubscribeToActivityResponseDTO(
                participant.getId(),
                participant.getSubscriptionStatus(),
                participant.getConfirmedAt(),
                activity.getId(),
                user.getId()
        );
    }

    /**
     * DELETE /activities/{id}/unsubscribe — Cancela inscrição.
     * E18: Não pode cancelar após confirmar presença.
     */
    @Transactional
    public SuccessResponseDTO unsubscribeFromActivity(UUID id) {
        Users user = userService.getLoggedUser();
        findActiveActivity(id);

        ActivityParticipants participant = participantsRepository
                .findByActivityIdAndUserId(id, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Você não está inscrito nesta atividade."));

        if (participant.getConfirmedAt() != null) {
            throw new ForbiddenException("Não é possível cancelar sua inscrição, pois sua presença já foi confirmada.");
        }

        participantsRepository.delete(participant);
        return new SuccessResponseDTO("Inscrição cancelada com sucesso.");
    }

    /**
     * PUT /activities/{id}/approve — Aprova ou rejeita participante.
     * E16: Apenas o criador pode aprovar/negar.
     */
    @Transactional
    public SuccessResponseDTO approveParticipant(UUID activityId, ParticipantApprovalDTO dto) {
        Users user = userService.getLoggedUser();
        Activities activity = findActiveActivity(activityId);

        assertIsCreator(activity, user.getId(), "Apenas o criador da atividade pode aprovar ou negar participantes.");

        ActivityParticipants participant = participantsRepository
                .findByActivityIdAndUserId(activityId, dto.userId())
                .orElseThrow(() -> new EntityNotFoundException("Participante não encontrado nesta atividade."));

        participant.setSubscriptionStatus(
                dto.approved() ? SubscriptionStatus.APPROVED : SubscriptionStatus.REJECTED
        );
        participantsRepository.save(participant);

        String msg = dto.approved()
                ? "Participante aprovado com sucesso."
                : "Participante rejeitado com sucesso.";
        return new SuccessResponseDTO(msg);
    }

    /**
     * PUT /activities/{id}/check-in — Realiza check-in com código.
     * E9: Apenas participantes aprovados podem fazer check-in.
     * E10: Código deve ser correto.
     * E11: Não pode fazer check-in duas vezes.
     * E13: Não pode fazer check-in em atividade concluída.
     *
     * XP: Participante E criador recebem XP ao fazer check-in.
     * Conquista: "Primeiro Check-in" ao fazer pela primeira vez.
     */
    @Transactional
    public SuccessResponseDTO checkIn(UUID id, CheckInDTO dto) {
        Users user = userService.getLoggedUser();
        Activities activity = findActiveActivity(id);

        // E13
        assertIsNotConcluded(activity, false);

        // Regra: Não pode fazer check-in antes da atividade começar
        if (LocalDateTime.now().isBefore(activity.getScheduledDate())) {
            throw new ForbiddenException("Não é possível realizar o check-in antes do início da atividade.");
        }

        // E10
        if (!activity.getConfirmationCode().equals(dto.confirmationCode())) {
            throw new BadRequestException("Código de confirmação incorreto.");
        }

        ActivityParticipants participant = participantsRepository
                .findByActivityIdAndUserId(id, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Você não está inscrito nesta atividade."));

        // E9
        if (!SubscriptionStatus.APPROVED.equals(participant.getSubscriptionStatus())) {
            throw new ForbiddenException("Apenas participantes aprovados na atividade podem fazer check-in.");
        }

        // E11
        if (participant.getConfirmedAt() != null) {
            throw new ConflictException("Você já confirmou sua participação nesta atividade.");
        }

        participant.setConfirmedAt(LocalDateTime.now());
        participantsRepository.save(participant);

        Users participantUser = usersRepository.findById(user.getId()).orElse(null);
        if (participantUser != null) {
            grantXpAndSave(participantUser, XP_PER_CHECKIN);

            userService.grantAchievementIfAbsent(participantUser, "Primeiro Check-in");
        }

        Users creator = usersRepository.findById(activity.getCreator().getId()).orElse(null);
        if (creator != null && !creator.getId().equals(user.getId())) {
            grantXpAndSave(creator, XP_PER_CHECKIN);
            log.info("XP concedido ao criador {} pelo check-in na atividade {}", creator.getId(), id);
        }

        return new SuccessResponseDTO("Check-in realizado com sucesso.");
    }

    /**
     * GET /activities/{id}/participants — Lista participantes.
     * Somente o criador pode ver.
     */
    @Transactional(readOnly = true)
    public List<ActivityParticipantDataDTO> getParticipants(UUID id) {
        Users user = userService.getLoggedUser();
        Activities activity = findActiveActivity(id);

        assertIsCreator(activity, user.getId(), "Apenas o criador da atividade pode visualizar os participantes.");

        return participantsRepository.findByActivityId(id).stream()
                .map(p -> new ActivityParticipantDataDTO(
                        p.getId(),
                        p.getUser().getId(),
                        p.getUser().getName(),
                        p.getUser().getAvatar(),
                        p.getSubscriptionStatus(),
                        p.getConfirmedAt()
                ))
                .toList();
    }

    /**
     * GET /activities/user/creator — Atividades criadas pelo usuário (paginado).
     */
    @Transactional(readOnly = true)
    public PaginatedActivitiesDTO getActivitiesCreatedByLoggedUser(int page, int pageSize) {
        Users user = userService.getLoggedUser();
        PageRequest pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());
        Page<Activities> result = activitiesRepository.findByCreatorId(user.getId(), pageable);

        List<ActivityResponseDTO> content = result.getContent().stream()
                .map(a -> toResponseDTO(a, user)).toList();

        return new PaginatedActivitiesDTO(content, page, pageSize, result.getTotalElements(), result.getTotalPages());
    }

    /**
     * GET /activities/user/creator/all — Todas as atividades criadas.
     */
    @Transactional(readOnly = true)
    public List<ActivityResponseDTO> getAllActivitiesCreatedByLoggedUser() {
        Users user = userService.getLoggedUser();
        return activitiesRepository.findAllByCreatorId(user.getId()).stream()
                .map(a -> toResponseDTO(a, user)).toList();
    }

    /**
     * GET /activities/user/participant — Atividades em que o usuário está inscrito (paginado).
     */
    @Transactional(readOnly = true)
    public PaginatedActivitiesDTO getActivitiesWhereUserIsSubscribed(int page, int pageSize) {
        Users user = userService.getLoggedUser();
        PageRequest pageable = PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());
        Page<Activities> result = activitiesRepository.findByParticipantId(user.getId(), pageable);

        List<ActivityResponseDTO> content = result.getContent().stream()
                .map(a -> toResponseDTO(a, user)).toList();

        return new PaginatedActivitiesDTO(content, page, pageSize, result.getTotalElements(), result.getTotalPages());
    }

    /**
     * GET /activities/user/participant/all — Todas as atividades inscritas.
     */
    @Transactional(readOnly = true)
    public List<ActivityResponseDTO> getAllActivitiesWhereUserIsSubscribed() {
        Users user = userService.getLoggedUser();
        return activitiesRepository.findAllByParticipantId(user.getId()).stream()
                .map(a -> toResponseDTO(a, user)).toList();
    }


}
