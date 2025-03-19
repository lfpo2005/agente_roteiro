package br.com.devluisoliveira.agenteroteiro.api.rest.controller;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.User;
import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.PhilosopherType;

import br.com.devluisoliveira.agenteroteiro.core.port.in.StoicContentPortIn;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.StoicContentGenerationRequest;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.ContentGenerationResponse;
import br.com.devluisoliveira.agenteroteiro.shared.utils.SecurityUtil;
import br.com.devluisoliveira.agenteroteiro.shared.validation.CustomOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/stoic")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Stoic Content Generator", description = "Endpoints para geração de conteúdo sobre filosofia estoica")
public class StoicContentController {

    private final StoicContentPortIn stoicContentPortIn;
    private final SecurityUtil securityUtil;

    @CustomOperation(summary = "Gerar conteúdo de filosofia estoica")
    @ApiResponse(responseCode = "200", description = "Conteúdo gerado com sucesso",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ContentGenerationResponse.class))})
    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping("/generate")
    public ResponseEntity<ContentGenerationResponse> generateStoicContent(
            @RequestBody @Valid StoicContentGenerationRequest request) {
        User user = securityUtil.getLoggedInUser();

        if (request.getProcessId() == null || request.getProcessId().trim().isEmpty()) {
            request.setProcessId(UUID.randomUUID().toString());
        }

        log.info("Recebida solicitação para geração de conteúdo estoico do usuário: {}, filósofo: {}," +
                        " tema: {}",
                user.getUsername(), request.getPhilosopherName(), request.getTheme());

        ContentGenerationResponse response = stoicContentPortIn.generateContent(user, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/philosophers")
    public ResponseEntity<List<PhilosopherType>> getAllPhilosophers() {
        return ResponseEntity.ok(Arrays.asList(PhilosopherType.values()));
    }

}
