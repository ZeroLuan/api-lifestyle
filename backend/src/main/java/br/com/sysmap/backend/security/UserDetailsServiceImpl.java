package br.com.sysmap.backend.security;

import br.com.sysmap.backend.entity.Users;
import br.com.sysmap.backend.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = usersRepository
                .findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        // E6: Conta desativada não pode usar nenhum endpoint protegido
        if (user.getDeletedAt() != null) {
            throw new DisabledException("Esta conta foi desativada e não pode ser utilizada.");
        }

        return new AuthenticatedUser(user);
    }
}
