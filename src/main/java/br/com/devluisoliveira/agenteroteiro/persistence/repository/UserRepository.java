package br.com.devluisoliveira.agenteroteiro.persistence.repository;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);

    @EntityGraph(attributePaths = {"roles", "addresses", "phones"}, type = EntityGraph.EntityGraphType.FETCH)
    Optional<User> findByUsername(String username);

    @EntityGraph(attributePaths = {"roles", "addresses", "phones"}, type = EntityGraph.EntityGraphType.FETCH)
    Optional<User> findById(UUID userId);

    @EntityGraph(attributePaths = {"roles", "addresses", "phones"}, type = EntityGraph.EntityGraphType.FETCH)
    Page<User> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"roles", "addresses", "phones"}, type = EntityGraph.EntityGraphType.FETCH)
    Optional<Object> findByEmail(String email);
}
