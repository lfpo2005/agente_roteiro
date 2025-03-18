package br.com.devluisoliveira.agenteroteiro.api.rest.controller;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.User;

import br.com.devluisoliveira.agenteroteiro.core.port.in.GenericGenerationPortIn;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.ContentGenerationRequest;
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

@RestController
@RequestMapping("/youtube")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "YouTube Content Generator", description = "Endpoints para geração de conteúdo para YouTube")
public class YouTubeContentController {

    private final GenericGenerationPortIn contentGenerationService;
    private final SecurityUtil securityUtil;

    @CustomOperation(summary = "Gerar conteúdo para YouTube")
    @ApiResponse(responseCode = "200", description = "Conteúdo gerado com sucesso",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ContentGenerationResponse.class))})
    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping("/generate")
    public ResponseEntity<ContentGenerationResponse> generateYouTubeContent(
            @RequestBody @Valid ContentGenerationRequest request) {

        User user = securityUtil.getLoggedInUser();

        log.info("Recebida solicitação para geração de conteúdo YouTube do usuário: {}", user.getUsername());

        ContentGenerationResponse response = contentGenerationService.generateContent(user, request);

        return ResponseEntity.ok(response);
    }

    @CustomOperation(summary = "Verificar status da geração")
    @ApiResponse(responseCode = "200", description = "Status da geração recuperado com sucesso",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ContentGenerationResponse.class))})
    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/status/{processId}")
    public ResponseEntity<ContentGenerationResponse> checkGenerationStatus(
            @PathVariable String processId) {

        // Implementar lógica para verificar o status de uma geração em andamento
        // Isso seria útil se o processo for assíncrono

        return ResponseEntity.ok(new ContentGenerationResponse()); // Implementar adequadamente
    }
}