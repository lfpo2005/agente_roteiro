package br.com.devluisoliveira.agenteroteiro.api.rest.controller;

import br.com.devluisoliveira.agenteroteiro.core.port.in.UserPortIn;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.JwtDto;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.LoginDto;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.UserRequestDto;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.UserResponseDto;
import br.com.devluisoliveira.agenteroteiro.shared.configs.security.JwtProvider;
import br.com.devluisoliveira.agenteroteiro.shared.validation.CustomOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.spec.InvalidParameterSpecException;

@RestController
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/public/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para criação e gerenciamento de acessos")
public class AuthenticationController {

        private final UserPortIn userPortIn;
        private final JwtProvider jwtProvider;
        private final AuthenticationManager authenticationManager;

        @CustomOperation(summary = "Post para criar um novo usuário")
        @ApiResponse(responseCode = "201", description = "Endpoint cria um novo usuário", content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class)) })
        @PostMapping("/signup")
        public ResponseEntity<Object> registerUser(@RequestBody @Valid UserRequestDto userRequestDto) {
                log.info("[AuthenticationController.registerUser] - Iniciando a criação de um novo usuario ------> {}",
                                userRequestDto.toString());

                try {
                        // UserResponseDto savedUser = userPortIn.registerUser(userRequestDto);
                        userPortIn.registerUser(userRequestDto);
                        // log.info("[AuthenticationController.registerUser] - Usuário criado com
                        // sucesso! {}", savedUser.toString());
                        log.info("[AuthenticationController.registerUser] - Usuário criado com sucesso! {}",
                                        userRequestDto.getUsername());

                        return ResponseEntity.status(HttpStatus.CREATED).body("Usuário criado com sucesso!");
                } catch (InvalidParameterSpecException e) {
                        log.warn("[AuthenticationController.registerUser] - Erro de validação: ", e);
                        throw new RuntimeException(e.getMessage());
                }
        }

        @CustomOperation(summary = "Post para autenticar usuário")
        @ApiResponse(responseCode = "200", description = "Endpoint para autenticar usuário", content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = JwtDto.class)) })
        @PostMapping("/login")
        public ResponseEntity<JwtDto> authenticateUser(@Valid @RequestBody LoginDto loginDto) {
                log.info("[AuthenticationController.authenticateUser] - Iniciando autenticação do usuário ------> {}",
                                loginDto.toString());
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(loginDto.getUsername(),
                                                loginDto.getPassword()));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("[AuthenticationController.authenticateUser] - Usuário autenticado com sucesso! {}",
                                loginDto.toString());
                String jwt = jwtProvider.generateJwt(authentication);
                log.info("[AuthenticationController.authenticateUser] - JWT gerado com sucesso! {}", jwt);
                return ResponseEntity.ok(new JwtDto(jwt));
        }
        //
        // @GetMapping("/logout")
        // public ResponseEntity logout(HttpServletRequest request, HttpServletResponse
        // response) {
        // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // if (auth != null) {
        // new SecurityContextLogoutHandler().logout(request, response, auth);
        // }
        // log.info("User logged out: {}", auth.getName());
        // return new ResponseEntity(HttpStatus.OK);
        // }

        // @GetMapping("/")
        // public String index(){
        // log.trace("TRACE");
        // log.debug("DEBUG");
        // log.info("INFO");
        // log.warn("WARN");
        // log.error("ERROR");
        // return "Logging Spring Boot...";
        // }

}
