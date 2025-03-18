package br.com.devluisoliveira.agenteroteiro.persistence.repository;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.Role;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByRoleName(RoleType name);
}
