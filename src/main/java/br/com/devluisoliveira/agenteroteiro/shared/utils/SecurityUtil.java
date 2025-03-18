package br.com.devluisoliveira.agenteroteiro.shared.utils;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.User;
import br.com.devluisoliveira.agenteroteiro.core.port.out.UserPortOut;
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
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userPortOut.getOneUser(principal.getId()).orElse(null);
    }
}