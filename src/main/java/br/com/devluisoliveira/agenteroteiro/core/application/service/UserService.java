package br.com.devluisoliveira.agenteroteiro.core.application.service;

import br.com.devluisoliveira.agenteroteiro.core.application.mapper.UserMapper;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.Role;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.User;
import br.com.devluisoliveira.agenteroteiro.core.domain.entity.enums.RoleType;
import br.com.devluisoliveira.agenteroteiro.core.port.in.RolePortIn;
import br.com.devluisoliveira.agenteroteiro.core.port.in.UserPortIn;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.UserRequestDto;
import br.com.devluisoliveira.agenteroteiro.core.port.out.UserPortOut;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.UserResponseDto;
import br.com.devluisoliveira.agenteroteiro.shared.utils.CryptoUtils;
import br.com.devluisoliveira.agenteroteiro.shared.utils.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.spec.InvalidParameterSpecException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserPortIn {

    private final UserPortOut userPortOut;
    private final RolePortIn roleService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;


    @Override
    public void registerUser(UserRequestDto userRequestDto) throws InvalidParameterSpecException {
        log.info("[UserService.registerUser] - Iniciando a criação de um novo usuario");
            validatorUser(userRequestDto);

        Role role = roleService.findByRoleName(RoleType.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role não encontrado."));
        log.info("[UserService.registerUser] - Role encontrada: {}", role.getRoleName());
        User user = userMapper.userRequestDtoToUser(userRequestDto);

        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        user.getRoles().add(role);
        log.info("[UserService.registerUser] - Role adicionada ao usuario: {}", role.getRoleName());


        log.info("[UserService.registerUser] - Salvando o usuario no banco de dados");
        User userSaved = userPortOut.save(user);
        log.info("[UserService.registerUser] - Usuario salvo com sucesso: {}", userSaved.getUsername());
    }

    @Override
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        log.info("[UserService.findAll] - Iniciando a busca de todos os usuário");

        Page<User> users = userPortOut.findAll(pageable);
        log.info("[UserService.findAll] - Busca concluída. Total de usuário: {}", users.getTotalElements());

        return userMapper.userToUserResponseDtoPage(users);
    }

    @Override
    public Optional<UserResponseDto> getOneUser(UUID userId) {
        log.info("[UserService.getOneUser] - Iniciando a busca de um usuário pelo ID: {}", userId);

        User user = userPortOut.getOneUser(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        return Optional.ofNullable(userMapper.userToUserResponseDto(user));

    }

    @Override
    public Optional<UserResponseDto> getFindByEmail(String email) {
        log.info("[UserService.getFindByEmail] - Iniciando a busca de um usuário pelo ID: {}", email);

        User user = (User) userPortOut.getFindByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Email não encontrado"));

        return Optional.ofNullable(userMapper.userToUserResponseDto(user));
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return userPortOut.getOneUser(userId);
    }

    @Override
    public void save(User loggedInUser) {
        userPortOut.save(loggedInUser);
    }


    private void validatorUser(UserRequestDto userRequestDto) throws InvalidParameterSpecException {
        if (userPortOut.existsByUsername(userRequestDto.getUsername())) {
            log.warn("Nome de usuario ja existente!: {} ------> ", userRequestDto.getUsername());
            throw new InvalidParameterSpecException(String.format("Error: Nome de usuario ja existente!"));
        }
        if (userPortOut.existsByEmail(userRequestDto.getEmail())) {
            log.warn("Email {} ja existente!!: ------> ", userRequestDto.getEmail());
            throw new InvalidParameterSpecException(String.format("Error: Email ja existente!!"));
        }

    }

}
