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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final UsersRepository usersRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Cadastra novo usuário.
     * E3: E-mail e CPF devem ser únicos.
     */
    @Transactional
    public SuccessResponseDTO register(CreateUserDTO dto) {
        // E3: Mensagem exata do requisito
        if (usersRepository.existsByEmail(dto.email()) || usersRepository.existsByCpf(dto.cpf())) {
            throw new ConflictException("O e-mail ou CPF informado já pertence a outro usuário.");
        }

        Users user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));
        usersRepository.save(user);

        log.info("Novo usuário registrado: {}", dto.email());
        return new SuccessResponseDTO("Usuário cadastrado com sucesso.");
    }

    /**
     * Login com e-mail e senha.
     * E4: Usuário não encontrado.
     * E5: Senha incorreta.
     * E6: Conta desativada.
     */
    @Transactional(readOnly = true)
    public AuthDataDTO signIn(SignInDTO dto) {
        // E4: Usuário não encontrado
        Users user = usersRepository.findByEmail(dto.email())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        // E6: Conta desativada
        if (user.getDeletedAt() != null) {
            throw new BadRequestException("Esta conta foi desativada e não pode ser utilizada.");
        }

        // E5: Senha incorreta
        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new BadRequestException("Senha incorreta.");
        }

        String token = jwtService.generateToken(user);
        AuthDataDTO response = userMapper.toAuthDataDTO(user);

        return new AuthDataDTO(
                response.id(),
                response.name(),
                response.email(),
                response.avatar(),
                response.xp(),
                response.level(),
                token,
                response.achievements() != null ? response.achievements() : List.of()
        );
    }
}
