package br.com.devluisoliveira.agenteroteiro.persistence;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.User;
import br.com.devluisoliveira.agenteroteiro.core.port.out.UserPortOut;
import br.com.devluisoliveira.agenteroteiro.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserIntegrator implements UserPortOut {

    private final UserRepository userRepository;

    @Override
    public Optional<User> getOneUser(UUID userId) {
        log.info("[UserIntegrator.findById] - Iniciando busca de usuário por id");

        try {
            return userRepository.findById(userId);
        } catch (Exception e) {
            log.error("[UserIntegrator.findById] - erro ao buscar usuário por id", e);
            throw new RuntimeException("Erro ao buscar usuário por id: " + e.getMessage());
        }
    }

    @Override
    public Optional<Object> getFindByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        log.info("[UserIntegrator.findAll] - Iniciando busca de todos os usuários");

        try {
            Page<User> userPage = userRepository.findAll(pageable);
            return userPage;
        } catch (Exception e) {
            log.error("[UserIntegrator.findAll] - erro ao buscar todos os usuários", e);
            throw new RuntimeException("Erro ao buscar todos os usuários"+ e.getMessage());
        }

    }

    @Override
    public boolean existsByCpf(String cpf) {

        return userRepository.existsByCpf(cpf);
    }

    @Override
    public User save(User user) {
        log.info("[UserIntegrator.save] - salvando usuário");
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            log.error("[UserIntegrator.save] - erro ao salvar usuário", e);
            throw new RuntimeException("Erro ao salvar usuário"+ e.getMessage());
        }
    }


    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}

