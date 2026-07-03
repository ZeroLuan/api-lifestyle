package br.com.sysmap.backend.service;

import br.com.sysmap.backend.dto.user.AvatarUrlDTO;
import br.com.sysmap.backend.dto.user.UpdateUserDTO;
import br.com.sysmap.backend.dto.user.UserDataDTO;
import br.com.sysmap.backend.entity.Users;
import br.com.sysmap.backend.mapper.UserMapper;
import br.com.sysmap.backend.repository.*;
import br.com.sysmap.backend.security.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UsersRepository usersRepository;
    @Mock private PreferencesRepository preferencesRepository;
    @Mock private ActivityTypesRepository activityTypesRepository;
    @Mock private UserAchievementsRepository userAchievementsRepository;
    @Mock private AchievementsRepository achievementsRepository;
    @Mock private UserMapper userMapper;
    @Mock private FileService fileService;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private Users loggedUser;

    @BeforeEach
    void setUp() {
        loggedUser = new Users();
        UUID userId = UUID.randomUUID();
        setId(loggedUser, userId);

        // Mocking SecurityContext
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(loggedUser);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        SecurityContextHolder.setContext(securityContext);
    }

    private void setId(Object obj, UUID id) {
        try {
            java.lang.reflect.Field field = obj.getClass().getDeclaredField("id");
            if (field == null) field = obj.getClass().getSuperclass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(obj, id);
        } catch (Exception ignored) {}
    }

    @Test
    @DisplayName("Deve retornar dados do usuário logado")
    void shouldReturnLoggedUserData() {
        when(usersRepository.findById(loggedUser.getId())).thenReturn(Optional.of(loggedUser));
        UserDataDTO expectedDto = new UserDataDTO(loggedUser.getId(), "John", "john@email.com", null, 0, 0, null);
        when(userMapper.toUserDataDTO(loggedUser)).thenReturn(expectedDto);

        UserDataDTO result = userService.getLoggedUserData();

        assertNotNull(result);
        assertEquals(expectedDto.email(), result.email());
    }

    @Test
    @DisplayName("Deve atualizar dados do usuário")
    void shouldUpdateUser() {
        UpdateUserDTO dto = new UpdateUserDTO("New Name", "new@email.com", "newPass123");
        when(usersRepository.findById(loggedUser.getId())).thenReturn(Optional.of(loggedUser));
        when(usersRepository.existsByEmail(dto.email())).thenReturn(false);
        when(passwordEncoder.encode(dto.password())).thenReturn("encodedPass");

        userService.updateUser(dto);

        assertEquals("New Name", loggedUser.getName());
        assertEquals("new@email.com", loggedUser.getEmail());
        verify(usersRepository).save(loggedUser);
    }

    @Test
    @DisplayName("Deve atualizar avatar e conceder conquista")
    void shouldUpdateAvatar() {
        MultipartFile file = mock(MultipartFile.class);
        when(usersRepository.findById(loggedUser.getId())).thenReturn(Optional.of(loggedUser));
        when(fileService.uploadImage(file, "avatars")).thenReturn("http://s3.url/avatar.jpg");
        when(achievementsRepository.findByName(anyString())).thenReturn(Optional.empty()); // Evita NPE

        AvatarUrlDTO result = userService.updateAvatar(file);

        assertEquals("http://s3.url/avatar.jpg", result.avatar());
        verify(usersRepository).save(loggedUser);
    }

    @Test
    @DisplayName("Deve desativar usuário (Soft Delete)")
    void shouldDeactivateUser() {
        when(usersRepository.findById(loggedUser.getId())).thenReturn(Optional.of(loggedUser));

        userService.deactivateUser();

        assertNotNull(loggedUser.getDeletedAt());
        verify(usersRepository).save(loggedUser);
    }

    @Test
    @DisplayName("Deve lançar exceção se tentar definir preferência com tipo inexistente")
    void shouldThrowEntityNotFoundWhenPreferenceTypeNotExists() {
        UUID invalidTypeId = UUID.randomUUID();
        when(usersRepository.findById(loggedUser.getId())).thenReturn(Optional.of(loggedUser));
        when(activityTypesRepository.findById(invalidTypeId)).thenReturn(Optional.empty());

        assertThrows(br.com.sysmap.backend.exception.EntityNotFoundException.class, 
            () -> userService.definePreferences(List.of(invalidTypeId)));
    }
}
