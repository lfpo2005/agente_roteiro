package br.com.devluisoliveira.agenteroteiro.api.rest.controller;

import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.PrayerStyle;
import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.PrayerType;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.PrayerOptionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller para fornecer opções de tipos e estilos de oração
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Opções de Oração", description = "Endpoints para obter opções de tipos e estilos de oração")
@RequestMapping("/public/prayer-options")
public class PrayerOptionsController {

    @Operation(summary = "Listar todos os tipos de oração")
    @GetMapping("/types")
    public ResponseEntity<List<PrayerOptionDto>> getAllPrayerTypes() {
        log.debug("[PrayerOptionsController.getAllPrayerTypes] - Listando todos os tipos de oração");

        List<PrayerOptionDto> types = Arrays.stream(PrayerType.values())
                .map(this::mapToPrayerTypeDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(types);
    }

    @Operation(summary = "Listar todos os estilos de oração")
    @GetMapping("/styles")
    public ResponseEntity<List<PrayerOptionDto>> getAllPrayerStyles() {
        log.debug("[PrayerOptionsController.getAllPrayerStyles] - Listando todos os estilos de oração");

        List<PrayerOptionDto> styles = Arrays.stream(PrayerStyle.values())
                .map(this::mapToPrayerStyleDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(styles);
    }

    /**
     * Mapeia um enum PrayerType para um DTO
     */
    private PrayerOptionDto mapToPrayerTypeDto(PrayerType prayerType) {
        return PrayerOptionDto.builder()
                .name(prayerType.name())
                .displayName(prayerType.getDisplayName())
                .description(prayerType.getDescription())
                .keywords(prayerType.getKeywords())
                .optionType("PRAYER_TYPE")
                .build();
    }

    /**
     * Mapeia um enum PrayerStyle para um DTO
     */
    private PrayerOptionDto mapToPrayerStyleDto(PrayerStyle prayerStyle) {
        return PrayerOptionDto.builder()
                .name(prayerStyle.name())
                .displayName(prayerStyle.getDisplayName())
                .description(prayerStyle.getDescription())
                .keywords(prayerStyle.getKeywords())
                .optionType("PRAYER_STYLE")
                .build();
    }
}