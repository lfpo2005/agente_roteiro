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

import java.util.UUID;

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

        log.info("Verificando status da geração para processId: {}", processId);

        try {
            User user = securityUtil.getLoggedInUser();

            ContentGenerationResponse serviceResponse = contentGenerationService.checkGenerationStatus(processId, user);

            ContentGenerationResponse response = ContentGenerationResponse.builder()
                    .processId(serviceResponse.getProcessId())
                    .status(serviceResponse.getStatus())
                    .message(serviceResponse.getMessage())
                    .title(serviceResponse.getTitle())
                    .build();

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("UUID inválido: {}", processId, e);

            ContentGenerationResponse errorResponse = ContentGenerationResponse.builder()
                    .processId(null)
                    .status("ERROR")
                    .message("ID de processo inválido: " + e.getMessage())
                    .build();

            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            log.error("Erro ao verificar status da geração: {}", processId, e);

            ContentGenerationResponse errorResponse = ContentGenerationResponse.builder()
                    .processId(null)
                    .status("ERROR")
                    .message("Erro ao verificar status: " + e.getMessage())
                    .build();

            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}