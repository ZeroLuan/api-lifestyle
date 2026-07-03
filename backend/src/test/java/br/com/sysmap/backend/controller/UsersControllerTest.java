package br.com.sysmap.backend.controller;

import br.com.sysmap.backend.dto.common.SuccessResponseDTO;
import br.com.sysmap.backend.dto.user.UpdateUserDTO;
import br.com.sysmap.backend.dto.user.UserDataDTO;
import br.com.sysmap.backend.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UsersController usersController;

    @Test
    @DisplayName("getUser deve retornar dados do usuário logado")
    void shouldReturnLoggedUserData() {
        UserDataDTO dto = new UserDataDTO(UUID.randomUUID(), "John", "john@email.com", null, 0, 0, null);
        when(userService.getLoggedUserData()).thenReturn(dto);

        ResponseEntity<UserDataDTO> response = usersController.getUser();

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("John", response.getBody().name());
        verify(userService).getLoggedUserData();
    }

    @Test
    @DisplayName("updateUserData deve retornar dados atualizados")
    void shouldUpdateUserData() {
        UpdateUserDTO dto = new UpdateUserDTO("New Name", "new@email.com", "pass123");
        UserDataDTO updatedDto = new UserDataDTO(UUID.randomUUID(), "New Name", "new@email.com", null, 0, 0, null);
        
        when(userService.updateUser(any())).thenReturn(updatedDto);

        ResponseEntity<UserDataDTO> response = usersController.updateUserData(dto);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("New Name", response.getBody().name());
        verify(userService).updateUser(dto);
    }

    @Test
    @DisplayName("deleteUser deve desativar o usuário e retornar sucesso")
    void shouldDeactivateUser() {
        SuccessResponseDTO success = new SuccessResponseDTO("Conta desativada");
        when(userService.deactivateUser()).thenReturn(success);

        ResponseEntity<SuccessResponseDTO> response = usersController.deleteUser();

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Conta desativada", response.getBody().message());
        verify(userService).deactivateUser();
    }

    @Test
    @DisplayName("updateUserPreferences deve retornar sucesso")
    void shouldUpdatePreferences() {
        List<UUID> typeIds = List.of(UUID.randomUUID());
        SuccessResponseDTO success = new SuccessResponseDTO("Sucesso");
        when(userService.definePreferences(any())).thenReturn(success);

        ResponseEntity<SuccessResponseDTO> response = usersController.updateUserPreferences(typeIds);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).definePreferences(typeIds);
    }
}
