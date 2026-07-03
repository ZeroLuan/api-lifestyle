package br.com.sysmap.backend.service;

import br.com.sysmap.backend.dto.activity.*;
import br.com.sysmap.backend.entity.*;
import br.com.sysmap.backend.enums.SubscriptionStatus;
import br.com.sysmap.backend.exception.BadRequestException;
import br.com.sysmap.backend.exception.ConflictException;
import br.com.sysmap.backend.exception.ForbiddenException;
import br.com.sysmap.backend.mapper.ActivityMapper;
import br.com.sysmap.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @Mock private ActivitiesRepository activitiesRepository;
    @Mock private ActivityParticipantsRepository participantsRepository;
    @Mock private ActivityTypesRepository activityTypesRepository;
    @Mock private ActivityAddressesRepository addressesRepository;
    @Mock private UserAchievementsRepository userAchievementsRepository;
    @Mock private AchievementsRepository achievementsRepository;
    @Mock private UsersRepository usersRepository;
    @Mock private ActivityMapper activityMapper;
    @Mock private FileService fileService;
    @Mock private UserService userService;

    @InjectMocks
    private ActivityService activityService;

    private Users loggedUser;
    private Activities activity;

    @BeforeEach
    void setUp() {
        loggedUser = new Users();
        setId(loggedUser, UUID.randomUUID());
        
        Users creator = new Users();
        setId(creator, UUID.randomUUID());
        
        activity = new Activities();
        setId(activity, UUID.randomUUID());
        activity.setCreator(creator);
        activity.setConfirmationCode("123456");
        activity.setIsPrivate(false);
        activity.setScheduledDate(LocalDateTime.now().minusHours(1)); // Passado por padrão
        activity.setAddress(new ActivityAddresses());

        lenient().when(userService.getLoggedUser()).thenReturn(loggedUser);
        
        // Stub padrão para evitar NPEs em chamadas ao mapper
        ActivityResponseDTO defaultDto = new ActivityResponseDTO(UUID.randomUUID(), "Title", "Desc", "Type", "Addr", "Img", false, LocalDateTime.now(), null, 0, null, null, "123456");
        lenient().when(activityMapper.toResponseDTO(any())).thenReturn(defaultDto);
    }

    private void setId(Object obj, UUID id) {
        try {
            java.lang.reflect.Field field = obj.getClass().getDeclaredField("id");
            if (field == null) field = obj.getClass().getSuperclass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(obj, id);
        } catch (Exception e) {
            try {
                java.lang.reflect.Field field = obj.getClass().getSuperclass().getDeclaredField("id");
                field.setAccessible(true);
                field.set(obj, id);
            } catch (Exception ignored) {}
        }
    }

    @Test
    @DisplayName("Deve criar atividade com sucesso e conceder conquista")
    void shouldCreateActivity() {
        CreateActivityDTO dto = new CreateActivityDTO("Title", "Desc", UUID.randomUUID(), "Address", mock(MultipartFile.class), "2026-05-10T10:00:00", false);
        ActivityTypes type = new ActivityTypes();
        
        when(activityTypesRepository.findById(any())).thenReturn(Optional.of(type));
        when(fileService.uploadImage(any(), anyString())).thenReturn("http://image.url");
        when(achievementsRepository.findByName("Primeira Atividade Criada")).thenReturn(Optional.of(new Achievements()));

        activityService.createActivity(dto);

        verify(activitiesRepository).save(any(Activities.class));
        verify(userService).grantAchievementIfAbsent(any(), eq("Primeira Atividade Criada"));
    }

    @Test
    @DisplayName("Deve falhar ao inscrever o próprio criador")
    void shouldFailWhenCreatorSubscribes() {
        activity.setCreator(loggedUser);
        when(activitiesRepository.findActiveById(activity.getId())).thenReturn(Optional.of(activity));

        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> activityService.subscribeToActivity(activity.getId()));
        assertEquals("O criador da atividade não pode se inscrever como um participante.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve inscrever usuário com sucesso (Pública -> APPROVED)")
    void shouldSubscribeSuccessfully() {
        when(activitiesRepository.findActiveById(activity.getId())).thenReturn(Optional.of(activity));
        when(participantsRepository.existsByActivityIdAndUserId(any(), any())).thenReturn(false);

        SubscribeToActivityResponseDTO response = activityService.subscribeToActivity(activity.getId());

        assertEquals(SubscriptionStatus.APPROVED, response.subscriptionStatus());
        verify(participantsRepository).save(any());
    }

    @Test
    @DisplayName("Deve falhar check-in com código incorreto")
    void shouldFailCheckInWithWrongCode() {
        CheckInDTO dto = new CheckInDTO("654321");
        when(activitiesRepository.findActiveById(activity.getId())).thenReturn(Optional.of(activity));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> activityService.checkIn(activity.getId(), dto));
        assertEquals("Código de confirmação incorreto.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve realizar check-in com sucesso, dar XP e conquista")
    void shouldCheckInSuccessfully() {
        CheckInDTO dto = new CheckInDTO("123456");
        ActivityParticipants participant = new ActivityParticipants();
        participant.setSubscriptionStatus(SubscriptionStatus.APPROVED);
        participant.setUser(loggedUser);
        
        // Atividade no passado (permitido check-in)
        activity.setScheduledDate(LocalDateTime.now().minusHours(1));

        when(activitiesRepository.findActiveById(activity.getId())).thenReturn(Optional.of(activity));
        when(participantsRepository.findByActivityIdAndUserId(any(), any())).thenReturn(Optional.of(participant));
        when(usersRepository.findById(loggedUser.getId())).thenReturn(Optional.of(loggedUser));
        when(usersRepository.findById(activity.getCreator().getId())).thenReturn(Optional.of(activity.getCreator()));
        when(achievementsRepository.findByName("Primeiro Check-in")).thenReturn(Optional.of(new Achievements()));

        activityService.checkIn(activity.getId(), dto);

        assertNotNull(participant.getConfirmedAt());
        assertTrue(loggedUser.getXp() > 0);
        assertTrue(activity.getCreator().getXp() > 0);
        verify(userService).grantAchievementIfAbsent(any(), eq("Primeiro Check-in"));
    }

    @Test
    @DisplayName("Deve falhar check-in antes da data de início")
    void shouldFailCheckInBeforeStartDate() {
        CheckInDTO dto = new CheckInDTO("123456");
        activity.setScheduledDate(LocalDateTime.now().plusDays(1)); // Futuro

        when(activitiesRepository.findActiveById(activity.getId())).thenReturn(Optional.of(activity));

        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> activityService.checkIn(activity.getId(), dto));
        assertEquals("Não é possível realizar o check-in antes do início da atividade.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve bloquear desinscrição após check-in (E18)")
    void shouldBlockUnsubscribeAfterCheckIn() {
        ActivityParticipants participant = new ActivityParticipants();
        participant.setConfirmedAt(LocalDateTime.now());

        when(activitiesRepository.findActiveById(activity.getId())).thenReturn(Optional.of(activity));
        when(participantsRepository.findByActivityIdAndUserId(any(), any())).thenReturn(Optional.of(participant));

        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> activityService.unsubscribeFromActivity(activity.getId()));
        assertEquals("Não é possível cancelar sua inscrição, pois sua presença já foi confirmada.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve concluir atividade com sucesso e dar conquista ao criador")
    void shouldConcludeActivity() {
        activity.setCreator(loggedUser);
        when(activitiesRepository.findActiveById(activity.getId())).thenReturn(Optional.of(activity));
        when(usersRepository.findById(loggedUser.getId())).thenReturn(Optional.of(loggedUser));
        when(achievementsRepository.findByName("Primeira Atividade Concluída")).thenReturn(Optional.of(new Achievements()));

        activityService.concludeActivity(activity.getId());

        assertNotNull(activity.getCompletedAt());
        verify(userService).grantAchievementIfAbsent(any(), eq("Primeira Atividade Concluída"));
    }

    @Test
    @DisplayName("Deve inscrever usuário como PENDING em atividade privada (E10)")
    void shouldSubscribeAsPendingToPrivateActivity() {
        activity.setIsPrivate(true);
        when(activitiesRepository.findActiveById(activity.getId())).thenReturn(Optional.of(activity));
        when(participantsRepository.existsByActivityIdAndUserId(any(), any())).thenReturn(false);

        SubscribeToActivityResponseDTO response = activityService.subscribeToActivity(activity.getId());

        assertEquals(SubscriptionStatus.PENDING, response.subscriptionStatus());
        verify(participantsRepository).save(any());
    }

    @Test
    @DisplayName("Deve falhar ao inscrever se já estiver inscrito")
    void shouldFailWhenAlreadySubscribed() {
        when(activitiesRepository.findActiveById(activity.getId())).thenReturn(Optional.of(activity));
        when(participantsRepository.existsByActivityIdAndUserId(any(), any())).thenReturn(true);

        ConflictException ex = assertThrows(ConflictException.class, () -> activityService.subscribeToActivity(activity.getId()));
        assertEquals("Você já se registrou nesta atividade.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar check-in duplo (E12)")
    void shouldFailDoubleCheckIn() {
        CheckInDTO dto = new CheckInDTO("123456");
        ActivityParticipants participant = new ActivityParticipants();
        participant.setSubscriptionStatus(SubscriptionStatus.APPROVED);
        participant.setConfirmedAt(LocalDateTime.now()); // Já fez check-in

        when(activitiesRepository.findActiveById(activity.getId())).thenReturn(Optional.of(activity));
        when(participantsRepository.findByActivityIdAndUserId(any(), any())).thenReturn(Optional.of(participant));

        ConflictException ex = assertThrows(ConflictException.class, () -> activityService.checkIn(activity.getId(), dto));
        assertEquals("Você já confirmou sua participação nesta atividade.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar check-in se usuário não estiver APROVADO")
    void shouldFailCheckInWhenNotApproved() {
        CheckInDTO dto = new CheckInDTO("123456");
        ActivityParticipants participant = new ActivityParticipants();
        participant.setSubscriptionStatus(SubscriptionStatus.PENDING); // Não aprovado

        when(activitiesRepository.findActiveById(activity.getId())).thenReturn(Optional.of(activity));
        when(participantsRepository.findByActivityIdAndUserId(any(), any())).thenReturn(Optional.of(participant));

        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> activityService.checkIn(activity.getId(), dto));
        assertEquals("Apenas participantes aprovados na atividade podem fazer check-in.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve aprovar participante com sucesso")
    void shouldApproveParticipant() {
        activity.setCreator(loggedUser); // Logado é o criador
        activity.setIsPrivate(true);
        ActivityParticipants participant = new ActivityParticipants();
        participant.setSubscriptionStatus(SubscriptionStatus.PENDING);

        when(activitiesRepository.findActiveById(activity.getId())).thenReturn(Optional.of(activity));
        when(participantsRepository.findByActivityIdAndUserId(any(), any())).thenReturn(Optional.of(participant));

        activityService.approveParticipant(activity.getId(), new ParticipantApprovalDTO(UUID.randomUUID(), true));

        assertEquals(SubscriptionStatus.APPROVED, participant.getSubscriptionStatus());
        verify(participantsRepository).save(participant);
    }

    @Test
    @DisplayName("Deve falhar aprovação se não for o criador")
    void shouldFailApprovalIfNotCreator() {
        activity.setIsPrivate(true); // Logado NÃO é o criador (configurado no setUp)

        when(activitiesRepository.findActiveById(activity.getId())).thenReturn(Optional.of(activity));

        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> activityService.approveParticipant(activity.getId(), new ParticipantApprovalDTO(UUID.randomUUID(), true)));
        assertEquals("Apenas o criador da atividade pode aprovar ou negar participantes.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar atualização se não for o criador (E17)")
    void shouldFailUpdateIfNotCreator() {
        UpdateActivityDTO dto = new UpdateActivityDTO("New", "Desc", UUID.randomUUID(), "Address", mock(MultipartFile.class), "2026-05-10T10:00:00", false);
        when(activitiesRepository.findActiveById(activity.getId())).thenReturn(Optional.of(activity));

        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> activityService.updateActivity(activity.getId(), dto));
        assertEquals("Apenas o criador da atividade pode editá-la.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar exclusão se não for o criador")
    void shouldFailDeleteIfNotCreator() {
        when(activitiesRepository.findActiveById(activity.getId())).thenReturn(Optional.of(activity));

        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> activityService.deleteActivity(activity.getId()));
        assertEquals("Apenas o criador da atividade pode exclui-la.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve ocultar código de confirmação se a atividade for no futuro")
    void shouldHideCodeForFutureActivity() {
        activity.setCreator(loggedUser);
        activity.setScheduledDate(LocalDateTime.now().plusDays(1));
        
        ActivityResponseDTO mappedDto = new ActivityResponseDTO(activity.getId(), "Title", "Desc", "Type", "Addr", "Img", false, activity.getScheduledDate(), null, 0, null, null, "123456");
        // Usar any() pois a entidade é criada dentro do createActivity
        when(activityMapper.toResponseDTO(any(Activities.class))).thenReturn(mappedDto);
        when(activityTypesRepository.findById(any())).thenReturn(Optional.of(new ActivityTypes()));
        when(fileService.uploadImage(any(), anyString())).thenReturn("url");

        CreateActivityDTO createDto = new CreateActivityDTO("Title", "Desc", UUID.randomUUID(), "Address", mock(MultipartFile.class), "2026-05-10T10:00:00", false);
        ActivityResponseDTO response = activityService.createActivity(createDto);
        
        assertNull(response.confirmationCode());
    }
}
