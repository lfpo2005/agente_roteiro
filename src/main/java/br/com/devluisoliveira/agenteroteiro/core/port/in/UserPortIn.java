package br.com.devluisoliveira.agenteroteiro.core.port.in;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.User;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.UserRequestDto;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.UserResponseDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.spec.InvalidParameterSpecException;
import java.util.Optional;
import java.util.UUID;

public interface UserPortIn {

    Page<UserResponseDto> getAllUsers(Pageable pageable);

    void registerUser(@Valid UserRequestDto userRequestDto) throws InvalidParameterSpecException;



    Optional<User> findById(UUID userId);

    void save(User loggedInUser);

    Optional<UserResponseDto> getOneUser(UUID userId);

    Optional<UserResponseDto> getFindByEmail(String email);
}
