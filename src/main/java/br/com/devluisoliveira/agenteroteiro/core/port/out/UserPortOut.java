package br.com.devluisoliveira.agenteroteiro.core.port.out;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface UserPortOut {
    
    Page<User> findAll(Pageable pageable);
    User save(User user);
    boolean existsByCpf(String cpf);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Optional<User> getOneUser(UUID userId);

    Optional<Object> getFindByEmail(String email);

}
