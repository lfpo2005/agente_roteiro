package br.com.devluisoliveira.agenteroteiro.core.application.service;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.Role;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.enums.RoleType;
import br.com.devluisoliveira.agenteroteiro.core.port.in.RolePortIn;
import br.com.devluisoliveira.agenteroteiro.persistence.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService implements RolePortIn {

    @Autowired
    RoleRepository roleRepository;

    @Override
    public Optional<Role> findByRoleName(RoleType roleType) {
        return roleRepository.findByRoleName(roleType);
    }
}
