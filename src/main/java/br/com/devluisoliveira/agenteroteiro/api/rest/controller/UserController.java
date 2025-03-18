package br.com.devluisoliveira.agenteroteiro.api.rest.controller;

import br.com.devluisoliveira.agenteroteiro.core.port.in.UserPortIn;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.UserResponseDto;
import br.com.devluisoliveira.agenteroteiro.shared.configs.security.AuthenticationCurrentUserService;
import br.com.devluisoliveira.agenteroteiro.shared.validation.CustomOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "User Controller", description = "Endpoints para gerenciar de usuário")
@RequestMapping("/users")
public class UserController {

    private final UserPortIn userPortIn;

    private final AuthenticationCurrentUserService authenticationCurrentUserService;

    @CustomOperation(summary = "Get para buscar todos os usuários")
    @ApiResponse(responseCode = "201", description = "Endpoint retorna informações de todos os usuários",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDto.class))})
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(@PageableDefault(page = 0, size = 10, sort = "fullName",
            direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("[UserController.getAllUsers] - Iniciando a busca de todos os usuário");

        Page<UserResponseDto> userModelPage = userPortIn.getAllUsers(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(userModelPage);
    }

    @CustomOperation(summary = "Get para buscar um usuário")
    @ApiResponse(responseCode = "200", description = "Endpoint para buscar dados de um usuário",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDto.class))})
    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/{userId}")
    public ResponseEntity<Object> getOneUser(@PathVariable(value = "userId") UUID userId) {
        log.info("[UserController.getOneUser] - Iniciando a busca de usuário por id: {}", userId);
        UUID currentUserId = authenticationCurrentUserService.getCurrentUser().getUserId();
        log.info("Usuario recuperado: {}", currentUserId);

        if (currentUserId.equals(userId)) {
            Optional<UserResponseDto> userModelOptional = userPortIn.getOneUser(userId);
            if (!userModelOptional.isPresent()) {
                log.warn("[UserController.getOneUser] - Usuario não encontrado no sistema....");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario não encontrado no sistema.");
            } else {
                log.info("[UserController.getOneUser] - Usuario econtrado no sistema....");
                return ResponseEntity.status(HttpStatus.OK).body(userModelOptional.get());
            }
        } else {
            throw new AccessDeniedException("Forbidden");
        }
    }

    @CustomOperation(summary = "Get para buscar um usuário por email")
    @ApiResponse(responseCode = "200", description = "Endpoint para buscar dados de um usuário por email",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDto.class))})
    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/find_email")
    public ResponseEntity<Object> getFindByEmail(@RequestParam String email) {
        log.info("[UserController.getFindByEmail] - Iniciando a busca de usuário por email: {}", email);
        Optional<UserResponseDto> userModelOptional = userPortIn.getFindByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(userModelOptional.get());
    }
}

