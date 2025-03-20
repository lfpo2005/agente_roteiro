package br.com.devluisoliveira.agenteroteiro.shared.utils;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.User;
import br.com.devluisoliveira.agenteroteiro.core.port.out.UserPortOut;
import br.com.devluisoliveira.agenteroteiro.shared.configs.security.UserDetailsImpl;
import br.com.devluisoliveira.agenteroteiro.shared.configs.security.UserPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    private final UserPortOut userPortOut;

    public SecurityUtil(UserPortOut userPortOut) {
        this.userPortOut = userPortOut;
    }

    public User getLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserPrincipal) {
            return userPortOut.getOneUser(((UserPrincipal) principal).getId()).orElse(null);
        } else if (principal instanceof UserDetailsImpl) {
            return userPortOut.getOneUser(((UserDetailsImpl) principal).getUserId()).orElse(null);
        } else {
            throw new ClassCastException("Tipo de usuário não suportado: " + principal.getClass().getName());
        }
    }

}