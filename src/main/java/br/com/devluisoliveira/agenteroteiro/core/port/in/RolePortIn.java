package br.com.devluisoliveira.agenteroteiro.core.port.in;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.Role;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.enums.RoleType;

import java.util.Optional;

public interface RolePortIn {
    Optional<Role> findByRoleName(RoleType roleType);
}
