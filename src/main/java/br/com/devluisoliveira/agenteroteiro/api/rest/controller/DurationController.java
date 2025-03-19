package br.com.devluisoliveira.agenteroteiro.api.rest.controller;

import br.com.devluisoliveira.agenteroteiro.core.application.service.enums.DurationType;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.dto.DurationOptionDto;
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
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller para fornecer opções de duração para o frontend
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Opções de Duração", description = "Endpoints para obter opções de duração para roteiros")
@RequestMapping("/public/duration-options")
public class DurationController {

    @Operation(summary = "Listar todas as opções de duração")
    @GetMapping
    public ResponseEntity<List<DurationOptionDto>> getAllDurationOptions() {
        log.debug("[DurationController.getAllDurationOptions] - Listando todas as opções de duração");

        List<DurationOptionDto> options = Arrays.stream(DurationType.values())
                .map(this::mapToDurationOptionDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(options);
    }

    @Operation(summary = "Listar opções de duração agrupadas por categoria")
    @GetMapping("/by-category")
    public ResponseEntity<Map<String, List<DurationOptionDto>>> getDurationOptionsByCategory() {
        log.debug("[DurationController.getDurationOptionsByCategory] - Listando opções de duração por categoria");

        Map<String, List<DurationOptionDto>> optionsByCategory = Arrays.stream(DurationType.values())
                .map(this::mapToDurationOptionDto)
                .collect(Collectors.groupingBy(DurationOptionDto::getCategory));

        return ResponseEntity.ok(optionsByCategory);
    }

    /**
     * Mapeia um enum DurationType para um DTO
     */
    private DurationOptionDto mapToDurationOptionDto(DurationType durationType) {
        return DurationOptionDto.builder()
                .name(durationType.name())
                .displayName(durationType.getDisplayName())
                .durationInSeconds(durationType.getDurationInSeconds())
                .durationInMinutes(durationType.getDurationInMinutes())
                .category(durationType.getCategory())
                .estimatedWordCount(durationType.getEstimatedWordCount())
                .estimatedCharacterCount(durationType.getEstimatedCharacterCount())
                .build();
    }
}
