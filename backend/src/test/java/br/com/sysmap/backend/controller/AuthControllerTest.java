package br.com.sysmap.backend.controller;

import br.com.sysmap.backend.dto.auth.AuthDataDTO;
import br.com.sysmap.backend.dto.auth.CreateUserDTO;
import br.com.sysmap.backend.dto.auth.SignInDTO;
import br.com.sysmap.backend.dto.common.SuccessResponseDTO;
import br.com.sysmap.backend.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("register deve retornar 201 Created quando o serviço tiver sucesso")
    void shouldReturnCreatedOnRegister() {
        CreateUserDTO dto = new CreateUserDTO("John Doe", "john@email.com", "12345678901", "password123");
        SuccessResponseDTO successResponse = new SuccessResponseDTO("Usuário cadastrado com sucesso.");
        
        when(authService.register(any())).thenReturn(successResponse);

        ResponseEntity<SuccessResponseDTO> response = authController.register(dto);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(successResponse.message(), response.getBody().message());
        verify(authService).register(dto);
    }

    @Test
    @DisplayName("signIn deve retornar 200 OK quando as credenciais forem válidas")
    void shouldReturnOkOnSignIn() {
        SignInDTO dto = new SignInDTO("john@email.com", "password123");
        AuthDataDTO authData = new AuthDataDTO(UUID.randomUUID(), "John", "john@email.com", null, 0, 0, "token", null);
        
        when(authService.signIn(any())).thenReturn(authData);

        ResponseEntity<AuthDataDTO> response = authController.signIn(dto);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("token", response.getBody().token());
        verify(authService).signIn(dto);
    }
}
