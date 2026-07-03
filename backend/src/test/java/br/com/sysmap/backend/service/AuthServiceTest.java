package br.com.sysmap.backend.service;

import br.com.sysmap.backend.dto.auth.AuthDataDTO;
import br.com.sysmap.backend.dto.auth.CreateUserDTO;
import br.com.sysmap.backend.dto.auth.SignInDTO;
import br.com.sysmap.backend.dto.common.SuccessResponseDTO;
import br.com.sysmap.backend.entity.Users;
import br.com.sysmap.backend.exception.BadRequestException;
import br.com.sysmap.backend.exception.ConflictException;
import br.com.sysmap.backend.exception.EntityNotFoundException;
import br.com.sysmap.backend.mapper.UserMapper;
import br.com.sysmap.backend.repository.UsersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Nested
    @DisplayName("Testes de Registro de Usuário")
    class RegisterTests {

        @Test
        @DisplayName("Deve registrar um usuário com sucesso")
        void shouldRegisterUserSuccessfully() {
            CreateUserDTO dto = new CreateUserDTO("John Doe", "john@example.com", "12345678901", "password123");
            Users user = new Users();

            when(usersRepository.existsByEmail(dto.email())).thenReturn(false);
            when(usersRepository.existsByCpf(dto.cpf())).thenReturn(false);
            when(userMapper.toEntity(dto)).thenReturn(user);
            when(passwordEncoder.encode(dto.password())).thenReturn("encodedPassword");

            SuccessResponseDTO response = authService.register(dto);

            assertNotNull(response);
            assertEquals("Usuário cadastrado com sucesso.", response.message());
            verify(usersRepository).save(user);
        }

        @Test
        @DisplayName("Deve lançar ConflictException se e-mail já existir")
        void shouldThrowConflictExceptionWhenEmailExists() {
            CreateUserDTO dto = new CreateUserDTO("John Doe", "john@example.com", "12345678901", "password123");

            when(usersRepository.existsByEmail(dto.email())).thenReturn(true);

            ConflictException exception = assertThrows(ConflictException.class, () -> authService.register(dto));
            assertEquals("O e-mail ou CPF informado já pertence a outro usuário.", exception.getMessage());
            verify(usersRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar ConflictException se CPF já existir")
        void shouldThrowConflictExceptionWhenCpfExists() {
            CreateUserDTO dto = new CreateUserDTO("John Doe", "john@example.com", "12345678901", "password123");

            when(usersRepository.existsByEmail(dto.email())).thenReturn(false);
            when(usersRepository.existsByCpf(dto.cpf())).thenReturn(true);

            ConflictException exception = assertThrows(ConflictException.class, () -> authService.register(dto));
            assertEquals("O e-mail ou CPF informado já pertence a outro usuário.", exception.getMessage());
            verify(usersRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Testes de Login (Sign-In)")
    class SignInTests {

        @Test
        @DisplayName("Deve fazer login com sucesso")
        void shouldSignInSuccessfully() {
            SignInDTO dto = new SignInDTO("john@example.com", "password123");
            Users user = mock(Users.class);
            AuthDataDTO authDataDTO = new AuthDataDTO(UUID.randomUUID(), "John", "john@example.com", null, 0, 0, null, null);

            when(usersRepository.findByEmail(dto.email())).thenReturn(Optional.of(user));
            when(user.getDeletedAt()).thenReturn(null);
            when(passwordEncoder.matches(dto.password(), user.getPassword())).thenReturn(true);
            when(jwtService.generateToken(user)).thenReturn("tokenJWT");
            when(userMapper.toAuthDataDTO(user)).thenReturn(authDataDTO);

            AuthDataDTO response = authService.signIn(dto);

            assertNotNull(response);
            assertEquals("tokenJWT", response.token());
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException se usuário não existir")
        void shouldThrowExceptionWhenUserNotFound() {
            SignInDTO dto = new SignInDTO("notfound@example.com", "password123");

            when(usersRepository.findByEmail(dto.email())).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> authService.signIn(dto));
            assertEquals("Usuário não encontrado.", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se conta estiver desativada")
        void shouldThrowExceptionWhenAccountDeactivated() {
            SignInDTO dto = new SignInDTO("deactivated@example.com", "password123");
            Users user = mock(Users.class);

            when(usersRepository.findByEmail(dto.email())).thenReturn(Optional.of(user));
            when(user.getDeletedAt()).thenReturn(LocalDateTime.now());

            BadRequestException exception = assertThrows(BadRequestException.class, () -> authService.signIn(dto));
            assertEquals("Esta conta foi desativada e não pode ser utilizada.", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar BadRequestException se senha estiver incorreta")
        void shouldThrowExceptionWhenPasswordIncorrect() {
            SignInDTO dto = new SignInDTO("john@example.com", "wrongpassword");
            Users user = mock(Users.class);

            when(usersRepository.findByEmail(dto.email())).thenReturn(Optional.of(user));
            when(user.getDeletedAt()).thenReturn(null);
            when(passwordEncoder.matches(dto.password(), user.getPassword())).thenReturn(false);

            BadRequestException exception = assertThrows(BadRequestException.class, () -> authService.signIn(dto));
            assertEquals("Senha incorreta.", exception.getMessage());
        }
    }
}
