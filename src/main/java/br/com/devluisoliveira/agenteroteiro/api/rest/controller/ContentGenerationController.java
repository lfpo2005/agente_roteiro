package br.com.devluisoliveira.agenteroteiro.api.rest.controller;

import br.com.devluisoliveira.agenteroteiro.core.domain.entity.User;
import br.com.devluisoliveira.agenteroteiro.core.port.in.AgentGenerationPortIn;
import br.com.devluisoliveira.agenteroteiro.core.port.in.dto.ContentGenerationRequest;
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

    @PostMapping("/agent/generate")
    public ResponseEntity<GenerationResponseDto> startGeneration(@RequestBody @Valid ContentGenerationRequest request) {
        User user = securityUtil.getLoggedInUser();

        String processId = UUID.randomUUID().toString();
        request.setProcessId(processId);

       log.info("[CONTROLLER_AGENT_GENERIC] - Iniciando processo de geração com ID: {}, (idioma: {}, título: {}, notas: {}, gerarVersaoShort: {}, gerarAudio: {})",
                          request.getProcessId(),
                          request.getLanguage(),
                          request.getTitle(),
                          request.getNotes(),
                          request.getGenerateShortVersion(),
                          request.getGenerateAudio());

        GenerationResponseDto generationResponse = agentGenerationPortIn.initializeAgentGeneric(user, request);

        log.info("[CONTROLLER_AGENT_GENERIC] - Processo de geração finalizado com sucesso: {}", generationResponse);

        // Retorne a resposta apropriada aqui analisando a resposta do serviço ainda
        return ResponseEntity.ok(new GenerationResponseDto());
    }
}