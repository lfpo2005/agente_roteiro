package br.com.devluisoliveira.agenteroteiro.api.rest.controller;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.User;
import br.com.devluisoliveira.agenteroteiro.core.port.AgentGenerationPortIn;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.GenerationRequestDto;
import br.com.devluisoliveira.agenteroteiro.core.port.out.response.GenerationResponseDto;
import br.com.devluisoliveira.agenteroteiro.shared.utils.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class ContentGenerationController {

    private final AgentGenerationPortIn agentGenerationPortIn;
    private final SecurityUtil securityUtil;

    @PostMapping("/agent/prayer/generate")
    public ResponseEntity<GenerationResponseDto> startGeneration(@RequestBody @Valid GenerationRequestDto request) {
        User user = securityUtil.getLoggedInUser();

        String processId = UUID.randomUUID().toString();
        request.setProcessId(processId);

        log.info("[CONTROLLER_AGENT_PRAYER] - Iniciando o Agente de Oração com ID {} (idioma: {}, estilo de oração: {}," +
                        " tipo de oração: {}, duração: {}, título: {}, notas: {}, versão curta: {}, áudio: {})",
                 request.getProcessId(),
                 request.getLanguage() != null ? request.getGenerateAudio() : "es_MX (padrão)",
                 request.getPrayerStyle(),
                 request.getPrayerType(),
                 request.getDuration(),
                 request.getTitle(),
                 request.getNotes(),
                 request.getGenerateShortVersion(),
                 request.getGenerateAudio());

        GenerationResponseDto generationResponse = agentGenerationPortIn.initializeAgentPrayer(user, request);

        log.info("[CONTROLLER_AGENT_PRAYER] - Processo de geração iniciado com sucesso para o Agente de Oração" +
                " com ID {}", request.getProcessId());

        // Retorne a resposta apropriada aqui
        return ResponseEntity.ok(new GenerationResponseDto());
    }
}