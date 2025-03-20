package br.com.devluisoliveira.agenteroteiro.api.rest.controller;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.User;
import br.com.devluisoliveira.agenteroteiro.core.application.service.PrayerContentService;
import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.PrayerStyle;
import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.PrayerType;
import br.com.devluisoliveira.agenteroteiro.core.port.in.PrayerContentPortIn;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.PrayerContentGenerationRequest;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.ContentGenerationResponse;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.PrayerOptionDto;
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
@RequestMapping("/prayer")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Prayer Content Generator", description = "Endpoints para geração de conteúdo de orações")
public class PrayerContentController {

    private final PrayerContentPortIn prayerContentPortIn;
    private final SecurityUtil securityUtil;

    @CustomOperation(summary = "Gerar conteúdo de oração")
    @ApiResponse(responseCode = "200", description = "Conteúdo gerado com sucesso",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ContentGenerationResponse.class))})
    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping("/generate")
    public ResponseEntity<ContentGenerationResponse> generatePrayerContent(
            @RequestBody @Valid PrayerContentGenerationRequest request) {
        User user = securityUtil.getLoggedInUser();

        if (request.getProcessId() == null || request.getProcessId().trim().isEmpty()) {
            request.setProcessId(UUID.randomUUID().toString());
        }

        log.info("Recebida solicitação para geração de oração do usuário: {}, tema: {}, estilo: {}",
                user.getUsername(), request.getTheme(), request.getPrayerStyle());

        ContentGenerationResponse response = prayerContentPortIn.generateContent(user, request);

        return ResponseEntity.ok(response);
    }

    @CustomOperation(summary = "Gerar versão curta de uma oração existente")
    @ApiResponse(responseCode = "200", description = "Versão curta gerada com sucesso",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ContentGenerationResponse.class))})
    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping("/{contentId}/short-version")
    public ResponseEntity<ContentGenerationResponse> generateShortVersion(
            @PathVariable UUID contentId) {
        User user = securityUtil.getLoggedInUser();

        log.info("Recebida solicitação para geração de versão curta da oração ID: {}", contentId);

        ContentGenerationResponse response = prayerContentPortIn.generateShortVersion(user, contentId);

        return ResponseEntity.ok(response);
    }

    @CustomOperation(summary = "Gerar rotina de oração personalizada")
    @ApiResponse(responseCode = "200", description = "Rotina de oração gerada com sucesso",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ContentGenerationResponse.class))})
    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping("/routine")
    public ResponseEntity<ContentGenerationResponse> generatePrayerRoutine(
            @RequestParam(required = false, defaultValue = "Christian") String religiousTradition,
            @RequestParam(required = false) String denomination,
            @RequestParam(required = false, defaultValue = "15") Integer durationMinutes,
            @RequestParam(required = false) String timeOfDay,
            @RequestParam(required = false) String intentions,
            @RequestParam(required = false, defaultValue = "pt_BR") String language) {

        User user = securityUtil.getLoggedInUser();

        log.info("Recebida solicitação para geração de rotina de oração: tradição: {}, duração: {} minutos",
                religiousTradition, durationMinutes);

        ContentGenerationResponse response = prayerContentPortIn.generatePrayerRoutine(
                user, religiousTradition, durationMinutes, timeOfDay, intentions, language);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/types")
    public ResponseEntity<List<PrayerType>> getAllPrayerTypes() {
        return ResponseEntity.ok(Arrays.asList(PrayerType.values()));
    }

    @GetMapping("/styles")
    public ResponseEntity<List<PrayerStyle>> getAllPrayerStyles() {
        return ResponseEntity.ok(Arrays.asList(PrayerStyle.values()));
    }
}